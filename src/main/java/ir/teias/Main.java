package ir.teias;

import ir.teias.grammar.query.NamedTable;
import ir.teias.model.InputAPI;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.DateCell;
import ir.teias.model.cell.IntegerCell;
import ir.teias.synthesizer.Synthesizer;

import java.io.IOException;
import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        FileScanner fileScanner = new FileScanner();
        try {
            InputAPI api = fileScanner.read("benchmarks/benchmark1.txt");
            for (var table : api.getInputs()) {
                table.saveToDb();
            }
            api.getOutput().saveToDb();
            Synthesizer synthesizer = new Synthesizer(api.getInputs(), api.getOutput(), api.getConstantsByType(), api.getAggregators());
            synthesizer.synthesis();
            SQLManager.deleteCreatedTables();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
