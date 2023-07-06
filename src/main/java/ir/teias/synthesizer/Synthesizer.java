package ir.teias.synthesizer;

import ir.teias.grammar.query.Query;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;

import java.util.HashMap;
import java.util.List;

public class Synthesizer {
    private final AbstractQuerySynthesizer abstractQuerySynthesizer;

    private final PredicateSynthesizer predicateSynthesizer;

    public Synthesizer(List<Table> inputs, Table output, HashMap<CellType, List<Cell<?>>> constantsByType, List<String> aggregators) {
        this.abstractQuerySynthesizer = new AbstractQuerySynthesizer(inputs, output, aggregators);
        this.predicateSynthesizer = new PredicateSynthesizer(output, constantsByType);
    }


    public void synthesis() {
        long end = System.currentTimeMillis() + 15000;
        int depth = 3;
        List<Query> abstractQueries = abstractQuerySynthesizer.synthesisAbstractQueries(depth);
        predicateSynthesizer.synthesisPredicates(abstractQueries);
//        while (System.currentTimeMillis() < end) {
//            List<Query> queries = new ArrayList<>();
//            for (Query abstractQuery : abstractQueries) {
//
//            }
//        }
//        for (Query abstractQuery : abstractQueries) {
//            System.out.println(abstractQuery.getQueryName() + "    " + abstractQuery.toString());
//            System.out.println(abstractQuery.evaluateAbstract().toString());
//            System.out.println(output);
//        }
//        System.out.println(abstractQueries.size());
    }

}
