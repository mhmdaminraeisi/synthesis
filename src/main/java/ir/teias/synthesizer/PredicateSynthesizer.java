package ir.teias.synthesizer;

import ir.teias.Utils;
import ir.teias.grammar.query.Query;
import ir.teias.grammar.query.QueryWithPredicate;
import ir.teias.model.BitVector;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PredicateSynthesizer {
    private final List<Table> inputs;
    private final Table output;
    private final List<Cell<?>> constants;
    HashMap<CellType, List<Cell<?>>> constantsByType = new HashMap<>();

    public PredicateSynthesizer(List<Table> inputs, Table output, List<Cell<?>> constants) {
        this.inputs = inputs;
        this.output = output;
        this.constants = constants;

        for (var cell : constants) {
            CellType type = cell.getCellType();
            if (!constantsByType.containsKey(type)) {
                constantsByType.put(type, new ArrayList<>());
            }
            constantsByType.get(type).add(cell);
        }
    }

    public void synthesisPredicates(List<Query> abstractQueries) {
        for (Query query : abstractQueries) {
            if (query instanceof QueryWithPredicate) {
                ((QueryWithPredicate) query).setConstantsByType(constantsByType);
            }
        }

        for (Query query : abstractQueries) {
            if (query instanceof QueryWithPredicate) {
//                ((QueryWithPredicate) query).setConstantsByType(constantsByType);
//                enumAndGroupPredicates((QueryWithPredicate) query);
            }
        }

        List<BitVector> bitVectors = abstractQueries.get(1).bitVectorDFS()
                .stream().filter(bitVector -> bitVector.decode().equals(output)).toList();

    }
}
