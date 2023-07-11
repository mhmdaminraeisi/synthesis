package ir.teias.synthesizer;

import ir.teias.SQLManager;
import ir.teias.Utils;
import ir.teias.grammar.query.Query;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Synthesizer {
    private final AbstractQuerySynthesizer abstractQuerySynthesizer;

    private final PredicateSynthesizer predicateSynthesizer;

    private final Table output;
    public Synthesizer(List<Table> inputs, Table output, HashMap<CellType, List<Cell<?>>> constantsByType, List<String> aggregators, boolean useProjection, boolean multipleGroupBy) {
        this.output = output;
        this.abstractQuerySynthesizer = new AbstractQuerySynthesizer(inputs, output, aggregators, useProjection, multipleGroupBy);
        this.predicateSynthesizer = new PredicateSynthesizer(output, constantsByType);
    }


    public String synthesis() {
        long startTime = System.currentTimeMillis();
        StringBuilder res = new StringBuilder();
        int depth = 1;
        List<Query> solutions = new ArrayList<>();
        while (solutions.size() == 0) {
            System.out.println("search for depth " + depth);
            List<Query> abstractQueries = abstractQuerySynthesizer.synthesisAbstractQueries(depth);
            List<Query> candidateAbstractQueries = filterCandidates(abstractQueries);
            solutions.addAll(predicateSynthesizer.synthesisPredicates(candidateAbstractQueries));
            depth += 1;
            if (solutions.size() != 0) {
                long elapsedTime = (new Date()).getTime() - startTime;

                res.append("candidate abstract queries:\n\n");
                for (var abstractQuery : candidateAbstractQueries) {
                    res.append(Utils.replaceQueryNames(abstractQuery.display(0))).append("\n\n\n");
                }
                res.append("_________________________________________________________\n");
                res.append("solutions: \n\n");
                for (var solution : solutions) {
                    res.append(Utils.replaceQueryNames(solution.display(0))).append("\n\n\n");
                }
                res.append("_________________________________________________________\n");
                res.append("time: " + (double) elapsedTime / 1000.0+ " seconds");
                res.append("\n_________________________________________________________\n");
                res.append("visited abstract queries: " + abstractQueries.size());
                res.append("\n_________________________________________________________\n");
                res.append("candidate abstract queries: " + candidateAbstractQueries.size());
                res.append("\n_________________________________________________________\n");
                res.append("solutions: " + solutions.size());
                res.append("\n_________________________________________________________\n");
                res.append("depth: " + (depth - 1));
            }
        }
        return res.toString();
    }

    public List<Query> filterCandidates(List<Query> queries) {
        return queries.stream().filter(
                query -> query.evaluateAbstract().contains(output)
        ).toList();
    }
}
