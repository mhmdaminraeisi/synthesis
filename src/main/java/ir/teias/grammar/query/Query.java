package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.Utils;
import ir.teias.model.BitVector;
import ir.teias.model.Table;
import ir.teias.model.cell.CellType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
public abstract class Query {
    private Table abstractTable = null;
    private final String queryName = Utils.generateRandomString(6);

    public Table evaluate() {
        return SQLManager.evaluate(this.toString(), queryName);
    }

    public Table getAbstractTable() {
        if (abstractTable == null) {
            abstractTable = this.evaluateAbstract();
        }
        return abstractTable;
    }

    public abstract Table evaluateAbstract();

    public abstract String display(int depth);

    public HashMap<CellType, List<String>> getColumnsByType() {
        Table absTable = getAbstractTable();
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
