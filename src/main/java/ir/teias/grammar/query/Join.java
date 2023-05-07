package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.grammar.predicate.Predicate;
import ir.teias.grammar.value.Column;
import ir.teias.model.Table;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Join extends Query {
    private final Query left;
    private final Query right;
    private final Predicate predicate;

    @Override
    public String toString() {
        String leftString = left.toString();
        if (!(left instanceof NamedTable)) {
            leftString = "( " + leftString + " )";
        }
        String rightString = right.toString();
        if (!(right instanceof NamedTable)) {
            rightString = "( " + rightString + " )";
        }
        return "SELECT * FROM " + leftString + " " + left.getQueryName() + " JOIN "
                + rightString + " " + right.getQueryName() + " ON " + predicate.toString();
    }

    @Override
    public Table evaluateAbstract() {
        if (SQLManager.isTableExists(getQueryName())) {
            return SQLManager.evaluate("SELECT * FROM " + getQueryName(), getQueryName());
        }
        Table leftTable = left.evaluateAbstract();
        Table rightTable = right.evaluateAbstract();
        if (!SQLManager.isTableExists(leftTable.getName())) {
            SQLManager.createDBTableFromTable(leftTable);
        }
        if (!SQLManager.isTableExists(rightTable.getName())) {
            SQLManager.createDBTableFromTable(rightTable);
        }
        String dbQuery = "SELECT * FROM " + leftTable.getName() + " JOIN " + rightTable.getName() + " ON TRUE";
        return SQLManager.evaluate(dbQuery, getQueryName());
    }
}
