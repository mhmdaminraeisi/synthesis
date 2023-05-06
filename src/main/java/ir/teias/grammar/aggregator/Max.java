package ir.teias.grammar.aggregator;

public class Max extends OneArgAggregator {
    public Max(String argColumn) {
        super(argColumn);
    }

    @Override
    public String toString() {
        return "MAX(" + argColumn + ")";
    }
}
