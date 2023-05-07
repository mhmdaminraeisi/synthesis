//package ir.teias.grammar.aggregator;
//
//public class Min extends OneArgAggregator {
//
//    public Min(String argColumn, String newName) {
//        super(argColumn, newName);
//    }
//    public Min(String argColumn) {
//        this(argColumn, null);
//    }
//
//    @Override
//    public String toString() {
//        return "MIN(" + argColumn + ") AS " + newColumnName();
//    }
//
//    @Override
//    protected String aggregatorName() {
//        return "min";
//    }
//}
