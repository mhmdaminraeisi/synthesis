package ir.teias.grammar.aggregator;

import ir.teias.grammar.query.Aggr;
import ir.teias.model.Table;
import lombok.Getter;

@Getter
public abstract class Aggregator {
    protected final String newName;
    public Aggregator(String newName) {
        this.newName = newName;
    }

    protected abstract String aggregatorName();
    protected abstract String newColumnName();
    public abstract Table evaluateAbstract(Aggr aggr);
}
