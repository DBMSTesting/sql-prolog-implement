package sqlancer.mysql.gen;

import java.util.*;
import java.util.stream.Collectors;
import sqlancer.IgnoreMeException;
import sqlancer.Randomly;
import sqlancer.common.gen.UntypedExpressionGenerator;

import sqlancer.mysql.*;
import sqlancer.mysql.MySQLSchema.MySQLColumn;
import sqlancer.mysql.MySQLSchema.MySQLRowValue;
import sqlancer.mysql.ast.*;
import sqlancer.mysql.ast.MySQLBinaryComparisonOperation.BinaryComparisonOperator;
import sqlancer.mysql.ast.MySQLBinaryLogicalOperation.MySQLBinaryLogicalOperator;
//import sqlancer.mysql.ast.MySQLBinaryOperation.MySQLBinaryOperator;
//import sqlancer.mysql.ast.MySQLComputableFunction.MySQLFunction;
import sqlancer.mysql.ast.MySQLConstant.MySQLDoubleConstant;
import sqlancer.mysql.ast.MySQLOrderByTerm.MySQLOrder;
import sqlancer.mysql.ast.MySQLUnaryPrefixOperation.MySQLUnaryPrefixOperator;
//import sqlancer.tidb.ast.TiDBFunctionCall;

public class MySQLExpressionGenerator extends UntypedExpressionGenerator<MySQLExpression, MySQLColumn> {

    private final MySQLGlobalState state;
    private MySQLRowValue rowVal;


    public MySQLExpressionGenerator(MySQLGlobalState state) {
        this.state = state;
    }

    public MySQLExpressionGenerator setRowVal(MySQLRowValue rowVal) {
        this.rowVal = rowVal;
        return this;
    }

//    private enum Actions {
//        COLUMN, LITERAL, UNARY_PREFIX_OPERATION, UNARY_POSTFIX, COMPUTABLE_FUNCTION, BINARY_LOGICAL_OPERATOR,
//        BINARY_COMPARISON_OPERATION, CAST, IN_OPERATION, BINARY_OPERATION, EXISTS, BETWEEN_OPERATOR;
//    }
    private enum Actions {
        COLUMN, LITERAL, UNARY_PREFIX_OPERATION, UNARY_POSTFIX,  BINARY_LOGICAL_OPERATOR,
        BINARY_COMPARISON_OPERATION, IN_OPERATION,  EXISTS, FUNCTION,COMPUTABLE_FUNCTION, BINARY_OPERATION;
    }

