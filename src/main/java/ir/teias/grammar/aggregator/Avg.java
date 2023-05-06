package ir.teias.grammar.aggregator;

public class Avg extends OneArgAggregator {
    public Avg(String argColumn) {
        super(argColumn);
    }
    @Override
    public String toString() {
        return "AVG(" + argColumn + ")";
    }
}
