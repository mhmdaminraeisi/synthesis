package ir.teias.grammar.aggregator;

public class Sum extends OneArgAggregator {
    public Sum(String argColumn) {
        super(argColumn);
    }

    @Override
    public String toString() {
        return "SUM(" + argColumn + ")";
    }
}
