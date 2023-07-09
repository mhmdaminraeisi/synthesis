package ir.teias.grammar.predicate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class ComposePredicate extends Predicate {
    protected final Predicate left;
    protected final Predicate right;
    public abstract ComposePredicate duplicateWithNewPredicates(Predicate left, Predicate right);
}
