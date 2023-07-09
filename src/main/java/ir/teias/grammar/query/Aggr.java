package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.grammar.aggregator.Aggregator;
import ir.teias.grammar.predicate.Predicate;
import ir.teias.grammar.predicate.True;
import ir.teias.grammar.value.Column;
import ir.teias.model.BitVector;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public class Aggr extends QueryWithPredicate {
    private final Column column;
    private final Aggregator aggregator;
    private final Query query;

    public Aggr(Column column, Aggregator aggregator, Query query, Predicate predicate) {
        super(predicate, null);
        this.column = column;
        this.aggregator = aggregator;
        this.query = query;
    }

    @Override
    public String toString() {
        String queryString = query.toString();
        if (!(query instanceof NamedTable)) {
            queryString = "( " + queryString + " )";
        }
        return "SELECT " + column.getColumnName() + ", " + aggregator.toString() + " FROM "
                + queryString + " " + query.getQueryName() + " GROUP BY " + column.getColumnName() + " HAVING " + predicate.toString();
    }

    @Override
    public Table evaluateAbstract() {
        if (SQLManager.isTableExists(getQueryName())) {
            return SQLManager.evaluate("SELECT * FROM " + getQueryName(), getQueryName());
        }
        return aggregator.evaluateAbstract(this);
    }

    @Override
    public String display(int depth) {
        String queryDisplay = query.display(depth + 1);
        if (!(query instanceof NamedTable)) {
            queryDisplay = "(" + queryDisplay + ") " + query.getQueryName();
        }
        String tab = "\t".repeat(depth * 2);
        StringBuilder builder = new StringBuilder("SELECT " + column.getColumnName() + ", " + aggregator.toString() + "\n");
        builder.append(tab).append("FROM   ").append(queryDisplay).append("\n");
        builder.append(tab).append("GROUP BY ").append(column.getColumnName()).append("\n");
        builder.append(tab).append("HAVING ").append(predicate);
        return builder.toString();
    }

    @Override
    public List<BitVector> bitVectorDFS() {
        List<Predicate> predicates = enumAndGroupPredicates();
        List<BitVector> bitVectors = encodeFiltersToBitVectors(predicates);

        List<BitVector> bitVectorsQuery = query.bitVectorDFS();
        List<BitVector> newBitVectors = new ArrayList<>();

        for (BitVector bvq : bitVectorsQuery) {
            Aggr aggr = new Aggr(column, aggregator, bvq.getQuery(), new True());
            newBitVectors.add(new BitVector(aggr, getAbstractTable(), getAbstractTableFull(), false));
        }
        List<BitVector> res = new ArrayList<>();
        for (BitVector bv : bitVectors) {
            for (BitVector nbv : newBitVectors) {
                QueryWithPredicate newQuery = ((Aggr) nbv.getQuery()).duplicateWithNewPredicate(((QueryWithPredicate) bv.getQuery()).getPredicate());
                res.add(new BitVector(bv.and(nbv.getVector()), newQuery, getAbstractTable(), getAbstractTableFull()));
            }
        }
        return filterEquivalentBitVectors(res);
    }

    @Override
    public QueryWithPredicate duplicateWithNewPredicate(Predicate predicate) {
        return new Aggr(column, aggregator, query, predicate);
    }

    @Override
    public List<Predicate> enumeratePrimitivePredicates() {
        // TODO
        return List.of(new True());
    }

    @Override
    public void setConstantsByType(HashMap<CellType, List<Cell<?>>> constantsByType) {
        this.constantsByType = constantsByType;
        if (query instanceof QueryWithPredicate) {
            ((QueryWithPredicate) query).setConstantsByType(constantsByType);
        }
    }
}
