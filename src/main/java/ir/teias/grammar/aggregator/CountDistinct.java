//package ir.teias.grammar.aggregator;
//
//public class CountDistinct extends OneArgAggregator {
//
//    public CountDistinct(String argColumn, String newName) {
//        super(argColumn, newName);
//    }
//    public CountDistinct(String argColumn) {
//        this(argColumn, null);
//    }
//
//    @Override
//    public String toString() {
//        return "COUNT(DISTINCT " + argColumn + ") AS " + newColumnName();
//    }
//
//    @Override
//    protected String aggregatorName() {
//        return "count_distinct";
//    }
//}
