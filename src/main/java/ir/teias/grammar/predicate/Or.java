package ir.teias.grammar.predicate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Or extends Predicate {
    private final Predicate left;
    private final Predicate right;

    @Override
    public String toString() {
        return "(" + left.toString() + " OR " + right.toString() + ")";
    }
}
