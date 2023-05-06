package ir.teias.grammar.predicate;

import ir.teias.grammar.binop.BinOp;
import ir.teias.grammar.value.Value;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BinaryOperator extends Predicate {
    private final Value left;
    private final Value right;
    private final BinOp binOp;

    @Override
    public String toString() {
        return left.toString() + " " + binOp.toString() + " " + right.toString();
    }
}
