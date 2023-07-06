package ir.teias.synthesizer;


import ir.teias.grammar.aggregator.Aggregator;
import ir.teias.grammar.aggregator.Max;
import ir.teias.grammar.predicate.Hole;
import ir.teias.grammar.query.*;
import ir.teias.grammar.value.Column;
import ir.teias.model.Table;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class AbstractQuerySynthesizer {
    private final List<Table> inputs;
    private final Table output;
    private final List<String> aggregators;

    public List<Query> filterCandidates(List<Query> queries) {
        return queries.stream().filter(
                query -> query.evaluateAbstract().contains(output)
        ).toList();
    }

    public List<Query> synthesisAbstractQueries(int depth) {
        List<Query> newQueries = new ArrayList<>();
        for (Table table : inputs) {
            NamedTable namedTable = new NamedTable(table);
            newQueries.add(new Select(namedTable, new Hole()));
//            newQueries.add(namedTable);
        }
        List<Query> abstractQueries = new ArrayList<>(newQueries);
        List<Query> preQueries = new ArrayList<>();
        int d = 1;
        while (d++ < depth) {
            newQueries = enumOneStepAbstractQuery(newQueries, preQueries).stream().filter(
                    newQuery -> {
                        for (Query query : abstractQueries) {
                            if (query.evaluateAbstract().equals(newQuery.evaluateAbstract())) {
                                return false;
                            }
                        }
                        return true;
                    }
            ).toList();
            preQueries = new ArrayList<>(abstractQueries);
            abstractQueries.addAll(newQueries);
        }
//        return abstractQueries;
        return filterCandidates(abstractQueries);
    }

    private List<Query> enumOneStepAbstractQuery(List<Query> newQueries, List<Query> preQueries) {
        List<Query> generatedAbstractQueries = new ArrayList<>();

        generatedAbstractQueries.addAll(generateSelectAbstractQueries(newQueries));
        generatedAbstractQueries.addAll(generateJoinAbstractQueries(newQueries, preQueries));
        generatedAbstractQueries.addAll(generateAggrAbstractQueries(newQueries));

        return generatedAbstractQueries;
    }


    private List<Select> generateSelectAbstractQueries(List<Query> newQueries) {
        return newQueries.stream()
                .filter(query -> query instanceof NamedTable)
                .map(query -> new Select(query, new Hole()))
                .toList();
    }

    private List<Join> generateJoinAbstractQueries(List<Query> newQueries, List<Query> preQueries) {
        List<Join> generatedAbstractQueries = new ArrayList<>();
        for (int i = 0; i < newQueries.size(); i++) {
            Query newQuery = newQueries.get(i);
            if (newQuery instanceof Join) {
                continue;
            }
            for (int j = i + 1; j < newQueries.size(); j++) {
                if (newQueries.get(j) instanceof Join) {
                    continue;
                }
                generatedAbstractQueries.add(new Join(newQueries.get(i), newQueries.get(j), new Hole()));
            }
            generatedAbstractQueries.addAll(preQueries.stream()
                    .filter(query -> !(query instanceof Join))
                    .map(query -> new Join(query, newQuery, new Hole()))
                    .toList());
        }
        return generatedAbstractQueries;
    }

    private List<Aggr> generateAggrAbstractQueries(List<Query> newQueries) {
        List<Aggr> generatedAbstractQueries = new ArrayList<>();
        for (Query query : newQueries) {
            if (query instanceof Aggr || query instanceof Join) {
                continue;
            }
            // just need table column names;
            Table table = query.evaluate();
            List<Aggr> queries = new ArrayList<>();
            for (int i = 0; i < table.getColumns().size(); i++) {
                for (int j = 0; j < table.getColumns().size(); j++) {
                    if (i == j) {
                        continue;
                    }
                    for (String aggr : aggregators) {
                        Aggregator aggregator = null;
                        switch (aggr) {
                            case "MAX" -> aggregator = new Max(table.getColumns().get(j));
                        }
                        queries.add(new Aggr(new Column(table.getColumns().get(i), table.getName()), aggregator, query, new Hole()));
                    }
                }
            }
            generatedAbstractQueries.addAll(queries);
        }
        return generatedAbstractQueries;
    }
}
