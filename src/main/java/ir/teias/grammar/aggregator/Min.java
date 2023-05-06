package ir.teias.grammar.aggregator;

public class Min extends OneArgAggregator {
    public Min(String argColumn) {
        super(argColumn);
    }

    @Override
    public String toString() {
        return "MIN(" + argColumn + ")";
    }
}
