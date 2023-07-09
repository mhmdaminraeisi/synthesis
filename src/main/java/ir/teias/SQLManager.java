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
                if (columns.contains(columnName)) {
                    columnName = columnName + "2";
                }
                columns.add(columnName);
                String columnTypeName = preparedStatement.getMetaData().getColumnTypeName(i);
                switch (columnTypeName) {
                    case "INT", "BIGINT", "DECIMAL" -> columnTypes.put(columnName, CellType.INTEGER);
//                    case "DECIMAL" -> columnTypes.put(columnName, CellType.DOUBLE);
                    case "VARCHAR" -> columnTypes.put(columnName, CellType.STRING);
                    case "DATE" -> columnTypes.put(columnName, CellType.DATE);
                    case "TIME" -> columnTypes.put(columnName, CellType.TIME);
                }
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Row> rows = new ArrayList<>();
            while (resultSet.next()) {
                HashMap<String, Cell<?>> cells = new HashMap<>();
                for (int i = 0; i < columns.size(); i++) {
                    String col = columns.get(i);
                    CellType type = columnTypes.get(col);
                    if (type.equals(CellType.INTEGER)) {
                        cells.put(col, new IntegerCell(resultSet.getInt(i + 1)));
                    } else if (type.equals(CellType.DOUBLE)) {
                        cells.put(col, new DoubleCell(resultSet.getDouble(i + 1)));
                    } else if (type.equals(CellType.STRING)) {
                        cells.put(col, new StringCell(resultSet.getString(i + 1)));
                    } else if (type.equals(CellType.DATE)) {
                        cells.put(col, new DateCell(resultSet.getDate(i + 1)));
                    } else if (type.equals(CellType.TIME)) {
                        cells.put(col, new TimeCell(resultSet.getTime(i + 1)));
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
                statement.executeUpdate("DROP TABLE " + tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
