package ir.teias.model;

import ir.teias.model.cell.CellType;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Getter
public class Table {
    private final String name;
    private final List<String> columns;
    private final HashMap<String, CellType> columnTypes;
    private final List<Row> rows;

    private final HashMap<String, Integer> rowsCount = new HashMap<>();
    /**
     * @param columns     is list of columnNames;
     * @param columnTypes is mapping from columnName to CellType
     */
    public Table(String name, List<String> columns, HashMap<String, CellType> columnTypes, List<Row> rows) {
        this.name = name;
        this.columns = columns;
        this.columnTypes = columnTypes;
        this.rows = rows;
        for (Row row : rows) {
            String rep = row.presentation(columns);
            if (rowsCount.containsKey(rep)) {
                rowsCount.put(rep, rowsCount.get(rep) + 1);
            } else {
                rowsCount.put(rep, 1);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder cls = new StringBuilder();
        for (String cName : columns) {
            cls.append(cName).append(" ");
        }
        StringBuilder rs = new StringBuilder();
        for (Row row : rows) {
            StringBuilder r = new StringBuilder("[ ");
            for (String cName : columns) {
                r.append(row.cells().get(cName).getValue().toString()).append(" ");
            }
            r.append("]\n");
            rs.append(r);
        }

        return "Table " + name + " ( " + cls + ")\n" + rs;
    }

    public boolean contains(Table that) {
        if (!hasSameSchema(that)) {
            return false;
        }
        for (var entry : that.getRowsCount().entrySet()) {
            if (!rowsCount.containsKey(entry.getKey())) {
                return false;
            }
            if (rowsCount.get(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Table that = (Table) obj;
        if (!hasSameSchema(that)) {
            return false;
        }
        if (rows.size() != that.getRows().size()) {
            return false;
        }
        if (rowsCount.size() != that.getRowsCount().size()) {
            return false;
        }
        for (var entry : rowsCount.entrySet()) {
            if (!that.getRowsCount().containsKey(entry.getKey())) {
                return false;
            }
            if (!Objects.equals(that.getRowsCount().get(entry.getKey()), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean hasSameSchema(Table that) {
        if (columns.size() != that.getColumns().size()) {
            return false;
        }
        for (int i = 0; i < columns.size(); i++) {
            if (!columnTypes.get(columns.get(i))
                    .equals(that.getColumnTypes().get(that.getColumns().get(i)))) {
                return false;
            }
        }
        return true;
    }
}
