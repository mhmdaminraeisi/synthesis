package ir.teias.model;

import ir.teias.SQLManager;
import ir.teias.Utils;
import ir.teias.model.cell.CellType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Getter
public class Table {
    private final String name;
    private final List<String> columns;
    private final HashMap<String, CellType> columnTypes;
    private final List<Row> rows;
    private final List<String> rowsRepresentation = new ArrayList<>();

    private final HashMap<String, Integer> rowsOccur = new HashMap<>();

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
            String rep = row.representation(columns);
            rowsRepresentation.add(rep);
            if (rowsOccur.containsKey(rep)) {
                rowsOccur.put(rep, rowsOccur.get(rep) + 1);
            } else {
                rowsOccur.put(rep, 1);
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

        return "Table " + name + "\n( " + cls + ")\n" + rs;
    }

    public boolean containsRow(Row row) {
        return containsRow(row.representation(columns));
    }

    public int getRowOccur(String rowRepresentation) {
        if (rowsOccur.containsKey(rowRepresentation)) {
            return rowsOccur.get(rowRepresentation);
        }
        return 0;
    }

    public boolean containsRow(String rowRepresentation) {
        return rowsOccur.containsKey(rowRepresentation);
    }

    public boolean contains(Table that) {
        if (!hasSameSchema(that)) {
            return false;
        }
        for (var entry : that.getRowsOccur().entrySet()) {
            if (!rowsOccur.containsKey(entry.getKey())) {
                return false;
            }
            if (rowsOccur.get(entry.getKey()) < entry.getValue()) {
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
        if (rowsOccur.size() != that.getRowsOccur().size()) {
            return false;
        }
        for (var entry : rowsOccur.entrySet()) {
            if (!that.getRowsOccur().containsKey(entry.getKey())) {
                return false;
            }
            if (!Objects.equals(that.getRowsOccur().get(entry.getKey()), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    public boolean hasSameSchema(Table that) {
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

    public void saveToDb() {
        SQLManager.createDBTableFromTable(this);
    }

    public Table duplicate() {
        return new Table(Utils.generateRandomString(6), columns, columnTypes, rows);
    }
}
