package sqlancer.tidb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import sqlancer.IgnoreMeException;
import sqlancer.Randomly;
import sqlancer.common.gen.UntypedExpressionGenerator;

import sqlancer.tidb.TiDBProvider.TiDBGlobalState;
import sqlancer.tidb.TiDBSchema.TiDBColumn;
import sqlancer.tidb.TiDBSchema.TiDBDataType;
import sqlancer.tidb.ast.*;
import sqlancer.tidb.ast.TiDBAggregate.TiDBAggregateFunction;
//import sqlancer.tidb.ast.TiDBBinaryBitOperation.TiDBBinaryBitOperator;
//import sqlancer.tidb.ast.TiDBBinaryComparisonOperation.TiDBComparisonOperator;
import sqlancer.tidb.ast.TiDBBinaryLogicalOperation.TiDBBinaryLogicalOperator;
import sqlancer.tidb.ast.TiDBFunctionCall.TiDBFunction;
//import sqlancer.tidb.ast.TiDBRegexOperation.TiDBRegexOperator;
import sqlancer.tidb.ast.TiDBUnaryPostfixOperation.TiDBUnaryPostfixOperator;
import sqlancer.tidb.ast.TiDBUnaryPrefixOperation.TiDBUnaryPrefixOperator;
import sqlancer.tidb.gen.TiDBHintGenerator;
import sqlancer.tidb.visitor.TiDBVisitor;

public class TiDBExpressionGenerator extends UntypedExpressionGenerator<TiDBExpression, TiDBColumn> {

    private final TiDBGlobalState globalState;

    public TiDBExpressionGenerator(TiDBGlobalState globalState) {
        this.globalState = globalState;
    }

//    private enum Gen {
//        UNARY_PREFIX, //
//        UNARY_POSTFIX, //
//        CONSTANT, //
//        COLUMN, //
//        COMPARISON, REGEX, FUNCTION, BINARY_LOGICAL, BINARY_BIT, CAST, DEFAULT, CASE
//        // BINARY_ARITHMETIC
//    }
    private enum Gen {
        UNARY_PREFIX, //
        UNARY_POSTFIX, //
        CONSTANT, //
        COLUMN, //
        COMPARISON, FUNCTION, BINARY_LOGICAL,  DEFAULT
        // BINARY_ARITHMETIC
    }

    @Override
    protected TiDBExpression generateExpression(int depth,String currentColumn) {
        if (depth >= globalState.getOptions().getMaxExpressionDepth() || Randomly.getBoolean()) {
            return generateLeafNode();
        }
        if (allowAggregates && Randomly.getBoolean()) {
            allowAggregates = false;
            TiDBAggregateFunction func = TiDBAggregateFunction.getRandom();
            List<TiDBExpression> args = generateExpressions(func.getNrArgs(),"");
            return new TiDBAggregate(args, func);
        }
        switch (Randomly.fromOptions(Gen.values())) {
        case DEFAULT:
            if (globalState.getSchema().getDatabaseTables().isEmpty()) {
                throw new IgnoreMeException();
            }
            //TiDBColumn column = Randomly.fromList(columns);
//            if (column.hasDefault()) {
//                return new TiDBFunctionCall(TiDBFunction.DEFAULT, Arrays.asList(new TiDBColumnReference(column)));
//            }
            throw new IgnoreMeException();
        case UNARY_POSTFIX:
            return new TiDBUnaryPostfixOperation(generateExpression(depth + 1,""), TiDBUnaryPostfixOperator.getRandom());
        case UNARY_PREFIX:
            TiDBUnaryPrefixOperator rand = TiDBUnaryPrefixOperator.getRandom();
            return new TiDBUnaryPrefixOperation(generateExpression(depth + 1,""), rand);
        case COLUMN:
            return generateColumn();
        case CONSTANT:
            return generateConstant();
        case COMPARISON:
            Random randomComparison = new Random();
            switch (randomComparison.nextInt(2)){
                case 0:
                    return new TiDBBinaryComparisonOperation(generateExpression(depth + 1,""), generateExpression(depth + 1,""),
                            TiDBBinaryComparisonOperation.TiDBComparisonOperator.getRandom(),null);
                case 1:
                    return new TiDBBinaryComparisonOperation(generateExpression(depth + 1,""), generateExpression(depth + 1,""),
                            TiDBBinaryComparisonOperation.TiDBComparisonOperator.getRandom(),generateSubQuery(depth));
            }
//        case REGEX:
//            return new TiDBRegexOperation(generateExpression(depth + 1,""), generateExpression(depth + 1,""),
//                    TiDBRegexOperator.getRandom());
        case FUNCTION:
            TiDBFunction func = TiDBFunction.getRandom();
            return new TiDBFunctionCall(func, generateExpressions(func.getNrArgs(), depth,""));
//        case BINARY_BIT:
//            return new TiDBBinaryBitOperation(generateExpression(depth + 1,""), generateExpression(depth + 1,""),
//                    TiDBBinaryBitOperator.getRandom());
        case BINARY_LOGICAL:
            return new TiDBBinaryLogicalOperation(generateExpression(depth + 1,""), generateExpression(depth + 1,""),
                    TiDBBinaryLogicalOperator.getRandom());
//        case CAST:
//            return new TiDBCastOperation(generateExpression(depth + 1,""), Randomly.fromOptions("BINARY", // https://github.com/tidb-challenge-program/bug-hunting-issue/issues/52
//                    "CHAR", "DATE", "DATETIME", "TIME", // https://github.com/tidb-challenge-program/bug-hunting-issue/issues/13
//                    "DECIMAL", "SIGNED", "UNSIGNED" /* https://github.com/pingcap/tidb/issues/16028 */));
//        case CASE:
//            int nr = Randomly.fromOptions(1, 2);
//            return new TiDBCase(generateExpression(depth + 1,""), generateExpressions(nr, depth + 1,""),
//                    generateExpressions(nr, depth + 1,""), generateExpression(depth + 1,""));
        default:
            throw new AssertionError();
        }
    }

