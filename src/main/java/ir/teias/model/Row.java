package ir.teias.model;

import ir.teias.model.cell.Cell;

import java.util.HashMap;

/**
 *
 * @param cells is mapping from columnName to Cell
 */
public record Row(HashMap<String, Cell<?>> cells) {
}
