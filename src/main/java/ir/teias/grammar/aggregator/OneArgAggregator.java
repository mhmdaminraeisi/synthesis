package ir.teias.grammar.aggregator;

import lombok.RequiredArgsConstructor;

public abstract class OneArgAggregator extends Aggregator {
    protected final String argColumn;
    public OneArgAggregator(String argColumn, String newName) {
        super(newName);
        this.argColumn = argColumn;
    }
    @Override
    protected String newColumnName() {
        if (newName != null) {
            return newName;
        }
        return aggregatorName() + "_" + argColumn;
    }
}
