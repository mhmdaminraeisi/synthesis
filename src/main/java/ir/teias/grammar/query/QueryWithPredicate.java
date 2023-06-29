package ir.teias.grammar.query;

import ir.teias.grammar.binop.Equal;
import ir.teias.grammar.binop.LessThan;
import ir.teias.grammar.predicate.BinaryOperator;
import ir.teias.grammar.predicate.Or;
import ir.teias.grammar.predicate.Predicate;
import ir.teias.grammar.predicate.True;
import ir.teias.grammar.value.Value;
import ir.teias.model.BitVector;
import ir.teias.model.Table;
import ir.teias.model.cell.Cell;
import ir.teias.model.cell.CellType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Getter
@RequiredArgsConstructor
public abstract class QueryWithPredicate extends Query {
    protected final Predicate predicate;

    @Setter
    protected HashMap<CellType, List<Cell<?>>> constantsByType;

    public abstract QueryWithPredicate duplicateWithNewPredicate(Predicate predicate);

    public abstract List<Predicate> enumeratePrimitivePredicates();

    public List<Predicate> enumerateBinOpPredicates(List<Value> lefts, List<Value> rights, boolean isSame, boolean isId) {
        List<Predicate> binOpPredicates = new ArrayList<>();
        for (int i = 0; i < lefts.size(); i++) {
            for (int j = isSame ? i + 1 : 0; j < rights.size(); j++) {
                binOpPredicates.add(new BinaryOperator(lefts.get(i), rights.get(j), new Equal()));
                if (isId) {
                    continue;
                }
                binOpPredicates.add(new BinaryOperator(lefts.get(i), rights.get(j), new LessThan()));
                // TODO uncomment below lines
//                    primitivesPredicates.add(new BinaryOperator(leftValue, rightValue, new GreaterThanEqual()));
//                    primitivesPredicates.add(new BinaryOperator(leftValue, rightValue, new LessThan()));
//                    primitivesPredicates.add(new BinaryOperator(leftValue, rightValue, new LessThanEqual()));
            }
        }
        return binOpPredicates;
    }

    public List<Predicate> enumAndGroupPredicates() {
        List<Predicate> representativePredicates = new ArrayList<>();
        List<Table> repTables = new ArrayList<>();
        List<Predicate> primitivesPredicates = enumeratePrimitivePredicates();
        addNonEquivalentPredicates(primitivesPredicates, representativePredicates, repTables);

        addNonEquivalentPredicates(List.of(new True()), representativePredicates, repTables);

        List<Predicate> compoundPredicates = getCompoundPredicates(representativePredicates);
        addNonEquivalentPredicates(compoundPredicates, representativePredicates, repTables);
        return representativePredicates;
    }

    private void addNonEquivalentPredicates(List<Predicate> newPredicates, List<Predicate> repPredicates,
                                            List<Table> repTables) {
        for (Predicate predicate : newPredicates) {

            QueryWithPredicate newQuery = duplicateWithNewPredicate(predicate);
            Table table = newQuery.evaluateAbstract();
            if (table.getRows().size() == 0) {
                continue;
            }
            boolean shouldAdd = true;
            for (Table repTable : repTables) {
                if (table.equals(repTable)) {
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd) {
                repPredicates.add(predicate);
                repTables.add(table);
            }
        }
    }

    private List<Predicate> getCompoundPredicates(List<Predicate> repPredicates) {
        List<Predicate> compoundPredicates = new ArrayList<>();
        for (int i = 0; i < repPredicates.size(); i++) {
            for (int j = i + 1; j < repPredicates.size(); j++) {
                compoundPredicates.add(new Or(repPredicates.get(i), repPredicates.get(j)));
                // TODO add another predicates
            }
        }
        return compoundPredicates;
    }

    protected List<BitVector> encodeFiltersToBitVectors(List<Predicate> predicates) {
        return predicates.stream().map(pred -> {
            QueryWithPredicate newQuery = duplicateWithNewPredicate(pred);
            return new BitVector(newQuery, getAbstractTable(), true);
        }).toList();
    }
}
