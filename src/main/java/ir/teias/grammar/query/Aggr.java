package ir.teias.grammar.query;

import ir.teias.grammar.aggregator.Aggregator;
import ir.teias.grammar.predicate.Predicate;
import ir.teias.grammar.value.Column;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Aggr extends Query {
    private final Column column;
    private final Aggregator aggregator;
    private final Query query;
    private final Predicate predicate;

    @Override
    public String toString() {
        String queryString = query.toString();
        if (query instanceof NamedTable) {
            queryString = "( " + queryString + " )";
        }
        return "SELECT " + column.toString() + ", " + aggregator.toString() + " FROM "
                + queryString + " GROUP BY " +  column.toString() + " HAVING " + predicate.toString();
    }
}
