package ir.teias.synthesizer;

import ir.teias.Utils;
import ir.teias.grammar.query.Query;
import ir.teias.grammar.query.QueryWithPredicate;
import ir.teias.model.BitVector;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class PredicateSynthesizer {
    private final Table output;
    private final HashMap<CellType, List<Cell<?>>> constantsByType;

    public void synthesisPredicates(List<Query> abstractQueries) {
        for (Query query : abstractQueries) {
            if (query instanceof QueryWithPredicate) {
                ((QueryWithPredicate) query).setConstantsByType(constantsByType);
            }
        }

        List<BitVector> bitVectors = abstractQueries.get(1).bitVectorDFS()
                .stream().filter(bitVector -> bitVector.decode().equals(output)).toList();
    }
}
