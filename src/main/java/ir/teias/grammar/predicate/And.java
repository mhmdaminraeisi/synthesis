package ir.teias.grammar.predicate;

public class And extends ComposePredicate {
    public And(Predicate left, Predicate right) {
        super(left, right);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " AND " + right.toString() + ")";
    }

    @Override
    public ComposePredicate duplicateWithNewPredicates(Predicate left, Predicate right) {
        return new And(left, right);
    }
}
