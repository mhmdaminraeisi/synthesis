package ir.teias.model;

import ir.teias.model.cell.CellType;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @param columns is unsorted mapping from columnName to cell type
 */
public record Table(String name, LinkedHashMap<String, CellType> columns, List<Row> rows) {
    @Override
    public String toString() {
        StringBuilder cls = new StringBuilder();
        for (var entry : columns.entrySet()) {
            cls.append(entry.getKey()).append(" ");
        }
        StringBuilder rs = new StringBuilder();
        for (Row row : rows) {
            StringBuilder r = new StringBuilder("[ ");
            for (var entry : columns.entrySet()) {
                r.append(row.cells().get(entry.getKey()).getValue().toString()).append(" ");
            }
            r.append("]\n");
            rs.append(r);
        }

        return "Table " + name + " ( " + cls + ")\n" + rs;
    }
}
