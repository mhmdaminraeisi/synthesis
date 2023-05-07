package ir.teias.grammar.aggregator;

public abstract class TwoArgsAggregator extends Aggregator {
    protected final String argOneColumn;
    protected final String argTwoColumn;

    protected TwoArgsAggregator(String argOneColumn, String argTwoColumn, String newName) {
        super(newName);
        this.argOneColumn = argOneColumn;
        this.argTwoColumn = argTwoColumn;
    }

    @Override
    protected String newColumnName() {
        if (newName != null) {
            return newName;
        }
        return aggregatorName() + "_" + argOneColumn + "_" + argTwoColumn;
    }
}
