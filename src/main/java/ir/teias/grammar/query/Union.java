package ir.teias.grammar.query;

import ir.teias.SQLManager;
import ir.teias.model.BitVector;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Union extends Query {
    private final Query left;
    private final Query right;
    public Union(Query left, Query right, List<String> projectColumns) {
        super(projectColumns);
        this.left = left;
        this.right = right;
    }
    public Union(Query left, Query right) {
        this(left, right, null);
    }

    @Override
    public Table evaluateAbstract() {
        Table leftTable = left.evaluateAbstract();
        Table rightTable = right.evaluateAbstract();
        leftTable.saveToDb();
        rightTable.saveToDb();
        String query = "SELECT * FROM " + leftTable.getName() + " UNION " +
                "SELECT * FROM " + rightTable.getName();
        return SQLManager.evaluate(query, getQueryName());
    }

    @Override
    public String display(int depth) {
        String leftDisplay = left.display(depth);
        String rightDisplay = right.display(depth);
        String tab = "\t".repeat(depth * 2);
        return tab + leftDisplay + "\n" +
                tab + "UNION" + "\n" +
                tab + rightDisplay + "\n";
    }

    @Override
    public List<BitVector> bitVectorDFS() {
        List<BitVector> leftBitvectors = left.bitVectorDFS();
        List<BitVector> rightBitvectors = right.bitVectorDFS();
        List<BitVector> res = new ArrayList<>();
        for (BitVector lbv : leftBitvectors) {
            for (BitVector rbv : rightBitvectors) {
                Union newUnion = new Union(lbv.getQuery(), rbv.getQuery());
                res.add(new BitVector(lbv.concatenate(rbv.getVector()), newUnion, getAbstractTable(), getAbstractTableFull()));
            }
        }
        return filterEquivalentBitVectors(res);
    }

    @Override
    public void setConstantsByType(HashMap<CellType, List<Cell<?>>> constantsByType) {
        left.setConstantsByType(constantsByType);
        right.setConstantsByType(constantsByType);
    }

    @Override
    public String toString() {
        return left.toString() + " UNION " + right.toString();
    }
}
