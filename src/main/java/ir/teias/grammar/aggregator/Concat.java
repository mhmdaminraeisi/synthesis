//package ir.teias.grammar.aggregator;
//
//public class Concat extends TwoArgsAggregator {
//
//    protected Concat(String argOneColumn, String argTwoColumn, String newName) {
//        super(argOneColumn, argTwoColumn, newName);
//    }
//    protected Concat(String argOneColumn, String argTwoColumn) {
//        this(argOneColumn, argTwoColumn, null);
//    }
//
//    @Override
//    public String toString() {
//        return "CONCAT(" + argOneColumn + ", " + argTwoColumn + ") AS " + newColumnName();
//    }
//    @Override
//    protected String aggregatorName() {
//        return "concat";
//    }
//}
