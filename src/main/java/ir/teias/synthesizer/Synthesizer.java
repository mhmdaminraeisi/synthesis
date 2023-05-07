package ir.teias.synthesizer;

import ir.teias.grammar.aggregator.Max;
import ir.teias.grammar.predicate.Hole;
import ir.teias.grammar.query.*;
import ir.teias.grammar.value.Column;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;

import java.util.ArrayList;
import java.util.List;

public class Synthesizer {
    private final List<Table> inputs;
    private final Table output;
    private final List<Cell<?>> constants;
    private final AbstractQuerySynthesizer abstractQuerySynthesizer;

    public Synthesizer(List<Table> inputs, Table output, List<Cell<?>> constants) {
        this.inputs = inputs;
        this.output = output;
        this.constants = constants;
        this.abstractQuerySynthesizer = new AbstractQuerySynthesizer(inputs, output);
    }


    public void synthesis() {
        long end = System.currentTimeMillis() + 15000;
        int depth = 3;
        List<Query> abstractQueries = abstractQuerySynthesizer.synthesisAbstractQueries(depth);
//        while (System.currentTimeMillis() < end) {
//            List<Query> queries = new ArrayList<>();
//            for (Query abstractQuery : abstractQueries) {
//
//            }
//        }
        for (Query abstractQuery : abstractQueries) {
            System.out.println(abstractQuery.getQueryName() + "    " + abstractQuery.toString());
            System.out.println(abstractQuery.evaluateAbstract().toString());
            System.out.println(output);
        }
        System.out.println(abstractQueries.size());
    }

}