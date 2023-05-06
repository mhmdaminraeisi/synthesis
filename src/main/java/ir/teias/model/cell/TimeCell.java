package ir.teias.model.cell;

import java.sql.Time;

public class TimeCell extends Cell<Time> {
    public TimeCell(Time value) {
        super(value, CellType.TIME);
    }
}
