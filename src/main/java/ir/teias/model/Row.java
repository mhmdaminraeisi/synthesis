package ir.teias.model;

import ir.teias.model.cell.Cell;

import java.util.HashMap;
import java.util.List;

/**
 * @param cells is mapping from columnName to Cell
 */
public record Row(HashMap<String, Cell<?>> cells) {
    public String representation(List<String> columns) {
        return columns.stream().map(c -> cells.get(c).getValue().toString()).toList().toString();
    }
}
