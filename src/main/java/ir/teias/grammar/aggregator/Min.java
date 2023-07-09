package ir.teias.grammar.aggregator;

import ir.teias.SQLManager;
import ir.teias.grammar.query.Aggr;
import ir.teias.model.Row;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Min extends OneArgAggregator {

    public Min(String argColumn, String newName) {
        super(argColumn, newName);
    }

    public Min(String argColumn) {
        this(argColumn, null);
    }

    @Override
    public String toString() {
        return "MIN(" + argColumn + ") AS " + newColumnName();
    }

    @Override
    protected String aggregatorName() {
        return "min";
    }

    @Override
    public Table evaluateAbstract(Aggr aggr) {
        Table deDuplicatedTable = SQLManager.deDuplicate(aggr.getQuery().evaluateAbstract(), aggr.getQueryName());
        List<String> newColumns = Arrays.asList(aggr.getColumn().getColumnName(), newColumnName());
        HashMap<String, CellType> newColumnsByType = new HashMap<>();
        String col = aggr.getColumn().getColumnName();
        newColumnsByType.put(col, deDuplicatedTable.getColumnTypes().get(col));
        newColumnsByType.put(newColumnName(), deDuplicatedTable.getColumnTypes().get(argColumn));
        List<Row> newRows = new ArrayList<>();
        for (Row row : deDuplicatedTable.getRows()) {
            HashMap<String, Cell<?>> cells = new HashMap<>();
            cells.put(newColumnName(), row.cells().get(argColumn));
            cells.put(col, row.cells().get(col));
            newRows.add(new Row(cells));
        }
        return new Table(deDuplicatedTable.getName(), newColumns, newColumnsByType, newRows);
    }
}
