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

import java.io.IOException;
import java.sql.Date;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        FileScanner fileScanner = new FileScanner();
        try {
            InputAPI api = fileScanner.read("benchmarks/benchmark3.txt");
            for (var table : api.getInputs()) {
                table.saveToDb();
            }
            api.getOutput().saveToDb();
            Synthesizer synthesizer = new Synthesizer(api.getInputs(), api.getOutput(),
                    api.getConstantsByType(), api.getAggregators(), api.isUseProjection());
            synthesizer.synthesis();
            SQLManager.deleteCreatedTables();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
