package ir.teias;

import ir.teias.grammar.aggregator.Max;
import ir.teias.grammar.aggregator.Sum;
import ir.teias.grammar.predicate.Hole;
import ir.teias.grammar.predicate.True;
import ir.teias.grammar.query.Aggr;
import ir.teias.grammar.query.Join;
import ir.teias.grammar.query.NamedTable;
import ir.teias.grammar.value.Column;
import ir.teias.model.InputAPI;
import ir.teias.model.Row;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.DateCell;
import ir.teias.model.cell.IntegerCell;
import ir.teias.synthesizer.Synthesizer;
import org.apache.catalina.AccessLog;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        SQLManager.deleteCreatedTables();
        FileScanner fileScanner = new FileScanner();
        try {
            int counter = 1;
            while (true) {
                String path = "benchmarks/benchmark" + counter + ".txt";
                File file = new File(path);
                if (!file.exists()) {
                    break;
                }
                System.out.println("synthesis benchmark " + counter);
                String solPath = "benchmarks/benchmark" + counter + "_solution.txt";
                InputAPI api = fileScanner.read(path);
                PrintWriter writer = new PrintWriter(solPath, "UTF-8");
                for (var table : api.getInputs()) {
                    table.saveToDb();
                }
                api.getOutput().saveToDb();
                Synthesizer synthesizer = new Synthesizer(api.getInputs(), api.getOutput(),
                        api.getConstantsByType(), api.getAggregators(), api.isUseProjection(), api.isMultipleGroupBy());
                String result = synthesizer.synthesis();
                writer.println(result);
                writer.close();
                SQLManager.deleteCreatedTables();
                counter += 1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
