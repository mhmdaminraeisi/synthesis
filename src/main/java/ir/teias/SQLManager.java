package ir.teias;

import ir.teias.model.Row;
import ir.teias.model.Table;
import ir.teias.model.cell.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class SQLManager {
    private final Connection connection;

    {
        try {
            connection = DriverManager.getConnection(
                    Configuration.sqlUrl, Configuration.sqlUser, Configuration.sqlPassword
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Table evaluate(String query) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            String name = preparedStatement.getMetaData().getTableName(1);
            LinkedHashMap<String, CellType> columns = new LinkedHashMap<>();
            for (int i = 2; i <= preparedStatement.getMetaData().getColumnCount(); i++) {
                String columnName = preparedStatement.getMetaData().getColumnName(i);
                String columnTypeName = preparedStatement.getMetaData().getColumnTypeName(i);
                if (columnTypeName.equals("int8")) {
                    columns.put(columnName, CellType.INTEGER);
                } else if (columnTypeName.equals("varchar")) {
                    columns.put(columnName, CellType.STRING);
                } else if (columnTypeName.equals("date")) {
                    columns.put(columnName, CellType.DATE);
                } else if (columnTypeName.equals("timetz")) {
                    columns.put(columnName, CellType.TIME);
                }
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Row> rows = new ArrayList<>();
            while (resultSet.next()) {
                HashMap<String, Cell<?>> cells = new HashMap<>();
                for (var entry : columns.entrySet()) {
                    if (entry.getValue().equals(CellType.INTEGER)) {
                        cells.put(entry.getKey(), new IntegerCell(resultSet.getInt(entry.getKey())));
                    } else if (entry.getValue().equals(CellType.STRING)) {
                        cells.put(entry.getKey(), new StringCell(resultSet.getString(entry.getKey())));
                    } else if (entry.getValue().equals(CellType.DATE)) {
                        cells.put(entry.getKey(), new DateCell(resultSet.getDate(entry.getKey())));
                    } else if (entry.getValue().equals(CellType.TIME)) {
                        cells.put(entry.getKey(), new TimeCell(resultSet.getTime(entry.getKey())));
                    }
                }
                rows.add(new Row(cells));
            }
            return new Table(name, columns, rows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
