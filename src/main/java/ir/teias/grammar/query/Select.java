package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.grammar.predicate.Predicate;
import ir.teias.grammar.value.Column;
import ir.teias.grammar.value.Const;
import ir.teias.grammar.value.Value;
import ir.teias.model.BitVector;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Select extends QueryWithPredicate {
    private final Query query;

    public Select(Query query, Predicate predicate) {
        super(predicate);
        this.query = query;
    }

    @Override
    public String toString() {
        String queryString = query.toString();
        if (!(query instanceof NamedTable)) {
            queryString = "(" + queryString + ") AS " + query.getQueryName();
        }
        return "SELECT * FROM " + queryString + " WHERE " + predicate;
    }

    @Override
    public Table evaluateAbstract() {
        if (SQLManager.isTableExists(getQueryName())) {
            return SQLManager.evaluate("SELECT * FROM " + getQueryName(), getQueryName());
        }
        Table queryTable = query.evaluateAbstract();
        queryTable.saveToDb();

        String dbQuery = "SELECT * FROM " + queryTable.getName() + " WHERE " + predicate.toString();
        return SQLManager.evaluate(dbQuery, getQueryName());
    }

    @Override
    public List<BitVector> bitVectorDFS() {
        List<Predicate> predicates = enumAndGroupPredicates();
        List<BitVector> bitVectors = encodeFiltersToBitVectors(predicates);
        List<BitVector> bitVectorsQuery = query.bitVectorDFS();

        List<BitVector> res = new ArrayList<>();
        for (BitVector bv : bitVectors) {
            for (BitVector bvq : bitVectorsQuery) {
                Select newSelect = new Select(bvq.getQuery(), ((QueryWithPredicate) bv.getQuery()).getPredicate());
                res.add(new BitVector(bv.and(bvq.getVector()), newSelect, getAbstractTable()));
            }
        }
        return filterEquivalentBitVectors(res);
    }

    @Override
    public QueryWithPredicate duplicateWithNewPredicate(Predicate predicate) {
        return new Select(query, predicate);
    }

    @Override
    public List<Predicate> enumeratePrimitivePredicates() {
        HashMap<CellType, List<String>> columnsByType = getColumnsByType();
        List<String> idColumns = getIdColumns();

        List<Predicate> primitivesPredicates = new ArrayList<>();

        for (var entry : columnsByType.entrySet()) {
            List<String> columns = entry.getValue();
            List<Value> columnValues = columns.stream().map(col -> new Column(col, query.getQueryName())).collect(Collectors.toList());

            List<Cell<?>> constantsWithSameType = constantsByType.get(entry.getKey());
            List<Value> constantValues = constantsWithSameType.stream().map(Const::new).collect(Collectors.toList());

            primitivesPredicates.addAll(enumerateBinOpPredicates(columnValues, columnValues, true, false));
            primitivesPredicates.addAll(enumerateBinOpPredicates(columnValues, constantValues, false, false));
        }
        return primitivesPredicates;
    }

    @Override
    public void setConstantsByType(HashMap<CellType, List<Cell<?>>> constantsByType) {
        this.constantsByType = constantsByType;
        if (query instanceof QueryWithPredicate) {
            ((QueryWithPredicate) query).setConstantsByType(constantsByType);
        }
    }
}