    @Override
    public MySQLExpression generateExpression(int depth,String currentCov) {
        String option;
        //System.out.println(currentCov);

        if(!currentCov.equals("") && !currentCov.equals("delete")&& !currentCov.equals("NO SUBQUERY")){
            option = currentCov;
        }else{
            option = Randomly.fromOptions(Actions.values()).toString();
            if(currentCov.equals("delete") && (option.equals("BINARY_COMPARISON_OPERATION")||option.equals("IN_OPERATION")||option.equals("EXISTS"))){
                option = "LITERAL";
            }
        }
        if (depth >= state.getOptions().getMaxExpressionDepth()) {
            return generateLeafNode();
        }
        if(currentCov.equals("NO SUBQUERY")){
            currentCov = "NO SUBQUERY";
            option = Randomly.fromOptions("UNARY_POSTFIX","BINARY_LOGICAL_OPERATOR","BINARY_COMPARISON_OPERATION","IN_OPERATION");
        }
//        else{
//            currentCov = "";
//        }


        //System.out.println(option+"kkkkk"+currentCov);

        switch (option) {
        case "COLUMN":
            return generateColumn();
        case "LITERAL":
            return generateConstant();
        //需要加入子查询
        case "UNARY_PREFIX_OPERATION":
            //System.out.println("??????????");
            MySQLExpression subExpr = generateExpression(depth + 1,currentCov);
            //System.out.println("6666666666");
            MySQLUnaryPrefixOperator random = MySQLUnaryPrefixOperator.getRandom();
            if (random == MySQLUnaryPrefixOperator.MINUS) {
                //System.out.println("??????????");
                // workaround for https://bugs.mysql.com/bug.php?id=99122
                //System.out.println("77777777");
                throw new IgnoreMeException();
            }
            //System.out.println("888888888");
            return new MySQLUnaryPrefixOperation(subExpr, random);
        case "UNARY_POSTFIX":
            return new MySQLUnaryPostfixOperation(generateExpression(depth + 1,currentCov),
                    Randomly.fromOptions(MySQLUnaryPostfixOperation.UnaryPostfixOperator.values()),
                    Randomly.getBoolean());
        case "COMPUTABLE_FUNCTION":
            return getComputableFunction(depth + 1);
        case "BINARY_LOGICAL_OPERATOR":
            return new MySQLBinaryLogicalOperation(generateExpression(depth + 1,currentCov), generateExpression(depth + 1,currentCov),
                    MySQLBinaryLogicalOperator.getRandom());
        case "FUNCTION":
            MySQLFunctionCall.MySQLFunction func = MySQLFunctionCall.MySQLFunction.getRandom();
            return new MySQLFunctionCall(func, generateExpressions(func.getNrArgs(), depth,currentCov));
        //需要加入子查询
        case "BINARY_COMPARISON_OPERATION":
            Random randomComparison = new Random();
            switch (randomComparison.nextInt(2)){
                case 0:
                    return new MySQLBinaryComparisonCheckOperation(generateExpression(depth + 1,currentCov), generateExpression(depth + 1,currentCov),
                            MySQLBinaryComparisonCheckOperation.BinaryComparisonCheckOperator.getRandom(),null);
                case 1:
                    if(currentCov.equals("NO SUBQUERY")){
                        return new MySQLBinaryComparisonCheckOperation(generateExpression(depth + 1,currentCov), generateExpression(depth + 1,currentCov),
                                MySQLBinaryComparisonCheckOperation.BinaryComparisonCheckOperator.getRandom(),null);
                    }else{
                        return new MySQLBinaryComparisonOperation(generateExpression(depth + 1,currentCov), generateExpression(depth + 1,currentCov),
                                BinaryComparisonOperator.getRandom(),generateSubQuery(depth));
                    }

            }

//        case "CAST":
//            return new MySQLCastOperation(generateExpression(depth + 1,""), MySQLCastOperation.CastType.getRandom());
        //需要加入子查询
        case "IN_OPERATION":
            MySQLExpression expr = generateExpression(depth + 1,currentCov);
            List<MySQLExpression> rightList = new ArrayList<>();

            for (int i = 0; i < 1 + Randomly.smallNumber(); i++) {
                rightList.add(generateExpression(depth + 1,currentCov));
            }


            Random randomIn = new Random();

            switch (randomIn.nextInt(2)){
                case 0:
                    //System.out.println("0这一步");
                    return new MySQLInOperation(expr, rightList, Randomly.getBoolean(),null);
                case 1:
                    if(currentCov.equals("NO SUBQUERY")){
                        //System.out.println("1NO SUBQUERY");
                        return new MySQLInOperation(expr, rightList, Randomly.getBoolean(),null);
                    }else{
                        //System.out.println("1SUBQUERY");
                        return new MySQLInOperation(expr, rightList, Randomly.getBoolean(),generateSubQuery(depth));
                    }


            }
        case "BINARY_OPERATION":
            //System.out.println("lllllllllllll");
            if (MySQLBugs.bug99135) {
                //System.out.println("ttttttttttttt");
                throw new IgnoreMeException();
            }
            //System.out.println("wwwwwwwwwwww");
            return new MySQLBinaryOperation(generateExpression(depth + 1,currentCov), generateExpression(depth + 1,currentCov),
                    MySQLBinaryOperation.MySQLBinaryOperator.getRandom());
        case "EXISTS":
            return getExists(depth,currentCov);
//        case "BETWEEN_OPERATOR":
//            if (MySQLBugs.bug99181) {
//                // TODO: there are a number of bugs that are triggered by the BETWEEN operator
//                throw new IgnoreMeException();
//            }
//            return new MySQLBetweenOperation(generateExpression(depth + 1,""), generateExpression(depth + 1,""),
//                    generateExpression(depth + 1,""));
        default:
            System.out.println(option);
            throw new AssertionError();
        }
    }

    private MySQLExpression getExists(int depth,String currentCov) {
//        if (Randomly.getBoolean()) {
//            return new MySQLExists(new MySQLStringExpression("SELECT 1", MySQLConstant.createTrue()));
//        } else {
//            return new MySQLExists(new MySQLStringExpression("SELECT 1 wHERE FALSE", MySQLConstant.createFalse()));
//        }

        if(currentCov.equals("NO SUBQUERY")){
            return new MySQLExists(new MySQLStringExpression("SELECT NULL", MySQLConstant.createFalse()));
        }else{
            return new MySQLExists(new MySQLStringExpression(generateSubQuery(depth),MySQLConstant.createFalse()));
        }

            //return new MySQLExists(new MySQLStringExpression("SELECT NULL", MySQLConstant.createFalse()));


    }
    MySQLSchema s;
    MySQLSchema.MySQLTables targetTables;
    MySQLExpressionGenerator gen;
    MySQLSelect select;
    private String generateSubQuery(int depth) {
        s = state.getSchema();
        targetTables = s.getRandomTableNonEmptyTables();
        gen = new MySQLExpressionGenerator(state).setColumns(targetTables.getColumns());

        select = new MySQLSelect();
        select.setFetchColumns(generateFetchColumns());
        List<MySQLSchema.MySQLTable> tables = targetTables.getTables();
        List<MySQLExpression> tableList = tables.stream().map(t -> new MySQLTableReference(t))
                .collect(Collectors.toList());
        List<MySQLExpression> joins = MySQLJoin.getJoins(tableList, state);
        select.setJoinList(joins);
        select.setFromList(tableList);
        select.setWhereClause(null);

        select.setWhereClause(null);

        if (Randomly.getBoolean()) {
            select.setOrderByExpressions(gen.generateOrderBys(""));
        }
        select.setOrderByExpressions(Collections.emptyList());

        MySQLExpression expression = generateExpression(depth,"");
        select.setWhereClause(expression);

        return "select * from("+MySQLVisitor.subQueryAsString(select)+" limit 1 )tempTable ";
    }

