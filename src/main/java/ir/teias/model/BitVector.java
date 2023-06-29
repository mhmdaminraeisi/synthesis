package ir.teias.model;

import ir.teias.Utils;
import ir.teias.grammar.query.Query;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter
public class BitVector {
    private final Query query;
    private final Table initAbstractTable;
    private final ArrayList<Boolean> vector = new ArrayList<>();

    public BitVector(Query query, Table initAbstractTable, boolean encodeAbstract) {
        this.query = query;
        this.initAbstractTable = initAbstractTable;
        this.encode(encodeAbstract ? query.evaluateAbstract() : query.evaluate());
    }

    public BitVector(ArrayList<Boolean> vc, Query query, Table initAbstractTable) {
        this.query = query;
        this.initAbstractTable = initAbstractTable;
        vector.addAll(vc);
    }

    private void encode(Table table) {
        if (!initAbstractTable.contains(table)) {
            vector.addAll(new ArrayList<Boolean>(
                    Collections.nCopies(initAbstractTable.getRows().size(), false)));
            return;
        }
        HashMap<String, Integer> occursInAbstractTable = new HashMap<>();

        for (String repRow : initAbstractTable.getRowsRepresentation()) {
            if (!occursInAbstractTable.containsKey(repRow)) {
                occursInAbstractTable.put(repRow, 0);
            }
            int occurs = occursInAbstractTable.get(repRow);
            vector.add(table.containsRow(repRow) && occurs < table.getRowOccur(repRow));
            occursInAbstractTable.put(repRow, occurs + 1);
        }
    }

    public Table decode() {
        String name = Utils.generateRandomString(6);
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < vector.size(); i++) {
            if (vector.get(i)) {
                rows.add(initAbstractTable.getRows().get(i));
            }
        }
        return new Table(name, initAbstractTable.getColumns(), initAbstractTable.getColumnTypes(), rows);
    }

    public ArrayList<Boolean> crossProduct(ArrayList<Boolean> thatVector) {
        ArrayList<Boolean> res = new ArrayList<>();
        for (boolean thatBool : thatVector) {
            for (int i = vector.size() - 1; i >= 0; i--) {
                res.add(thatBool && vector.get(i));
            }
        }
        return res;
    }

    public ArrayList<Boolean> and(ArrayList<Boolean> thatVector) {
        ArrayList<Boolean> res = new ArrayList<>();
        for (int i = 0; i < vector.size(); i++) {
            res.add(vector.get(i) && thatVector.get(i));
        }
        return res;
    }

    public ArrayList<Boolean> concatenate(ArrayList<Boolean> thatVector) {
        ArrayList<Boolean> res = new ArrayList<>(vector);
        res.addAll(thatVector);
        return res;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[ ");
        for (boolean bool : vector) {
            builder.append(bool).append(" ");
        }
        builder.append("]");
        return query.toString() + "\n" + initAbstractTable.toString() + "\n" + decode() + "\n" + builder;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BitVector that)) {
            return false;
        }
        if (vector.size() != that.getVector().size()) {
            return false;
        }
        for (int i = 0; i < vector.size(); i++) {
            if (vector.get(i) != that.getVector().get(i)) {
                return false;
            }
        }
        return true;
    }
}
