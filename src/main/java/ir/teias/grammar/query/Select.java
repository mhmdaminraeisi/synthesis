package ir.teias.grammar.query;

import ir.teias.grammar.predicate.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Select extends Query {
    private final Query query;
    private final Predicate predicate;

    @Override
    public String toString() {
        String queryString = query.toString();
        if (!(query instanceof NamedTable)) {
            queryString = "(" + queryString + ")";
        }
        return "SELECT * FROM " + queryString + " WHERE " + predicate;
    }
}
