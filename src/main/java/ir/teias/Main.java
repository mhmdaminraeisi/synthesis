package ir.teias;

import ir.teias.grammar.aggregator.Max;
import ir.teias.grammar.predicate.Hole;
import ir.teias.grammar.query.Aggr;
import ir.teias.grammar.query.NamedTable;
import ir.teias.grammar.value.Column;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.DateCell;
import ir.teias.model.cell.IntegerCell;
import ir.teias.synthesizer.Optimizer;
import ir.teias.synthesizer.Synthesizer;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String queryT1 = "SELECT * FROM t1";
        String queryT2 = "SELECT * FROM t2";
        String queryTout = "SELECT * FROM Tout";

        Table T1 = SQLManager.evaluate(queryT1, "t1");
        Table T2 = SQLManager.evaluate(queryT2, "t2");
        Table Tout = SQLManager.evaluate(queryTout, "tout");

        List<Table> inputs = Arrays.asList(T1, T2);
        List<Cell<?>> constants = Arrays.asList(
                new DateCell(new Date(2022, 12, 25)),
                new DateCell(new Date(2022, 12, 24)),
                new IntegerCell(12)
        );
        Synthesizer synthesizer = new Synthesizer(inputs, Tout, constants);
        synthesizer.synthesis();

//        String testQuery = "SELECT * FROM ( SELECT * FROM \"t1\" WHERE \"t1\".date = '2022-12-24' OR \"t1\".date = '2022-12-25') t3 JOIN (select oid, max(val) from (select * from t2 where t2.val < 50) t4 group by oid) t5 on t3.uid = t5.oid";
//        System.out.println(SQLManager.evaluate(testQuery));

        Table newTable = new Table("newName", T1.getColumns(), T1.getColumnTypes(), T1.getRows());
//        List<String> ls = Arrays.asList("salam", "khoobam", "khoobi");
//        SQLManager.createDBTableFromTable(newTable);
        NamedTable namedTable1 = new NamedTable(T1);
        NamedTable namedTable2 = new NamedTable(T2);
        Aggr aggr = new Aggr(new Column("oid"), new Max("val"), namedTable2, new Hole());
//        System.out.println(aggr.evaluateAbstract());
//        System.out.println(aggr.evaluate());
//        System.out.println(namedTable2.getTable());

//        HashMap<String, Integer> hashMap = new HashMap<>();
//        hashMap.put("salam", 3);
//        hashMap.put("chetori", 6);
//        System.out.println(hashMap.values().stream().toList().toString());
        Table test1 = SQLManager.evaluate("select * from test1", "test1");
        Table test2 = SQLManager.evaluate("select * from test2", "test2");
        SQLManager.deleteCreatedTables();
    }
}
