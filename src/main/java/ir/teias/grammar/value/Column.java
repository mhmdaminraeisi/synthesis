package ir.teias.grammar.value;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Column extends Value {
    private final String columnName;

    @Override
    public String toString() {
        return columnName;
    }
}
