package ir.teias.synthesizer;


import ir.teias.Utils;
import ir.teias.grammar.aggregator.*;
import ir.teias.grammar.predicate.Hole;
import ir.teias.grammar.query.*;
import ir.teias.grammar.value.Column;
import ir.teias.model.Table;
import ir.teias.model.cell.CellType;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class AbstractQuerySynthesizer {
    private final List<Table> inputs;
    private final Table output;
    private final List<String> aggregators;
    private final boolean useProjection;
    private final boolean multipleColumn;

    public List<Query> synthesisAbstractQueries(int depth) {
        List<Query> newQueries = new ArrayList<>();
        for (Table table : inputs) {
            NamedTable namedTable = new NamedTable(table);
            newQueries.addAll(generateSelectAbstractQueries(List.of(namedTable), useProjection && depth == 1));
        }

        List<Query> abstractQueries = new ArrayList<>(newQueries);
        List<Query> preQueries = new ArrayList<>();
        int d = 1;
        while (d++ < depth) {
            newQueries = enumOneStepAbstractQuery(newQueries, preQueries, useProjection && d == depth, d == depth);
            preQueries = new ArrayList<>(abstractQueries);
            abstractQueries.addAll(newQueries);
        }
        return abstractQueries;
    }

    private List<Query> enumOneStepAbstractQuery(List<Query> newQueries, List<Query> preQueries, boolean useProj, boolean lastDepth) {
        List<Query> generatedAbstractQueries = new ArrayList<>();

        generatedAbstractQueries.addAll(generateSelectAbstractQueries(newQueries, useProj));
        generatedAbstractQueries.addAll(generateJoinAbstractQueries(newQueries, preQueries, useProj));
        generatedAbstractQueries.addAll(generateAggrAbstractQueries(newQueries, multipleColumn, lastDepth));
        generatedAbstractQueries.addAll(generateUnionAbstractQueries(newQueries, preQueries));
        return generatedAbstractQueries;
    }

    private List<Union> generateUnionAbstractQueries(List<Query> newQueries, List<Query> preQueries) {
        List<Union> unions = new ArrayList<>();
        for (int i = 0; i < newQueries.size(); i++) {
            Query newQuery = newQueries.get(i);
            if (newQuery instanceof NamedTable) {
                continue;
            }
            for (int j = i + 1; j < newQueries.size(); j++) {
                Query rightQuery = newQueries.get(j);
                if (rightQuery instanceof NamedTable) {
                    continue;
                }
                if (newQuery.getAbstractTable().hasSameSchema(rightQuery.getAbstractTable())) {
                    unions.add(new Union(newQuery, rightQuery));
                }
            }
            for (Query query : preQueries) {
                if (query instanceof NamedTable) {
                    continue;
                }
                if (query.getAbstractTable().hasSameSchema(newQuery.getAbstractTable())) {
                    unions.add(new Union(query, newQuery));
                }
            }
        }
        return unions;
    }

    private List<Select> generateSelectAbstractQueries(List<Query> newQueries, boolean useProj) {
        List<Query> namedTableQueries = newQueries.stream().filter(q -> q instanceof NamedTable).toList();
        if (!useProj) {
            return namedTableQueries.stream().map(q -> new Select(q, new Hole())).toList();
        }

        List<Select> selects = new ArrayList<>();
        for (Query query : namedTableQueries) {
            Table table = query.getAbstractTable();
            List<List<String>> columnSubsets = Utils.getListOfSubsets(table.getColumns());
            for (List<String> subset : columnSubsets) {
                if (subset.size() == table.getColumns().size()) {
                    selects.add(new Select(query, new Hole()));
                } else {
                    selects.add(new Select(query, new Hole(), subset));
                }
            }
        }
        return selects;
    }

    private List<Join> generateJoinAbstractQueries(List<Query> newQueries, List<Query> preQueries, boolean useProj) {
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
                if (useProj) {
                    generatedAbstractQueries.addAll(getJoinAbstractQueriesWithProjections(
                            newQueries.get(i), newQueries.get(j)
                    ));
                }
            }
            for (Query query : preQueries) {
                if (query instanceof Join) {
                    continue;
                }
                generatedAbstractQueries.add(new Join(query, newQuery, new Hole()));
                if (useProj) {
                    generatedAbstractQueries.addAll(getJoinAbstractQueriesWithProjections(
                            query, newQuery
                    ));
                }
            }
        }
        return generatedAbstractQueries;
    }

    private List<Join> getJoinAbstractQueriesWithProjections(Query left, Query right) {
        List<Join> queries = new ArrayList<>();
        Table leftTable = left.getAbstractTable();
        Table rightTable = right.getAbstractTable();
        List<List<String>> leftColumnSubsets = Utils.getListOfSubsets(leftTable.getColumns());
        List<List<String>> rightColumnSubsets = Utils.getListOfSubsets(rightTable.getColumns());

        for (List<String> leftSubset : leftColumnSubsets) {
            queries.add(new Join(left, right, new Hole(), leftSubset, null));
        }
        for (List<String> rightSubset : rightColumnSubsets) {
            queries.add(new Join(left, right, new Hole(), null, rightSubset));
        }
        for (List<String> leftSubset : leftColumnSubsets) {
            for (List<String> rightSubset : rightColumnSubsets) {
                if (leftSubset.size() == leftTable.getColumns().size() &&
                        rightSubset.size() == rightTable.getColumns().size()) {
                    continue;
                }
                queries.add(new Join(left, right, new Hole(), leftSubset, rightSubset));
            }
        }
        return queries;
    }

    private List<Aggr> generateAggrAbstractQueries(List<Query> newQueries, boolean multipleGroupBy, boolean lastDepth) {
        List<Aggr> generatedAbstractQueries = new ArrayList<>();
        for (Query query : newQueries) {
            if (query instanceof Aggr || query instanceof Join) {
                continue;
            }
            // just need table column names;
            Table table = query.evaluate();
            List<Aggr> queries = new ArrayList<>();
            List<List<String>> columnSubsets = Utils.getListOfSubsets(table.getColumns());
            for (List<String> subset : columnSubsets) {
                if (!multipleGroupBy && subset.size() != 1) {
                    continue;
                }
                if (lastDepth && subset.size() != output.getColumns().size() - 1) {
                    continue;
                }
//            for (int i = 0; i < table.getColumns().size(); i++) {
                boolean countAdded = false;
                for (int j = 0; j < table.getColumns().size(); j++) {
                    String argColumn = table.getColumns().get(j);
                    if (subset.contains(argColumn)) {
                        continue;
                    }
                    for (String aggr : aggregators) {
                        Aggregator aggregator = null;
                        switch (aggr) {
                            case "MAX" -> {
                                if (table.getColumnTypes().get(argColumn).equals(CellType.STRING)) {
                                    continue;
                                }
                                aggregator = new Max(argColumn);
                            }
                            case "MIN" -> {
                                if (table.getColumnTypes().get(argColumn).equals(CellType.STRING)) {
                                    continue;
                                }
                                aggregator = new Min(argColumn);
                            }
                            case "SUM" -> {
                                if (!table.getColumnTypes().get(argColumn).equals(CellType.INTEGER)) {
                                    continue;
                                }
                                aggregator = new Sum(argColumn);
                            }
                            case "COUNT" -> {
                                if (countAdded) {
                                    continue;
                                }
                                aggregator = new Count();
                                countAdded = true;
                            }
                        }
                        List<Column> columns = subset.stream().map(c -> new Column(c, table.getName())).toList();
                        queries.add(new Aggr(columns, aggregator, query, new Hole()));
                    }
                }
            }
            generatedAbstractQueries.addAll(queries);
        }
        return generatedAbstractQueries;
    }
}
