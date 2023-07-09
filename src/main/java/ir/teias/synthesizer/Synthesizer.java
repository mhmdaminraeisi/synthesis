package ir.teias.synthesizer;

import ir.teias.Utils;
import ir.teias.grammar.query.Query;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Synthesizer {
    private final AbstractQuerySynthesizer abstractQuerySynthesizer;

    private final PredicateSynthesizer predicateSynthesizer;

    public Synthesizer(List<Table> inputs, Table output, HashMap<CellType, List<Cell<?>>> constantsByType, List<String> aggregators, boolean useProjection) {
        this.abstractQuerySynthesizer = new AbstractQuerySynthesizer(inputs, output, aggregators, useProjection);
        this.predicateSynthesizer = new PredicateSynthesizer(output, constantsByType);
    }


    public void synthesis() {
        long end = System.currentTimeMillis() + 15000;
        int depth = 1;
        List<Query> solutions = new ArrayList<>();
        while (solutions.size() == 0) {
            System.out.println("Search for depth = " + depth);
            List<Query> candidateAbstractQueries = abstractQuerySynthesizer.synthesisAbstractQueries(depth);
            solutions.addAll(predicateSynthesizer.synthesisPredicates(candidateAbstractQueries));
            depth += 1;
        }

//        List<Query> candidates = abstractQuerySynthesizer.synthesisAbstractQueries(3);
//        solutions.addAll(predicateSynthesizer.synthesisPredicates(candidates));

        for (Query query : solutions) {
            System.out.println(Utils.replaceQueryNames(query.display(0)));
            System.out.println("_______________________________");
        }

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
