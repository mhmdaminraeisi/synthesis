//package ir.teias.grammar.aggregator;
//
//public class Sum extends OneArgAggregator {
//    public Sum(String argColumn, String newName) {
//        super(argColumn, newName);
//    }
//    public Sum(String argColumn) {
//        this(argColumn, null);
//    }
//
//    @Override
//    public String toString() {
//        return "SUM(" + argColumn + ") AS " + newColumnName();
//    }
//
//    @Override
//    protected String aggregatorName() {
//        return "sum";
//    }
//}
