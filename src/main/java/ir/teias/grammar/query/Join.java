package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.grammar.predicate.Predicate;
import ir.teias.grammar.value.Column;
import ir.teias.grammar.value.Value;
import ir.teias.model.BitVector;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Join extends QueryWithPredicate {
    private final Query left;
    private final Query right;

    public Join(Query left, Query right, Predicate predicate) {
        super(predicate);
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        String leftString = left.toString();
        if (!(left instanceof NamedTable)) {
            leftString = "( " + leftString + " )";
        }
        String rightString = right.toString();
        if (!(right instanceof NamedTable)) {
            rightString = "( " + rightString + " )";
        }
        return "SELECT * FROM " + leftString + " " + left.getQueryName() + " JOIN "
                + rightString + " " + right.getQueryName() + " ON " + predicate.toString();
    }

    @Override
    public Table evaluateAbstract() {
        if (SQLManager.isTableExists(getQueryName())) {
            return SQLManager.evaluate("SELECT * FROM " + getQueryName(), getQueryName());
        }
        Table leftTable = left.evaluateAbstract();
        Table rightTable = right.evaluateAbstract();
        leftTable.saveToDb();
        rightTable.saveToDb();
        String dbQuery = "SELECT * FROM " + leftTable.getName() + " " + left.getQueryName()
                + " JOIN " + rightTable.getName() + " " + right.getQueryName() + " ON " + predicate.toString();
        return SQLManager.evaluate(dbQuery, getQueryName());
    }

    @Override
    public List<BitVector> bitVectorDFS() {
        List<Predicate> predicates = enumAndGroupPredicates();
        List<BitVector> bitVectors = encodeFiltersToBitVectors(predicates);
        List<BitVector> bitVectorsLeft = left.bitVectorDFS();
        List<BitVector> bitVectorsRight = right.bitVectorDFS();

        List<BitVector> res = new ArrayList<>();
        for (BitVector bvLeft : bitVectorsLeft) {
            for (BitVector bvRight : bitVectorsRight) {
                ArrayList<Boolean> product = bvLeft.crossProduct(bvRight.getVector());
                for (BitVector bv : bitVectors) {
                    Join newJoin = new Join(bvLeft.getQuery(), bvRight.getQuery(),
                            ((QueryWithPredicate) bv.getQuery()).getPredicate());
                    res.add(new BitVector(bv.and(product), newJoin, getAbstractTable()));
                }
            }
        }
        return filterEquivalentBitVectors(res);
    }

    @Override
    public QueryWithPredicate duplicateWithNewPredicate(Predicate predicate) {
        return new Join(left, right, predicate);
    }

    @Override
    public List<Predicate> enumeratePrimitivePredicates() {
        HashMap<CellType, List<String>> leftColumnsByType = left.getColumnsByType();
        HashMap<CellType, List<String>> rightColumnsByType = right.getColumnsByType();

        List<String> leftIdColumns = left.getIdColumns();
        List<String> rightIdColumns = right.getIdColumns();

        List<Predicate> primitivesPredicates = new ArrayList<>();

        for (var entry : leftColumnsByType.entrySet()) {
            if (!rightColumnsByType.containsKey(entry.getKey())) {
                continue;
            }
            List<String> leftColumns = entry.getValue();
            List<String> rightColumns = rightColumnsByType.get(entry.getKey());
            List<Value> leftColumnValues = leftColumns.stream()
                    .map(col -> new Column(col, left.getQueryName()))
                    .collect(Collectors.toList());
            List<Value> rightColumnValues = rightColumns.stream()
                    .map(col -> new Column(col, right.getQueryName()))
                    .collect(Collectors.toList());

            primitivesPredicates.addAll(enumerateBinOpPredicates(leftColumnValues, rightColumnValues, false, false));
        }

        List<Value> leftIdColumnValues = leftIdColumns.stream().map(idCol -> new Column(idCol, left.getQueryName())).collect(Collectors.toList());
        List<Value> rightIdColumnValues = rightIdColumns.stream().map(idCol -> new Column(idCol, right.getQueryName())).collect(Collectors.toList());

        primitivesPredicates.addAll(enumerateBinOpPredicates(leftIdColumnValues, rightIdColumnValues, false, true));

        return primitivesPredicates;
    }

    @Override
    public void setConstantsByType(HashMap<CellType, List<Cell<?>>> constantsByType) {
        this.constantsByType = constantsByType;
        if (left instanceof QueryWithPredicate) {
            ((QueryWithPredicate) left).setConstantsByType(constantsByType);
        }
        if (right instanceof QueryWithPredicate) {
            ((QueryWithPredicate) right).setConstantsByType(constantsByType);
        }
    }
}
