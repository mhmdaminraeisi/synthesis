package ir.teias.grammar.aggregator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TwoArgsAggregator extends Aggregator {
    protected final String argOneColumn;
    protected final String argTwoColumn;
}
