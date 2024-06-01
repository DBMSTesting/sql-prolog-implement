package sqlancer.sqlite3.ast;

import sqlancer.sqlite3.schema.SQLite3Schema;

public class SQLite3Exists extends SQLite3Expression {



    @Override
    public SQLite3Schema.SQLite3Column.SQLite3CollateSequence getExplicitCollateSequence() {
        return null;
    }

    private final String expr;
    private final SQLite3Constant expected;

    public SQLite3Exists(String expr, SQLite3Constant expectedValue) {
        this.expr = expr;
        this.expected = expectedValue;
    }

    public String getExpr() {
        return expr;
    }
    public SQLite3Constant getExpected() {
        return expected;
    }
}