    TiDBSchema s;
    TiDBSchema.TiDBTables targetTables;
    TiDBExpressionGenerator gen;
    TiDBSelect select;
    private String generateSubQuery(int depth) {
        s = globalState.getSchema();
        targetTables = s.getRandomTableNonEmptyTables();
        gen = new TiDBExpressionGenerator(globalState).setColumns(targetTables.getColumns());

        select = new TiDBSelect();
        select.setFetchColumns(generateFetchColumns());
        List<TiDBSchema.TiDBTable> tables = targetTables.getTables();
        if (Randomly.getBoolean()) {
            TiDBHintGenerator.generateHints(select, tables);
        }

        List<TiDBExpression> tableList = tables.stream().map(t -> new TiDBTableReference(t))
                .collect(Collectors.toList());
        List<TiDBExpression> joins = TiDBJoin.getJoins(tableList, globalState);
        select.setJoinList(joins);
        select.setFromList(tableList);
        select.setWhereClause(null);
        TiDBExpression expression = generateExpression(depth,"");
        select.setWhereClause(expression);

        return "select * from("+ TiDBVisitor.asString(select)+" limit 1 ) tempTable ";
    }

    List<TiDBExpression> generateFetchColumns() {
        return Arrays.asList(new TiDBColumnReference(targetTables.getColumns().get(0)));
    }

    @Override
    protected TiDBExpression generateColumn() {
        TiDBColumn column = Randomly.fromList(columns);
        return new TiDBColumnReference(column);
    }

    @Override
    public TiDBExpression generateConstant() {
        TiDBDataType type = TiDBDataType.getRandom();
        if (Randomly.getBooleanWithRatherLowProbability()) {
            return TiDBConstant.createNullConstant();
        }
        switch (type) {
        case INT:
            return TiDBConstant.createIntConstant(globalState.getRandomly().getInteger());
        case BLOB:
        case TEXT:
            return TiDBConstant.createStringConstant(globalState.getRandomly().getString());
        case BOOL:
            return TiDBConstant.createBooleanConstant(Randomly.getBoolean());
        case FLOATING:
            return TiDBConstant.createFloatConstant(globalState.getRandomly().getDouble());
        case CHAR:
            return TiDBConstant.createStringConstant(globalState.getRandomly().getChar());
        case DECIMAL:
        case NUMERIC:
            return TiDBConstant.createIntConstant(globalState.getRandomly().getInteger());
        default:
            throw new AssertionError();
        }
    }

    @Override
    public List<TiDBExpression> generateOrderBys(String currentCov) {
        List<TiDBExpression> expressions = super.generateOrderBys("");
        List<TiDBExpression> newExpressions = new ArrayList<>();
        for (TiDBExpression expr : expressions) {
            TiDBExpression newExpr = expr;
            if (Randomly.getBoolean()) {
                newExpr = new TiDBOrderingTerm(expr, Randomly.getBoolean());
            }
            newExpressions.add(newExpr);
        }
        return newExpressions;
    }

    @Override
    public TiDBExpression negatePredicate(TiDBExpression predicate) {
        return new TiDBUnaryPrefixOperation(predicate, TiDBUnaryPrefixOperator.NOT);
    }

    @Override
    public TiDBExpression isNull(TiDBExpression expr) {
        return new TiDBUnaryPostfixOperation(expr, TiDBUnaryPostfixOperator.IS_NULL);
    }

    public TiDBExpression generateConstant(TiDBDataType type) {
        if (Randomly.getBooleanWithRatherLowProbability()) {
            return TiDBConstant.createNullConstant();
        }
        switch (type) {
        case INT:
            return TiDBConstant.createIntConstant(globalState.getRandomly().getInteger());
        case BLOB:
        case TEXT:
            return TiDBConstant.createStringConstant(globalState.getRandomly().getString());
        case BOOL:
            return TiDBConstant.createBooleanConstant(Randomly.getBoolean());
        case FLOATING:
            return TiDBConstant.createFloatConstant(globalState.getRandomly().getDouble());
        case CHAR:
            return TiDBConstant.createStringConstant(globalState.getRandomly().getChar());
        case DECIMAL:
        case NUMERIC:
            return TiDBConstant.createIntConstant(globalState.getRandomly().getInteger());
        default:
            throw new AssertionError();
        }
    }

}
