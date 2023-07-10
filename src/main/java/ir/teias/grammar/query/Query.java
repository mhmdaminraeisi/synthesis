package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.Utils;
import ir.teias.model.BitVector;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class Query {
    private Table abstractTable = null;
    private Table abstractTableFull = null;
    private final String queryName = Utils.generateRandomString(6);
    protected final List<String> projectColumns;
    protected boolean isFullVersion = false;

    @Setter
    protected HashMap<CellType, List<Cell<?>>> constantsByType;

    public Table evaluate() {
        return SQLManager.evaluate(this.toString(), queryName);
    }
    public Table evaluateFull() {
        isFullVersion = true;
        Table res = evaluate();
        isFullVersion = false;
        return res;
    }

    public Table getAbstractTable() {
        if (abstractTable == null) {
            abstractTable = this.evaluateAbstract();
        }
        return abstractTable;
    }

    public Table getAbstractTableFull() {
        if (abstractTableFull == null) {
            abstractTableFull = this.evaluateAbstractFull();
        }
        return abstractTableFull;
    }

    public abstract Table evaluateAbstract();
    public Table evaluateAbstractFull() {
        isFullVersion = true;
        Table res = evaluateAbstract();
        isFullVersion = false;
        return res;
    }

    public abstract String display(int depth);

    public HashMap<CellType, List<String>> getColumnsByType() {
        Table absTable = getAbstractTableFull();
        HashMap<CellType, List<String>> columnsByType = new HashMap<>();
        for (String column : absTable.getColumns()) {
            if (column.contains("id") || column.contains("Id")) {
                continue;
            }
            CellType type = absTable.getColumnTypes().get(column);
            if (!columnsByType.containsKey(type)) {
                columnsByType.put(type, new ArrayList<>());
            }
            columnsByType.get(type).add(column);
        }
        return columnsByType;
    }

    public List<String> getIdColumns() {
        return getAbstractTable().getColumns().stream()
                .filter(column -> column.contains("id") || column.contains("Id"))
                .toList();
    }

    public abstract List<BitVector> bitVectorDFS();

    protected List<BitVector> filterEquivalentBitVectors(List<BitVector> bitVectors) {
        List<BitVector> filteredBitVectors = new ArrayList<>();
        for (BitVector bitVector : bitVectors) {
            boolean shouldAdd = true;
            for (BitVector filteredBitVector : filteredBitVectors) {
                if (filteredBitVector.equals(bitVector)) {
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd) {
                filteredBitVectors.add(bitVector);
            }
        }
        return filteredBitVectors;
    }
}
