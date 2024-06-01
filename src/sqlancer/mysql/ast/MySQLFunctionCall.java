package sqlancer.mysql.ast;

import sqlancer.Randomly;

import java.util.List;

public class MySQLFunctionCall implements MySQLExpression {

    private sqlancer.mysql.ast.MySQLFunctionCall.MySQLFunction function;
    private List<MySQLExpression> args;

    // https://pingcap.github.io/docs/stable/reference/sql/functions-and-operators/numeric-functions-and-operators/
    public enum MySQLFunction {

        LCASE(1),
        LPAD(3),
        RPAD(3),
        UCASE(1),
        LEFT(2),
        LOCATE(2),
        RIGHT(2),
        STRCMP(2),
        SUBSTR(2),
        MID(3),
        REPEAT(2),
        SUBSTRING_INDEX(3),
        ASCII(1),
        FIND_IN_SET(2),
        //POSITION(2),
        SPACE(1),
        FORMAT(2),
        INSERT(4),
        FIELD(2),
        CHAR(1),
        ELT(2),
        CHAR_LENGTH(1),
        CHARACTER_LENGTH(1),
        ACOS(1), //
        ASIN(1), //
        ATAN(1), //
        COS(1), //
        SIN(1), //
        TAN(1), //
        //COT(1), //
        ATAN2(1), //
        // math functions
        ABS(1), //
        CEIL(1), //
        CEILING(1), //
        FLOOR(1), //
        LOG(1), //
        LOG10(1), LOG2(1), //
        LN(1), //
        PI(0), //
        BIN(1),
        OCT(1),
        //SQUARE(1),

        SQRT(1), //
        POWER(2), //
        //CBRT(1), //
        ROUND(2), //
        SIGN(1), //
        DEGREES(1), //
        RADIANS(1), //
        MOD(2), //
        //XOR(2), //
        // string functions
        LENGTH(1), //
        LOWER(1), //
        UPPER(1), //
        SUBSTRING(3), //
        REVERSE(1), //
        CONCAT(1, true), //
        CONCAT_WS(2, true),
        //CONTAINS(2), //
        //PREFIX(2), //
        //SUFFIX(2), //
        INSTR(2), //
        //PRINTF(1, true), //
        //REGEXP_MATCHES(2), //
        //REGEXP_REPLACE(3), //

        COALESCE(3), NULLIF(2),

        // LPAD(3),
        // RPAD(3),
        LTRIM(1), RTRIM(1),
        // LEFT(2), https://github.com/cwida/duckdb/issues/633
        // REPEAT(2),
        REPLACE(3),
        //UNICODE(1),

        BIT_COUNT(1), BIT_LENGTH(1), LAST_DAY(1), MONTHNAME(1), DAYNAME(1), YEARWEEK(1), DAYOFMONTH(1), WEEKDAY(1),
        WEEKOFYEAR(1),

        IFNULL(2), IF(3);


        private int nrArgs;
        private boolean isVariadic;

        MySQLFunction(int nrArgs) {
            this.nrArgs = nrArgs;
        }

        MySQLFunction(int nrArgs, boolean isVariadic) {
            this.nrArgs = nrArgs;
            this.isVariadic = true;
        }

        public static sqlancer.mysql.ast.MySQLFunctionCall.MySQLFunction getRandom() {
            while (true) {
                sqlancer.mysql.ast.MySQLFunctionCall.MySQLFunction func = Randomly.fromOptions(values());
                if (func.getNrArgs() != -1) {
                    // special functions that need to be created manually (e.g., DEFAULT)
                    return func;
                }
            }
        }

        public int getNrArgs() {
            return nrArgs + (isVariadic() ? Randomly.smallNumber() : 0);
        }

        public boolean isVariadic() {
            return isVariadic;
        }

    }

    public MySQLFunctionCall(sqlancer.mysql.ast.MySQLFunctionCall.MySQLFunction function, List<MySQLExpression> args) {
        this.function = function;
        this.args = args;
    }

    public List<MySQLExpression> getArgs() {
        return args;
    }

    public sqlancer.mysql.ast.MySQLFunctionCall.MySQLFunction getFunction() {
        return function;
    }

}

