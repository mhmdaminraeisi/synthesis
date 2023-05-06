package ir.teias.model.cell;

import java.sql.Date;

public class DateCell extends Cell<Date> {
    public DateCell(Date value) {
        super(value, CellType.DATE);
    }

    @Override
    public String toString() {
        return "'" + super.toString() + "'";
    }
}
