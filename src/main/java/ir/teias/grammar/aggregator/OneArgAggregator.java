package ir.teias.grammar.aggregator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class OneArgAggregator extends Aggregator {
    protected final String argColumn;
}
