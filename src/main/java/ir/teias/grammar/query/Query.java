package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.Utils;
import ir.teias.model.Table;
import lombok.Getter;

@Getter
public abstract class Query {
    private final String queryName = Utils.generateRandomString(6);
    public Table evaluate() {
        return SQLManager.evaluate(this.toString(), queryName);
    }
    public abstract Table evaluateAbstract();
}
