package ir.teias.model;

import ir.teias.model.cell.CellType;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @param columns is unsorted mapping from columnName to cell type
 */
public record Table(String name, LinkedHashMap<String, CellType> columns, List<Row> rows) {
}
