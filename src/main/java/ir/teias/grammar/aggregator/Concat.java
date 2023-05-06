package ir.teias.grammar.aggregator;

public class Concat extends TwoArgsAggregator {
    public Concat(String argOneColumn, String argTwoColumn) {
        super(argOneColumn, argTwoColumn);
    }

    @Override
    public String toString() {
        return "CONCAT(" + argOneColumn + ", " + argTwoColumn + ")";
    }
}
