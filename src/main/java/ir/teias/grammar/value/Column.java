package ir.teias.grammar.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Column extends Value {
    private final String columnName;
    private final String tableName;

    @Override
    public String toString() {
//        System.out.println(tableName + "." + columnName);
        return tableName + "." + columnName;
//        return columnName;
    }
}
