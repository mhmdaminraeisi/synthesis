package ir.teias.grammar.query;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NamedTable extends Query {
    private final String name;

    @Override
    public String toString() {
        return "\"" + name + "\"";
    }
}
