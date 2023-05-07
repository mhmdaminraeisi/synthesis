package ir.teias.synthesizer;

import ir.teias.grammar.query.Query;
import ir.teias.grammar.query.Select;
import ir.teias.model.Table;

import java.util.List;

public class Optimizer {
    public boolean canExtentQuery(Query query, Query nestedQuery) {
        if (query instanceof Select && nestedQuery instanceof Select) {
            return false;
        }
        return true;
    }
}
