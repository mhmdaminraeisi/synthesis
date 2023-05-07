//package ir.teias.grammar.aggregator;
//
//public class Count extends OneArgAggregator {
//
//    public Count(String argColumn, String newName) {
//        super(argColumn, newName);
//    }
//    public Count(String argColumn) {
//        this(argColumn, null);
//    }
//
//    @Override
//    public String toString() {
//        return "COUNT(" + argColumn + ") AS " + newColumnName();
//    }
//
//    @Override
//    protected String aggregatorName() {
//        return "count";
//    }
//}