    List<MySQLExpression> generateFetchColumns() {
        return Arrays.asList(MySQLColumnReference.create(targetTables.getColumns().get(0), null));
    }

    private MySQLExpression getComputableFunction(int depth) {
        MySQLComputableFunction.MySQLFunction func = MySQLComputableFunction.MySQLFunction.getRandomFunction();
        int nrArgs = func.getNrArgs();
        if (func.isVariadic()) {
            nrArgs += Randomly.smallNumber();
        }
        //if(func.type.equals(""))
        MySQLExpression[] args = new MySQLExpression[nrArgs];
        for (int i = 0; i < args.length; i++) {
            args[i] = generateExpression(depth + 1,"");
        }
        return new MySQLComputableFunction(func, args);
    }

    private enum ConstantType {
        INT, NULL, STRING, DOUBLE;

        public static ConstantType[] valuesPQS() {
            return new ConstantType[] { INT, NULL, STRING };
        }
    }

    @Override
    public MySQLExpression generateConstant() {
        ConstantType[] values;
        if (state.usesPQS()) {
            values = ConstantType.valuesPQS();
        } else {
            values = ConstantType.values();
        }
        switch (Randomly.fromOptions(values)) {
        case INT:
            return MySQLConstant.createIntConstant((int) state.getRandomly().getInteger());
        case NULL:
            return MySQLConstant.createNullConstant();
        case STRING:
            /* Replace characters that still trigger open bugs in MySQL */
            String string = state.getRandomly().getString().replace("\\", "").replace("\n", "");
            if (string.startsWith("\n")) {
                // workaround for https://bugs.mysql.com/bug.php?id=99130
                throw new IgnoreMeException();
            }
            if (string.startsWith("-0") || string.startsWith("0.") || string.startsWith(".")) {
                // https://bugs.mysql.com/bug.php?id=99145
                throw new IgnoreMeException();
            }
            MySQLConstant createStringConstant = MySQLConstant.createStringConstant(string);
            // if (Randomly.getBoolean()) {
            // return new MySQLCollate(createStringConstant,
            // Randomly.fromOptions("ascii_bin", "binary"));
            // }
            if (string.startsWith("1e")) {
                // https://bugs.mysql.com/bug.php?id=99146
                throw new IgnoreMeException();
            }
            return createStringConstant;
        case DOUBLE:
            double val = state.getRandomly().getDouble();
            if (Math.abs(val) <= 1 && val != 0) {
                // https://bugs.mysql.com/bug.php?id=99145
                throw new IgnoreMeException();
            }
            if (Math.abs(val) > 1.0E30) {
                // https://bugs.mysql.com/bug.php?id=99146
                throw new IgnoreMeException();
            }
            return new MySQLDoubleConstant(val);
        default:
            throw new AssertionError();
        }
    }

    @Override
    protected MySQLExpression generateColumn() {
        MySQLColumn c = Randomly.fromList(columns);
        MySQLConstant val;
        if (rowVal == null) {
            val = null;
        } else {
            val = rowVal.getValues().get(c);
        }
        return MySQLColumnReference.create(c, val);
    }

    @Override
    public MySQLExpression negatePredicate(MySQLExpression predicate) {
        return new MySQLUnaryPrefixOperation(predicate, MySQLUnaryPrefixOperator.NOT);
    }

    @Override
    public MySQLExpression isNull(MySQLExpression expr) {
        return new MySQLUnaryPostfixOperation(expr, MySQLUnaryPostfixOperation.UnaryPostfixOperator.IS_NULL, false);
    }

    @Override
    public List<MySQLExpression> generateOrderBys(String str) {
        List<MySQLExpression> expressions = super.generateOrderBys("");
        List<MySQLExpression> newOrderBys = new ArrayList<>();
        for (MySQLExpression expr : expressions) {
            if (Randomly.getBoolean()) {
                MySQLOrderByTerm newExpr = new MySQLOrderByTerm(expr, MySQLOrder.getRandomOrder());
                newOrderBys.add(newExpr);
            } else {
                newOrderBys.add(expr);
            }
        }
        return newOrderBys;
    }

}
