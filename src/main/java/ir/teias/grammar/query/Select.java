package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.grammar.predicate.Predicate;
import ir.teias.model.Table;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Select extends Query {
    private final Query query;
    private final Predicate predicate;

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
        return query.evaluateAbstract();
    }
}
