package ir.teias;

import ir.teias.model.Row;
import ir.teias.model.Table;
import ir.teias.model.cell.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLManager {
    private static final Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(
                    Configuration.sqlUrl, Configuration.sqlUser, Configuration.sqlPassword
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Table evaluate(String query, String tableName) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            HashMap<String, CellType> columnTypes = new HashMap<>();
            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= preparedStatement.getMetaData().getColumnCount(); i++) {
                String columnName = preparedStatement.getMetaData().getColumnName(i);
                columns.add(columnName);
                String columnTypeName = preparedStatement.getMetaData().getColumnTypeName(i);
                switch (columnTypeName) {
                    case "INT", "BIGINT" -> columnTypes.put(columnName, CellType.INTEGER);
                    case "DOUBLE" -> columnTypes.put(columnName, CellType.DOUBLE);
                    case "VARCHAR" -> columnTypes.put(columnName, CellType.STRING);
                    case "DATE" -> columnTypes.put(columnName, CellType.DATE);
                    case "TIME" -> columnTypes.put(columnName, CellType.TIME);
                }
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Row> rows = new ArrayList<>();
            while (resultSet.next()) {
                HashMap<String, Cell<?>> cells = new HashMap<>();
                for (var entry : columnTypes.entrySet()) {
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
            return new Table(tableName, columns, columnTypes, rows);
        } catch (SQLException e) {
            System.out.println(query);
            throw new RuntimeException(e);
        }
    }

    public static Table deDuplicate(Table table, String name) {
        SQLManager.createDBTableFromTable(table);
        String columns = String.join(", ", table.getColumns());
        String query = "SELECT " + columns + " FROM " + table.getName() + " GROUP BY " + columns;
        return evaluate(query, name);
    }

    public static boolean isTableExists(String tableName) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void createDBTableFromTable(Table table) {
        if (isTableExists(table.getName())) {
            return;
        }
        try {
            Statement statement = connection.createStatement();
            StringBuilder query = new StringBuilder("CREATE TABLE " + table.getName() + " (");
            query.append(String.join(", ", table.getColumns().stream().map(
                    column -> {
                        if (table.getColumnTypes().get(column).equals(CellType.STRING)) {
                            return column + " VARCHAR(255)";
                        }
                        if (table.getColumnTypes().get(column).equals(CellType.INTEGER)) {
                            return column + " INT";
                        }
                        if (table.getColumnTypes().get(column).equals(CellType.DOUBLE)) {
                            return column + " DOUBLE";
                        }
                        if (table.getColumnTypes().get(column).equals(CellType.DATE)) {
                            return column + " DATE";
                        }
                        return column + " TIME";
                    }
            ).toList()));
            query.append(");");
            statement.executeUpdate(query.toString());
            for (Row row : table.getRows()) {
                String insertQuery = "INSERT INTO " + table.getName() + " VALUES (";
                insertQuery += String.join(", ", table.getColumns().stream().map(
                        column -> "'" + row.cells().get(column).getValue().toString() + "'"
                ).toList());
                insertQuery += ")";
                statement.executeUpdate(insertQuery);
            }
        } catch (SQLException e) {
            System.out.println(table);
            throw new RuntimeException(e);
        }
    }

    public static void deleteCreatedTables() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            Statement statement = connection.createStatement();
            ResultSet resultSet = metaData.getTables(Configuration.databaseCatalog, null, "%", null);
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                if (tableName.length() == 6) {
                    statement.executeUpdate("DROP TABLE " + tableName);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
