//package ir.teias.grammar.aggregator;
//
//public class Avg extends OneArgAggregator {
//    public Avg(String argColumn, String newName) {
//        super(argColumn, newName);
//    }
//    public Avg(String argColumn) {
//        this(argColumn, null);
//    }
//
//    @Override
//    public String toString() {
//        return "AVG(" + argColumn + ") AS " + newColumnName();
//    }
//
//    @Override
//    protected String aggregatorName() {
//        return "avg";
//    }
//}
