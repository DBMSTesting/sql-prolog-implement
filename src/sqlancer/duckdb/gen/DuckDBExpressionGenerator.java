package sqlancer.duckdb.gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sqlancer.IgnoreMeException;
import sqlancer.Randomly;
import sqlancer.common.ast.BinaryOperatorNode.Operator;
import sqlancer.common.ast.newast.*;
import sqlancer.common.ast.newast.NewOrderingTerm.Ordering;
import sqlancer.common.gen.UntypedExpressionGenerator;
import sqlancer.duckdb.DuckDBProvider.DuckDBGlobalState;
import sqlancer.duckdb.DuckDBSchema.DuckDBColumn;
import sqlancer.duckdb.DuckDBSchema.DuckDBCompositeDataType;
import sqlancer.duckdb.DuckDBSchema.DuckDBDataType;
import sqlancer.duckdb.DuckDBToStringVisitor;
import sqlancer.duckdb.ast.DuckDBConstant;
import sqlancer.duckdb.ast.DuckDBExpression;
import sqlancer.duckdb.DuckDBSchema;
import sqlancer.duckdb.ast.DuckDBJoin;
import sqlancer.duckdb.ast.DuckDBSelect;

public final class DuckDBExpressionGenerator extends UntypedExpressionGenerator<Node<DuckDBExpression>, DuckDBColumn> {

    private final DuckDBGlobalState globalState;

    public DuckDBExpressionGenerator(DuckDBGlobalState globalState) {
        this.globalState = globalState;
    }

//    private enum Expression {
//        UNARY_POSTFIX, UNARY_PREFIX, BINARY_COMPARISON, BINARY_LOGICAL, BINARY_ARITHMETIC, CAST, FUNC, BETWEEN, CASE,
//        IN, COLLATE, LIKE_ESCAPE
//    }
    private enum Expression {
        UNARY_POSTFIX, UNARY_PREFIX, BINARY_COMPARISON, BINARY_LOGICAL, BINARY_ARITHMETIC, FUNC, BETWEEN,
        IN
    }

    @Override
    protected Node<DuckDBExpression> generateExpression(int depth,String currentCov) {
        if (depth >= globalState.getOptions().getMaxExpressionDepth() || Randomly.getBoolean()) {
            return generateLeafNode();
        }
        if (allowAggregates && Randomly.getBoolean()) {
            DuckDBAggregateFunction aggregate = DuckDBAggregateFunction.getRandom();
            allowAggregates = false;
            return new NewFunctionNode<>(generateExpressions(aggregate.getNrArgs(), depth + 1,""), aggregate);
        }
        List<Expression> possibleOptions = new ArrayList<>(Arrays.asList(Expression.values()));
//        if (!globalState.getDbmsSpecificOptions().testCollate) {
//            possibleOptions.remove(Expression.COLLATE);
//        }
        if (!globalState.getDbmsSpecificOptions().testFunctions) {
            possibleOptions.remove(Expression.FUNC);
        }
//        if (!globalState.getDbmsSpecificOptions().testCasts) {
//            possibleOptions.remove(Expression.CAST);
//        }
        if (!globalState.getDbmsSpecificOptions().testBetween) {
            possibleOptions.remove(Expression.BETWEEN);
        }
        if (!globalState.getDbmsSpecificOptions().testIn) {
            possibleOptions.remove(Expression.IN);
        }
//        if (!globalState.getDbmsSpecificOptions().testCase) {
//            possibleOptions.remove(Expression.CASE);
//        }
        if (!globalState.getDbmsSpecificOptions().testBinaryComparisons) {
            possibleOptions.remove(Expression.BINARY_COMPARISON);
        }
        if (!globalState.getDbmsSpecificOptions().testBinaryLogicals) {
            possibleOptions.remove(Expression.BINARY_LOGICAL);
        }
        Expression expr = Randomly.fromList(possibleOptions);
        switch (expr) {
//        case COLLATE:
//            return new NewUnaryPostfixOperatorNode<DuckDBExpression>(generateExpression(depth + 1,""),
//                    DuckDBCollate.getRandom());
        case UNARY_PREFIX:
            return new NewUnaryPrefixOperatorNode<DuckDBExpression>(generateExpression(depth + 1,""),
                    DuckDBUnaryPrefixOperator.getRandom());
        case UNARY_POSTFIX:
            return new NewUnaryPostfixOperatorNode<DuckDBExpression>(generateExpression(depth + 1,""),
                    DuckDBUnaryPostfixOperator.getRandom());
        case BINARY_COMPARISON:
            Operator op = DuckDBBinaryComparisonOperator.getRandom();
            return new NewBinaryOperatorNode<DuckDBExpression>(generateExpression(depth + 1,""),
                    generateExpression(depth + 1,""), op,generateSubQuery(depth+1));
        case BINARY_LOGICAL:
            op = DuckDBBinaryLogicalOperator.getRandom();
            return new NewBinaryOperatorNode<DuckDBExpression>(generateExpression(depth + 1,""),
                    generateExpression(depth + 1,""), op,null);
        case BINARY_ARITHMETIC:
            return new NewBinaryOperatorNode<DuckDBExpression>(generateExpression(depth + 1,""),
                    generateExpression(depth + 1,""), DuckDBBinaryArithmeticOperator.getRandom(),null);
//        case CAST:
//            return new DuckDBCastOperation(generateExpression(depth + 1,""),
//                    DuckDBCompositeDataType.getRandomWithoutNull());
        case FUNC:
            DBFunction func = DBFunction.getRandom();
            return new NewFunctionNode<DuckDBExpression, DBFunction>(generateExpressions(func.getNrArgs(),""), func);
        case BETWEEN:
            return new NewBetweenOperatorNode<DuckDBExpression>(generateExpression(depth + 1,""),
                    generateExpression(depth + 1,""), generateExpression(depth + 1,""), Randomly.getBoolean());
        case IN:
            return new NewInOperatorNode<DuckDBExpression>(generateExpression(depth + 1,""),
                    generateExpressions(Randomly.smallNumber() + 1, depth + 1,""), Randomly.getBoolean());
//        case CASE:
//            int nr = Randomly.smallNumber() + 1;
//            return new NewCaseOperatorNode<DuckDBExpression>(generateExpression(depth + 1,""),
//                    generateExpressions(nr, depth + 1,""), generateExpressions(nr, depth + 1,""),
//                    generateExpression(depth + 1,""));
//        case LIKE_ESCAPE:
//            return new NewTernaryNode<DuckDBExpression>(generateExpression(depth + 1,""), generateExpression(depth + 1,""),
//                    generateExpression(depth + 1,""), "LIKE", "ESCAPE");
        default:
            throw new AssertionError();
        }
    }

