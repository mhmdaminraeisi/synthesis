package ir.teias.grammar.query;

import ir.teias.grammar.predicate.Predicate;
import ir.teias.grammar.value.Column;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Join extends Query {
    private final Query left;
    private final Query right;
    private final Predicate predicate;

    @Override
    public String toString() {
        return "SELECT * FROM (" + left.toString() + ") " + left.getQueryName() + " JOIN ("
                + right.toString() + ") " + right.getQueryName() + " ON " + predicate.toString();
    }
}
