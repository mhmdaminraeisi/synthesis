package ir.teias.model;

import ir.teias.grammar.aggregator.Aggregator;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class InputAPI {
    private final List<Table> inputs;
    private final Table output;
    private final HashMap<CellType, List<Cell<?>>> constantsByType;
    private final List<String> aggregators;
    private final boolean useProjection;
    private final boolean multipleGroupBy;

}
