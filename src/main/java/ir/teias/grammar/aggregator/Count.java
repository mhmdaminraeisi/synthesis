package ir.teias.grammar.aggregator;

public class Count extends OneArgAggregator {
    public Count(String argColumn) {
        super(argColumn);
    }

    @Override
    public String toString() {
        return "COUNT(" + argColumn + ")";
    }
}