    DuckDBSchema s;
    DuckDBSchema.DuckDBTables targetTables;
    DuckDBExpressionGenerator gen;
    DuckDBSelect select;
    private String generateSubQuery(int depth) {
        s = globalState.getSchema();
        targetTables = s.getRandomTableNonEmptyTables();
        gen = new DuckDBExpressionGenerator(globalState).setColumns(targetTables.getColumns());
        select = new DuckDBSelect();
        select.setFetchColumns(generateFetchColumns());
        List<DuckDBSchema.DuckDBTable> tables = targetTables.getTables();
        List<TableReferenceNode<DuckDBExpression, DuckDBSchema.DuckDBTable>> tableList = tables.stream()
                .map(t -> new TableReferenceNode<DuckDBExpression, DuckDBSchema.DuckDBTable>(t)).collect(Collectors.toList());
        List<Node<DuckDBExpression>> joins = DuckDBJoin.getJoins(tableList, globalState);
        select.setJoinList(joins.stream().collect(Collectors.toList()));
        select.setFromList(tableList.stream().collect(Collectors.toList()));
        select.setWhereClause(null);

        Node<DuckDBExpression> expression = generateExpression(depth,"");
        select.setWhereClause(expression);

        return "select * from("+ DuckDBToStringVisitor.asString(select)+" limit 1 ) tempTable ";
    }

    List<Node<DuckDBExpression>> generateFetchColumns() {
        List<Node<DuckDBExpression>> columns = new ArrayList<>();
        if (Randomly.getBoolean()) {
            columns.add(new ColumnReferenceNode<>(new DuckDBColumn("*", null, false, false)));
        } else {
            columns = Randomly.nonEmptySubset(targetTables.getColumns()).stream()
                    .map(c -> new ColumnReferenceNode<DuckDBExpression, DuckDBColumn>(c)).collect(Collectors.toList());
        }
        return columns;
    }

    @Override
    protected Node<DuckDBExpression> generateColumn() {
        DuckDBColumn column = Randomly.fromList(columns);
        return new ColumnReferenceNode<DuckDBExpression, DuckDBColumn>(column);
    }

