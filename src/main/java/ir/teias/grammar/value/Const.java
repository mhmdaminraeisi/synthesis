package ir.teias.grammar.value;

import ir.teias.model.cell.Cell;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Const<C extends Cell<?>> extends Value {
    private final C constant;

    @Override
    public String toString() {
        return constant.toString();
    }
}
