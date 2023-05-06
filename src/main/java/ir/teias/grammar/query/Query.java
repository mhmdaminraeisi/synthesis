package ir.teias.grammar.query;

import ir.teias.Utils;
import lombok.Getter;

@Getter
public class Query {
    private final String queryName = Utils.generateRandomString(6);
}
