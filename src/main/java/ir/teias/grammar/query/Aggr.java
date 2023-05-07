package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.grammar.aggregator.Aggregator;
import ir.teias.grammar.predicate.Predicate;
import ir.teias.grammar.value.Column;
import ir.teias.model.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Aggr extends Query {
    private final Column column;
    private final Aggregator aggregator;
    private final Query query;
    private final Predicate predicate;

    @Override
    public String toString() {
        String queryString = query.toString();
        if (!(query instanceof NamedTable)) {
            queryString = "( " + queryString + " )";
        }
        return "SELECT " + column.toString() + ", " + aggregator.toString() + " FROM "
                + queryString + " " + query.getQueryName() + " GROUP BY " +  column.toString() + " HAVING " + predicate.toString();
    }

    @Override
    public Table evaluateAbstract() {
        if (SQLManager.isTableExists(getQueryName())) {
            return SQLManager.evaluate("SELECT * FROM " + getQueryName(), getQueryName());
        }
        return aggregator.evaluateAbstract(this);
    }
}
