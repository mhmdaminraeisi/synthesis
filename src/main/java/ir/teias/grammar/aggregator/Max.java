package ir.teias.grammar.aggregator;

import ir.teias.SQLManager;
import ir.teias.grammar.query.Aggr;
import ir.teias.model.Row;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Max extends OneArgAggregator {

    public Max(String argColumn, String newName) {
        super(argColumn, newName);
    }

    public Max(String argColumn) {
        this(argColumn, null);
    }

    @Override
    public String toString() {
        return "MAX(" + argColumn + ") AS " + newColumnName();
    }

    @Override
    protected String aggregatorName() {
        return "max";
    }

    @Override
    public Table evaluateAbstract(Aggr aggr) {
        Table deDuplicatedTable = SQLManager.deDuplicate(aggr.getQuery().evaluateAbstract(), aggr.getQueryName());
        deDuplicatedTable.getColumns().clear();
        deDuplicatedTable.getColumns().addAll(Arrays.asList(aggr.getColumn().getColumnName(), newColumnName()));
        deDuplicatedTable.getColumnTypes().put(newColumnName(), deDuplicatedTable.getColumnTypes().get(argColumn));
        for (Row row : deDuplicatedTable.getRows()) {
            row.cells().put(newColumnName(), row.cells().get(argColumn));
        }
        List<Row> newRows = deDuplicatedTable.getRows().stream().map(
                row -> {
                    HashMap<String, Cell<?>> cells = new HashMap<>();
                    for (String c : deDuplicatedTable.getColumns()) {
                        cells.put(c, row.cells().get(c));
                    }
                    return new Row(cells);
                }
        ).toList();
        deDuplicatedTable.getRows().clear();
        deDuplicatedTable.getRows().addAll(newRows);
        return deDuplicatedTable;
    }
}
