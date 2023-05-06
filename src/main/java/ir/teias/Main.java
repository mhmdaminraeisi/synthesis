package ir.teias;

import ir.teias.model.Table;

public class Main {
    public static void main(String[] args) {
        String query = "SELECT * FROM \"Test\"";
        SQLManager sqlManager = new SQLManager();
        Table table = sqlManager.evaluate(query);
        System.out.println(table.toString());
    }
}
