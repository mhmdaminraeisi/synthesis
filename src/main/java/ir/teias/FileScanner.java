package ir.teias;

import ir.teias.model.InputAPI;
import ir.teias.model.Row;
import ir.teias.model.Table;
import ir.teias.model.cell.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.*;

public class FileScanner {

    public InputAPI read(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            List<Table> inputTables = new ArrayList<>();
            StringBuilder sb = new StringBuilder();

            String line = br.readLine();
            int tableId = 1;
            while (!line.contains("tout")) {
                inputTables.add(readTable(br, "t" + tableId));
                tableId += 1;
                line = br.readLine();
            }
            Table output = readTable(br, "tout");

            br.readLine();

            HashMap<CellType, List<Cell<?>>> constantsByType = new HashMap<>();

            line = br.readLine();
            List<String> intConstants = Arrays.stream(line.split("[=]")[1].replaceAll("[{}\s]", "").split("[,]"))
                    .filter(s -> s.length() > 0).toList();
            if (intConstants.size() > 0) {
                List<Cell<?>> integers = new ArrayList<>();
                for (var constant : intConstants) {
                    integers.add(new IntegerCell(Integer.parseInt(constant)));
                }
                constantsByType.put(CellType.INTEGER, integers);
            }

            line = br.readLine();
            // TODO double

            line = br.readLine();
            List<String> stringConstants = Arrays.stream(line.split("[=]")[1].replaceAll("[{}\s]", "").split("[,]"))
                    .filter(s -> s.length() > 0).toList();
            if (stringConstants.size() > 0) {
                List<Cell<?>> strings = new ArrayList<>();
                for (var constant : stringConstants) {
                    strings.add(new StringCell(constant));
                }
                constantsByType.put(CellType.STRING, strings);
            }

            line = br.readLine();
            List<String> dateConstants = Arrays.stream(line.split("[=]")[1].replaceAll("[{}\s]", "").split("[,]"))
                    .filter(s -> s.length() > 0).toList();
            if (dateConstants.size() > 0) {
                List<Cell<?>> dates = new ArrayList<>();
                for (var constant : dateConstants) {
                    String[] dateStr = constant.split("[-]");
                    Date date = new Date(new GregorianCalendar(Integer.parseInt(dateStr[0]),
                            Integer.parseInt(dateStr[1]) - 1, Integer.parseInt(dateStr[2])).getTime().getTime());

                    dates.add(new DateCell(date));
                }
                constantsByType.put(CellType.DATE, dates);
            }

            // TODO time
            br.readLine();

            br.readLine();
            line = br.readLine();
            List<String> aggrFunctions = Arrays.stream(line.split("[=]")[1].replaceAll("[{}\s]", "").split("[,]"))
                    .filter(s -> s.length() > 0).toList();

            return new InputAPI(inputTables, output, constantsByType, aggrFunctions);
        }
    }

    private Table readTable(BufferedReader reader, String name) throws IOException {
        reader.readLine();
        String[] cls = reader.readLine().split("\s*[|]\s*");
        List<String> columns = Arrays.asList(Arrays.copyOfRange(cls, 1, cls.length));

        HashMap<String, CellType> columnTypes = new HashMap<>();
        String[] clTypes = reader.readLine().split("\s*[|]\s*");
        for (int i = 0; i < columns.size(); i++) {
            String type = clTypes[i + 1];
            switch (type) {
                case "INTEGER" -> columnTypes.put(columns.get(i), CellType.INTEGER);
                case "DOUBLE" -> columnTypes.put(columns.get(i), CellType.DOUBLE);
                case "STRING" -> columnTypes.put(columns.get(i), CellType.STRING);
                case "DATE" -> columnTypes.put(columns.get(i), CellType.DATE);
                case "TIME" -> columnTypes.put(columns.get(i), CellType.TIME);
            }
        }

        List<Row> rows = new ArrayList<>();
        reader.readLine();
        String line = reader.readLine();
        while (!line.contains("_____")) {
            HashMap<String, Cell<?>> cells = new HashMap<>();
            String[] values = line.split("\s*[|]\s*");
            for (int i = 0; i < columns.size(); i++) {
                String value = values[i + 1];
                CellType type = columnTypes.get(columns.get(i));
                if (type.equals(CellType.INTEGER)) {
                    cells.put(columns.get(i), new IntegerCell(Integer.parseInt(value)));
                } else if (type.equals(CellType.STRING)) {
                    cells.put(columns.get(i), new StringCell(value));
                } else if (type.equals(CellType.DATE)) {
                    String[] dateStr = value.split("[-]");
                    Date date = new Date(new GregorianCalendar(Integer.parseInt(dateStr[0]),
                            Integer.parseInt(dateStr[1]) - 1, Integer.parseInt(dateStr[2])).getTime().getTime());
                    cells.put(columns.get(i), new DateCell(date));
                } else if (type.equals(CellType.TIME)) {
                    // TODO
                }
            }
            rows.add(new Row(cells));

            line = reader.readLine();
        }
        return new Table(name, columns, columnTypes, rows);
    }
}
