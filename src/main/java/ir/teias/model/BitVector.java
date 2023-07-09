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
    private final Table initAbstractTableFull;
    private final ArrayList<Boolean> vector = new ArrayList<>();

    public BitVector(Query query, Table initAbstractTable, Table initAbstractTableFull, boolean encodeAbstract) {
        this.query = query;
        this.initAbstractTable = initAbstractTable;
        this.initAbstractTableFull = initAbstractTableFull;
        this.encode(encodeAbstract ? query.getAbstractTableFull() : query.evaluateFull());
    }

    public BitVector(ArrayList<Boolean> vc, Query query, Table initAbstractTable, Table initAbstractTableFull) {
        this.query = query;
        this.initAbstractTable = initAbstractTable;
        this.initAbstractTableFull = initAbstractTableFull;
        vector.addAll(vc);
    }

    private void encode(Table tableFull) {
        if (!initAbstractTableFull.contains(tableFull)) {
            vector.addAll(new ArrayList<Boolean>(
                    Collections.nCopies(initAbstractTable.getRows().size(), false)));
            return;
        }
        HashMap<String, Integer> occursInAbstractTableFull = new HashMap<>();

        for (String repRow : initAbstractTableFull.getRowsRepresentation()) {
            if (!occursInAbstractTableFull.containsKey(repRow)) {
                occursInAbstractTableFull.put(repRow, 0);
            }
            int occurs = occursInAbstractTableFull.get(repRow);
            vector.add(tableFull.containsRow(repRow) && occurs < tableFull.getRowOccur(repRow));
            occursInAbstractTableFull.put(repRow, occurs + 1);
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
