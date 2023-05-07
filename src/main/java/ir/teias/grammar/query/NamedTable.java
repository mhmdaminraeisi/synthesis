package ir.teias.grammar.query;

import ir.teias.model.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NamedTable extends Query {
    private final Table table;
    @Override
    public String toString() {
        return table.getName();
    }
    @Override
    public Table evaluate() {
        return table;
    }

    @Override
    public Table evaluateAbstract() {
        return evaluate();
    }
}
