package ir.teias.grammar.aggregator;

import ir.teias.SQLManager;
import ir.teias.Utils;
import ir.teias.grammar.predicate.True;
import ir.teias.grammar.query.Aggr;
import ir.teias.grammar.query.NamedTable;
import ir.teias.grammar.value.Column;
import ir.teias.model.Row;
import ir.teias.model.Table;
import ir.teias.model.cell.CellType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Count extends Aggregator {
    public Count(String newName) {
        super(newName);
    }
    public Count() {
        this(null);
    }
    @Override
    public String toString() {
        return "COUNT(*) AS " + newColumnName();
    }

    @Override
    protected String aggregatorName() {
        return "count";
    }

    @Override
    protected String newColumnName() {
        return aggregatorName();
    }

    @Override
    public Table evaluateAbstract(Aggr aggr) {
        Table table = aggr.getQuery().evaluateAbstract();
        List<List<Row>> rowSubsets = Utils.getListOfSubsets(table.getRows());
        List<Row> rows = new ArrayList<>();
        List<String> columns = new ArrayList<>(aggr.getColumns().stream().map(Column::getColumnName).toList());
        columns.add(newColumnName());
        HashMap<String, CellType> columnsByType = new HashMap<>();
        for (Column col : aggr.getColumns()) {
            columnsByType.put(col.getColumnName(), table.getColumnTypes().get(col.getColumnName()));
        }
        columnsByType.put(newColumnName(), CellType.INTEGER);

        for (List<Row> subset : rowSubsets) {
            Table newTable = new Table(Utils.generateRandomString(6),
                    table.getColumns(), table.getColumnTypes(), subset);
            newTable.saveToDb();
            Aggr newAggr = new Aggr(aggr.getColumns(), aggr.getAggregator(), new NamedTable(newTable), new True());
            rows.addAll(newAggr.evaluate().getRows());
        }
        Table unionTable = new Table(aggr.getQueryName(), columns, columnsByType, rows);
        return SQLManager.deDuplicate(unionTable, unionTable.getName());
    }
}
