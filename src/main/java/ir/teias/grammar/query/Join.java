package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.grammar.predicate.*;
import ir.teias.grammar.value.Column;
import ir.teias.grammar.value.Value;
import ir.teias.model.BitVector;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class Join extends QueryWithPredicate {
    private final Query left;
    private final Query right;

    private final List<String> leftProjColumns;
    private final List<String> rightProjColumns;

    public Join(Query left, Query right, Predicate predicate, List<String> leftProjColumns, List<String> rightProjColumns) {
        super(predicate);
        this.left = left;
        this.right = right;
        this.leftProjColumns = leftProjColumns;
        this.rightProjColumns = rightProjColumns;
    }

    public Join(Query left, Query right, Predicate predicate) {
        this(left, right, predicate, null, null);
    }

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
        return "SELECT " + columnsProjectionString() + " FROM " + leftString + " " + left.getQueryName() + " JOIN " + rightString + " " + right.getQueryName() + " ON " + predicate.toString();
    }

    @Override
    public Table evaluateAbstract() {
        if (SQLManager.isTableExists(getQueryName())) {
            return SQLManager.evaluate("SELECT * FROM " + getQueryName(), getQueryName());
        }
        Table leftTable = left.evaluateAbstract();
        Table rightTable = right.evaluateAbstract();
        leftTable.saveToDb();
        rightTable.saveToDb();
        String dbQuery = "SELECT " + columnsProjectionString() + " FROM " + leftTable.getName() + " " + left.getQueryName() + " JOIN " + rightTable.getName() + " " + right.getQueryName() + " ON " + predicate.toString();
        return SQLManager.evaluate(dbQuery, getQueryName());
    }

    @Override
    public String display(int depth) {
        String leftDisplay = left.display(depth + 1);
        if (!(left instanceof NamedTable)) {
            leftDisplay = "(" + leftDisplay + ") " + left.getQueryName();
        }
        String rightDisplay = right.display(depth + 1);
        if (!(right instanceof NamedTable)) {
            rightDisplay = "(" + rightDisplay + ") " + right.getQueryName();
        }
        String tab = "\t".repeat(depth * 2);
        StringBuilder builder = new StringBuilder("SELECT " + columnsProjectionString() + "\n");
        builder.append(tab).append("FROM   ").append(leftDisplay).append("\n");
        builder.append(tab).append("JOIN   ").append(rightDisplay).append("\n");
        builder.append(tab).append("ON     ").append(predicate);
        return builder.toString();
    }

    private String columnsProjectionString() {
        if (isFullVersion) {
            return "*";
        }
        String leftColumns = leftProjColumns == null ? null : String.join(", ",
                leftProjColumns.stream().map(c -> left.getQueryName() + "." + c).toList());
        String rightColumns = rightProjColumns == null ? null : String.join(", ",
                rightProjColumns.stream().map(c -> right.getQueryName() + "." + c).toList());

        return leftColumns == null && rightColumns == null ? "*"
                : leftColumns == null ? rightColumns
                : rightColumns == null ? leftColumns
                : leftColumns + ", " + rightColumns;
    }

    @Override
    public List<BitVector> bitVectorDFS() {
        List<Predicate> predicates = enumAndGroupPredicates();
        List<BitVector> bitVectors = encodeFiltersToBitVectors(predicates);

        List<BitVector> bitVectorsLeft = left.bitVectorDFS();
        List<BitVector> bitVectorsRight = right.bitVectorDFS();

        List<BitVector> res = new ArrayList<>();
        for (BitVector bvLeft : bitVectorsLeft) {
            for (BitVector bvRight : bitVectorsRight) {
                ArrayList<Boolean> product = bvLeft.crossProduct(bvRight.getVector());
                for (BitVector bv : bitVectors) {
                    Query lQuery = bvLeft.getQuery();
                    Query rQuery = bvRight.getQuery();
                    Predicate pred = updatePredicate(((QueryWithPredicate) bv.getQuery()).getPredicate(), lQuery, rQuery);
                    Join newJoin = new Join(lQuery, rQuery, pred, leftProjColumns, rightProjColumns);
                    res.add(new BitVector(bv.and(product), newJoin, getAbstractTable(), getAbstractTableFull()));
                }
            }
        }
        return filterEquivalentBitVectors(res);
    }

    @Override
    public QueryWithPredicate duplicateWithNewPredicate(Predicate predicate) {
        return new Join(left, right, predicate, leftProjColumns, rightProjColumns);
    }

    @Override
    public List<Predicate> enumeratePrimitivePredicates() {
        HashMap<CellType, List<String>> leftColumnsByType = left.getColumnsByType();
        HashMap<CellType, List<String>> rightColumnsByType = right.getColumnsByType();

        List<String> leftIdColumns = left.getIdColumns();
        List<String> rightIdColumns = right.getIdColumns();

        List<Predicate> primitivesPredicates = new ArrayList<>();

        for (var entry : leftColumnsByType.entrySet()) {
            if (!rightColumnsByType.containsKey(entry.getKey())) {
                continue;
            }
            List<String> leftColumns = entry.getValue();
            List<String> rightColumns = rightColumnsByType.get(entry.getKey());
            List<Value> leftColumnValues = leftColumns.stream().map(col -> new Column(col, left.getQueryName())).collect(Collectors.toList());
            List<Value> rightColumnValues = rightColumns.stream().map(col -> new Column(col, right.getQueryName())).collect(Collectors.toList());

            boolean isString = entry.getKey().equals(CellType.STRING);
            primitivesPredicates.addAll(enumerateBinOpPredicates(leftColumnValues, rightColumnValues, false, false, isString));
        }

        List<Value> leftIdColumnValues = leftIdColumns.stream().map(idCol -> new Column(idCol, left.getQueryName())).collect(Collectors.toList());
        List<Value> rightIdColumnValues = rightIdColumns.stream().map(idCol -> new Column(idCol, right.getQueryName())).collect(Collectors.toList());

        primitivesPredicates.addAll(enumerateBinOpPredicates(leftIdColumnValues, rightIdColumnValues, false, true, false));

        return primitivesPredicates;
    }

    @Override
    public void setConstantsByType(HashMap<CellType, List<Cell<?>>> constantsByType) {
        this.constantsByType = constantsByType;
        if (left instanceof QueryWithPredicate) {
            ((QueryWithPredicate) left).setConstantsByType(constantsByType);
        }
        if (right instanceof QueryWithPredicate) {
            ((QueryWithPredicate) right).setConstantsByType(constantsByType);
        }
    }

    private Predicate updatePredicate(Predicate predicate, Query leftQuery, Query rightQuery) {
        if (predicate instanceof ComposePredicate composePredicate) {
            Predicate leftPredicate = updatePredicate(composePredicate.getLeft(), leftQuery, rightQuery);
            Predicate rightPredicate = updatePredicate(composePredicate.getRight(), leftQuery, rightQuery);
            return composePredicate.duplicateWithNewPredicates(leftPredicate, rightPredicate);
        }
        if (!(predicate instanceof BinaryOperator binOp)) {
            return predicate;
        }
        Value leftValue = binOp.getLeft() instanceof Column
                ? new Column(((Column) binOp.getLeft()).getColumnName(), leftQuery.getQueryName())
                : binOp.getLeft();
        Value rightValue = binOp.getRight() instanceof Column
                ? new Column(((Column) binOp.getRight()).getColumnName(), rightQuery.getQueryName())
                : binOp.getRight();
        return new BinaryOperator(leftValue, rightValue, binOp.getBinOp());
    }
}
