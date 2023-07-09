package ir.teias.grammar.aggregator;

import ir.teias.SQLManager;
import ir.teias.Utils;
import ir.teias.grammar.predicate.True;
import ir.teias.grammar.query.Aggr;
import ir.teias.grammar.query.NamedTable;
import ir.teias.model.Row;
import ir.teias.model.Table;
import ir.teias.model.cell.CellType;

import java.util.*;

public class Sum extends OneArgAggregator {
    public Sum(String argColumn, String newName) {
        super(argColumn, newName);
    }
    public Sum(String argColumn) {
        this(argColumn, null);
    }

    @Override
    public String toString() {
        return "SUM(" + argColumn + ") AS " + newColumnName();
    }

    @Override
    protected String aggregatorName() {
        return "sum";
    }

    @Override
    public Table evaluateAbstract(Aggr aggr) {
        Table table = aggr.getQuery().evaluateAbstract();
        List<List<Row>> rowSubsets = Utils.getListOfSubsets(table.getRows());
        List<Row> rows = new ArrayList<>();
        List<String> columns = new ArrayList<>(Arrays.asList(aggr.getColumn().getColumnName(), newColumnName()));
        HashMap<String, CellType> columnsByType = new HashMap<>();
        String columnName = aggr.getColumn().getColumnName();
        columnsByType.put(columnName, table.getColumnTypes().get(columnName));
        columnsByType.put(newColumnName(), table.getColumnTypes().get(argColumn));

        for (List<Row> subset : rowSubsets) {
            Table newTable = new Table(Utils.generateRandomString(6),
                    table.getColumns(), table.getColumnTypes(), subset);
            newTable.saveToDb();
            Aggr newAggr = new Aggr(aggr.getColumn(), aggr.getAggregator(), new NamedTable(newTable), new True());
            rows.addAll(newAggr.evaluate().getRows());
        }
        Table unionTable = new Table(aggr.getQueryName(), columns, columnsByType, rows);
        return SQLManager.deDuplicate(unionTable, unionTable.getName());
    }
}
