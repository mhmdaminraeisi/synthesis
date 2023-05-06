package ir.teias.model.cell;

public class StringCell extends Cell<String> {
    public StringCell(String value) {
        super(value, CellType.STRING);
    }

    @Override
    public String toString() {
        return "'" + super.toString() + "'";
    }
}
