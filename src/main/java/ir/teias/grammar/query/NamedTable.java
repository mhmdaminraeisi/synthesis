package ir.teias.grammar.query;

import ir.teias.model.BitVector;
import ir.teias.model.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
    public String getQueryName() {
        return table.getName();
    }

    @Override
    public Table evaluateAbstract() {
        return evaluate();
    }

    @Override
    public List<BitVector> bitVectorDFS() {
        return List.of(new BitVector(this, getAbstractTable(), true));
    }

}