    @Override
    public Node<DuckDBExpression> generateConstant() {
        if (Randomly.getBooleanWithSmallProbability()) {
            return DuckDBConstant.createNullConstant();
        }
        DuckDBDataType type = DuckDBDataType.getRandomWithoutNull();
        switch (type) {
        case INT:
            if (!globalState.getDbmsSpecificOptions().testIntConstants) {
                throw new IgnoreMeException();
            }
            return DuckDBConstant.createIntConstant(globalState.getRandomly().getInteger());
        case DATE:
            if (!globalState.getDbmsSpecificOptions().testDateConstants) {
                throw new IgnoreMeException();
            }
            return DuckDBConstant.createDateConstant(globalState.getRandomly().getInteger());
        case TIMESTAMP:
            if (!globalState.getDbmsSpecificOptions().testTimestampConstants) {
                throw new IgnoreMeException();
            }
            return DuckDBConstant.createTimestampConstant(globalState.getRandomly().getInteger());
        case VARCHAR:
            if (!globalState.getDbmsSpecificOptions().testStringConstants) {
                throw new IgnoreMeException();
            }
            return DuckDBConstant.createStringConstant(globalState.getRandomly().getString());
        case BOOLEAN:
            if (!globalState.getDbmsSpecificOptions().testBooleanConstants) {
                throw new IgnoreMeException();
            }
            return DuckDBConstant.createBooleanConstant(Randomly.getBoolean());
        case FLOAT:
            if (!globalState.getDbmsSpecificOptions().testFloatConstants) {
                throw new IgnoreMeException();
            }
            return DuckDBConstant.createFloatConstant(globalState.getRandomly().getDouble());
        default:
            throw new AssertionError();
        }
    }

    @Override
    public List<Node<DuckDBExpression>> generateOrderBys(String currentCov) {
        List<Node<DuckDBExpression>> expr = super.generateOrderBys(currentCov);
        List<Node<DuckDBExpression>> newExpr = new ArrayList<>(expr.size());
        for (Node<DuckDBExpression> curExpr : expr) {
            if (Randomly.getBoolean()) {
                curExpr = new NewOrderingTerm<>(curExpr, Ordering.getRandom());
            }
            newExpr.add(curExpr);
        }
        return newExpr;
    };

    public static class DuckDBCastOperation extends NewUnaryPostfixOperatorNode<DuckDBExpression> {

        public DuckDBCastOperation(Node<DuckDBExpression> expr, DuckDBCompositeDataType type) {
            super(expr, new Operator() {

                @Override
                public String getTextRepresentation() {
                    return "::" + type.toString();
                }
            });
        }

    }

    public enum DuckDBAggregateFunction {
        MAX(1), MIN(1), AVG(1), COUNT(1),SUM(1);
//        STRING_AGG(1), FIRST(1),  STDDEV_SAMP(1), STDDEV_POP(1), VAR_POP(1),
//        VAR_SAMP(1), COVAR_POP(1), COVAR_SAMP(1);

        private int nrArgs;

        DuckDBAggregateFunction(int nrArgs) {
            this.nrArgs = nrArgs;
        }

        public static DuckDBAggregateFunction getRandom() {
            return Randomly.fromOptions(values());
        }

        public int getNrArgs() {
            return nrArgs;
        }

    }

    public enum DBFunction {
        // trigonometric functions
        LCASE(1),
        LPAD(3),
        RPAD(3),
        UCASE(1),
        LEFT(2),
        //LOCATE(2),
        RIGHT(2),
        //STRCMP(2),
        SUBSTR(2),
        //MID(3),
        REPEAT(2),
        //SUBSTRING_INDEX(3),
        ASCII(1),
        //FIND_IN_SET(2),
        //POSITION(2),
        //SPACE(1),
        FORMAT(2),
        //INSERT(4),
        //FIELD(2),
        //CHAR(1),
        //ELT(2),
        //CHAR_LENGTH(1),
        //CHARACTER_LENGTH(1),
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
        //BIN(1),
        //OCT(1),
        //SQUARE(1),

        SQRT(1), //
        POWER(2), //
        CBRT(1), //
        ROUND(2), //
        SIGN(1), //
        DEGREES(1), //
        RADIANS(1), //
        MOD(2), //
        XOR(2), //
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
        //STRIP_ACCENTS(1), //

        // date functions
        //DATE_PART(2), AGE(2),

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

        DBFunction(int nrArgs) {
            this(nrArgs, false);
        }

        DBFunction(int nrArgs, boolean isVariadic) {
            this.nrArgs = nrArgs;
            this.isVariadic = isVariadic;
        }

