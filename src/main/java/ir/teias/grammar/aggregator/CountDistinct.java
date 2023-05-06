package ir.teias.grammar.aggregator;

public class CountDistinct extends OneArgAggregator {

    public CountDistinct(String argColumn) {
        super(argColumn);
    }

    @Override
    public String toString() {
        return "COUNT(DISTINCT " + argColumn + ")";
    }
}
