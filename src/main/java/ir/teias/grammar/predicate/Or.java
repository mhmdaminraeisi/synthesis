package ir.teias.grammar.predicate;

public class Or extends ComposePredicate {

    public Or(Predicate left, Predicate right) {
        super(left, right);
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " OR " + right.toString() + ")";
    }

    @Override
    public ComposePredicate duplicateWithNewPredicates(Predicate left, Predicate right) {
        return new Or(left, right);
    }
}