        public static DBFunction getRandom() {
            return Randomly.fromOptions(values());
        }

        public int getNrArgs() {
            if (isVariadic) {
                return Randomly.smallNumber() + nrArgs;
            } else {
                return nrArgs;
            }
        }

    }

    public enum DuckDBUnaryPostfixOperator implements Operator {

        IS_NULL("IS NULL"), IS_NOT_NULL("IS NOT NULL"),is_TRUE("IS TRUE"),is_FALSE("IS FALSE");

        private String textRepr;

        DuckDBUnaryPostfixOperator(String textRepr) {
            this.textRepr = textRepr;
        }

        @Override
        public String getTextRepresentation() {
            return textRepr;
        }

        public static DuckDBUnaryPostfixOperator getRandom() {
            return Randomly.fromOptions(values());
        }

    }

    public static final class DuckDBCollate implements Operator {

        private final String textRepr;

        private DuckDBCollate(String textRepr) {
            this.textRepr = textRepr;
        }

        @Override
        public String getTextRepresentation() {
            return "COLLATE " + textRepr;
        }

        public static DuckDBCollate getRandom() {
            return new DuckDBCollate(DuckDBTableGenerator.getRandomCollate());
        }

    }

    public enum DuckDBUnaryPrefixOperator implements Operator {

        NOT("NOT"), PLUS("+"), MINUS("-");

        private String textRepr;

        DuckDBUnaryPrefixOperator(String textRepr) {
            this.textRepr = textRepr;
        }

        @Override
        public String getTextRepresentation() {
            return textRepr;
        }

        public static DuckDBUnaryPrefixOperator getRandom() {
            return Randomly.fromOptions(values());
        }

    }

    public enum DuckDBBinaryLogicalOperator implements Operator {

        AND, OR;

        @Override
        public String getTextRepresentation() {
            return toString();
        }

        public static Operator getRandom() {
            return Randomly.fromOptions(values());
        }

    }

    public enum DuckDBBinaryArithmeticOperator implements Operator {
        CONCAT("||"), ADD("+"), SUB("-"), MULT("*"), DIV("/"), MOD("%"),
        AND("&"), OR("|"), LSHIFT("<<"), RSHIFT(">>"), XOR("^");

        private String textRepr;

        DuckDBBinaryArithmeticOperator(String textRepr) {
            this.textRepr = textRepr;
        }

        public static Operator getRandom() {
            return Randomly.fromOptions(values());
        }

        @Override
        public String getTextRepresentation() {
            return textRepr;
        }

    }

    public enum DuckDBBinaryComparisonOperator implements Operator {
        EQUALS("="), GREATER(">"), GREATER_EQUALS(">="), SMALLER("<"), SMALLER_EQUALS("<="), NOT_EQUALS("!=");
//        LIKE("LIKE"), NOT_LIKE("NOT LIKE"), SIMILAR_TO("SIMILAR TO"), NOT_SIMILAR_TO("NOT SIMILAR TO"),
//        REGEX_POSIX("~"), REGEX_POSIT_NOT("!~");

        private String textRepr;

        DuckDBBinaryComparisonOperator(String textRepr) {
            this.textRepr = textRepr;
        }

        public static Operator getRandom() {
            return Randomly.fromOptions(values());
        }

        @Override
        public String getTextRepresentation() {
            return textRepr;
        }

    }

    public NewFunctionNode<DuckDBExpression, DuckDBAggregateFunction> generateArgsForAggregate(
            DuckDBAggregateFunction aggregateFunction) {
        return new NewFunctionNode<DuckDBExpression, DuckDBExpressionGenerator.DuckDBAggregateFunction>(
                generateExpressions(aggregateFunction.getNrArgs(),""), aggregateFunction);
    }

    public Node<DuckDBExpression> generateAggregate() {
        DuckDBAggregateFunction aggrFunc = DuckDBAggregateFunction.getRandom();
        return generateArgsForAggregate(aggrFunc);
    }

    @Override
    public Node<DuckDBExpression> negatePredicate(Node<DuckDBExpression> predicate) {
        return new NewUnaryPrefixOperatorNode<>(predicate, DuckDBUnaryPrefixOperator.NOT);
    }

    @Override
    public Node<DuckDBExpression> isNull(Node<DuckDBExpression> expr) {
        return new NewUnaryPostfixOperatorNode<>(expr, DuckDBUnaryPostfixOperator.IS_NULL);
    }

}
