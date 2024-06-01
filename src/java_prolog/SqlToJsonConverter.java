package java_prolog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.NotExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.statement.select.Limit;

import net.sf.jsqlparser.expression.OrderByClause;
import net.sf.jsqlparser.expression.operators.arithmetic.*;

import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import java.util.*;

public class SqlToJsonConverter {
    public static void main(String[] args) {
        String sql = "select * from t2,t3 where t2.a > 1;";
        String json = convertSqlToJson(sql);
        System.out.println(json);
    }


    public static String convertSqlToJson(String sql) {
//        "select * from("+MySQLVisitor.subQueryAsString(select)+" limit 1 )tempTable "
        sql = sql.replace("select * from(","");
        sql = sql.replace(")tempTable","");
        try {
            Select select = (Select) CCJSqlParserUtil.parse(sql);
            SelectBody selectBody = select.getSelectBody();

            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            List<SelectItem> selectItems = plainSelect.getSelectItems();

            Map<String, Object> result = new HashMap<>();
            List<String> selectItem = new ArrayList<>();
            for(SelectItem item:selectItems){
                selectItem.add("'"+item.toString()+"'");
            }
            result.put("'select'", selectItem.toString());


            List<String> fromItems = new ArrayList<>();

            selectBody.accept(new SelectVisitorAdapter() {
                @Override
                public void visit(PlainSelect plainSelect) {
                    // 处理表名情况
                    FromItem fromItem = plainSelect.getFromItem();
                    if (fromItem instanceof Table) {
                        Table table = (Table) fromItem;
                        String tableName = table.getName(); // 获取表名
                        // 其他操作...
                        fromItems.add("'"+fromItem.toString()+"'");

                    }

                    // 处理多表连接情况
                    boolean joinFlag  = false;

                    List<Join> joins = plainSelect.getJoins();
                    if(joins!=null){
                        System.out.println("hhhhh");
                        Map<String, Object> joinClause = new HashMap<>();
                        String joinType = "";
                        for (Join join : joins) {
                            System.out.println(join.getRightItem());
                            System.out.println(join.toString());
                            if(join.isNatural()){
                                joinType = "'natural join'";
                                //joinClause.put("'natural join'","'"+join.getRightItem()+"'");
                            }else if(join.isInner()){
                                joinType = "'inner join'";
                                //joinClause.put("'inner join'","'"+join.getRightItem()+"'");
                            }else if(join.isFull()){
                                joinType = "'full outer join'";
                                //joinClause.put("'full outer join'","'"+join.getRightItem()+"'");
                            }else if(join.isCross()){
                                joinType = "'cross join'";
                                //joinClause.put("'cross join'","'"+join.getRightItem()+"'");
                            }else if(join.isLeft()){
                                joinType = "'left join'";
                                //joinClause.put("'left join'","'"+join.getRightItem()+"'");
                            }else if(join.isCross()){
                                joinType = "'right join'";
                                //joinClause.put("'right join'","'"+join.getRightItem()+"'");
                            }else{
                                joinFlag = true;
                                fromItems.add("'"+join.toString()+"'");
                            }
                            if(!joinFlag){
                                Collection<Expression> onExpressions = join.getOnExpressions();
                                if(onExpressions!=null){
                                    for (Expression expression : onExpressions){
                                        Map<String, Object> onClause = parseExpression(expression);
                                        if(onClause!=null){
                                            joinClause.put(joinType, "'"+join.getRightItem()+"'");
                                            joinClause.put("'c_on'", "'"+onClause.toString()+"'");

                                        }else{
                                            joinClause.put(joinType, "'"+join.getRightItem()+"'");
                                            joinClause.put("'c_on'","'"+expression.toString()+"'");

                                        }

                                    }
                                }

                                fromItems.add(joinClause.toString());
                            }
                            joinFlag = false;
                        }
                    }

                    // 处理子查询情况
                    if (fromItem instanceof SubSelect) {
                        SubSelect subSelect = (SubSelect) fromItem;
                        SelectBody subSelectBody = subSelect.getSelectBody();
                        // 处理子查询的 SelectBody
                        // 可以进一步解析子查询的选择项、条件等
                    }
                    //processSubSelect(plainSelect.getFromItem());
                }
            });


            result.put("'from'",fromItems);
            Expression where = ((PlainSelect) selectBody).getWhere();
            if(where!=null){
                Map<String, Object> whereClause = parseExpression(where);
                result.put("'where'", whereClause);
            }

            Limit limit = ((PlainSelect) selectBody).getLimit();
            if(limit!=null){
                result.put("'limit'",limit.getRowCount());
            }

            String json = result.toString();
            return json;
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Map<String, Object> parseExpression(Expression expression) {
        Map<String, Object> result = new HashMap<>();

        if (expression instanceof Parenthesis) {
            System.out.println("1");
            System.out.println(expression.toString());
            Parenthesis parenthesis = (Parenthesis) expression;
            Expression innerExpression = parenthesis.getExpression();
            result = parseExpression(innerExpression);
            return result;
        } else if (expression instanceof BinaryExpression) {
            System.out.println("2");
            System.out.println(expression.toString());
            BinaryExpression binaryExpression = (BinaryExpression) expression;
            String operator = "'"+binaryExpression.getStringExpression()+"'";

            Expression leftExpression = binaryExpression.getLeftExpression();
            Expression rightExpression = binaryExpression.getRightExpression();

            List<Object> operation = new ArrayList<>();
            Map<String, Object> leftOperand = parseExpression(leftExpression);
            if(leftOperand == null){
                if(leftExpression.toString().substring(0,1).equals("'") && leftExpression.toString().substring(leftExpression.toString().length()-1,leftExpression.toString().length()).equals("'")){
                    operation.add(leftExpression.toString());
                }else{
                    operation.add("'"+leftExpression.toString()+"'");
                }
            }else{
                operation.add(leftOperand.toString());
            }
            Map<String, Object> rightOperand = parseExpression(rightExpression);
            if(rightOperand == null){
                if(rightExpression.toString().substring(0,1).equals("'") && rightExpression.toString().substring(rightExpression.toString().length()-1,rightExpression.toString().length()).equals("'")){
                    String str = rightExpression.toString().replaceAll("'","^");
                    operation.add("'"+str+"'");
                }else{
                    operation.add("'"+rightExpression.toString()+"'");
                }
            }else{
                operation.add(rightOperand.toString());
            }
            result.put(operator, operation);
            return result;
        }else if (expression instanceof NotExpression) {
            NotExpression notExpression = (NotExpression) expression;
            String operator = "'not'";

            Expression expression1 = notExpression.getExpression();

            List<Object> operation = new ArrayList<>();
            Map<String, Object> leftOperand = parseExpression(expression1);
            if(leftOperand == null){
                if(expression1.toString().substring(0,1).equals("'") && expression1.toString().substring(expression1.toString().length()-1,expression1.toString().length()).equals("'")){
                    operation.add(expression1.toString());
                }else{
                    operation.add("'"+expression1.toString()+"'");
                }
            }else{
                operation.add(leftOperand);
            }

            result.put(operator, operation);
            return result;
        }else if (expression instanceof Function) {
            Function function = (Function) expression;
            String operator = "'"+function.getName()+"'";
            System.out.println("hhh"+operator);

            ExpressionList expressionList = function.getParameters();

            List<Object> operation = new ArrayList<>();
            for(int i = 0;i<expressionList.getExpressions().size();i++){
                Expression expression1 = expressionList.getExpressions().get(i);
                Map<String, Object> leftOperand = parseExpression(expression1);
                if(leftOperand == null){
                    if(expression1.toString().substring(0,1).equals("'") && expression1.toString().substring(expression1.toString().length()-1,expression1.toString().length()).equals("'")){
                        operation.add(expression1.toString());
                    }else{
                        operation.add("'"+expression1.toString()+"'");
                    }

                }else{
                    operation.add(leftOperand);
                }
            }


            result.put(operator, operation);
            return result;
        }else if (expression instanceof InExpression) {
            InExpression inExpression = (InExpression) expression;
            String operator = "'in'";

            Expression expression1 = inExpression.getLeftExpression();
            Expression expression2 = inExpression.getRightExpression();
            ItemsList list = inExpression.getRightItemsList();
            List<Object> operation = new ArrayList<>();
            Map<String, Object> leftOperand = parseExpression(expression1);
            if(leftOperand == null){
                if(expression1.toString().substring(0,1).equals("'") && expression1.toString().substring(expression1.toString().length()-1,expression1.toString().length()).equals("'")){
                    operation.add(expression1.toString());
                }else{
                    operation.add("'"+expression1.toString()+"'");
                }
            }else{
                operation.add(leftOperand);
            }

            if(list!=null){
                operation.add("'"+list.toString()+"'");
                result.put(operator,operation);
            }else{
                Map<String, Object> rightOperand = parseExpression(expression2);
                if(rightOperand == null){
                    if(expression2.toString().substring(0,1).equals("'") && expression2.toString().substring(expression2.toString().length()-1,expression2.toString().length()).equals("'")){
                        operation.add(expression2.toString());
                    }else{
                        operation.add("'"+expression2.toString()+"'");
                    }
                }else{
                    operation.add(rightOperand);
                }
                result.put(operator, operation);
            }
            return result;
        }else if (expression instanceof IsNullExpression) {
            IsNullExpression isNullExpression = (IsNullExpression) expression;
            String operator = "'isNull'";

            Expression expression1 = isNullExpression.getLeftExpression();

            List<Object> operation = new ArrayList<>();
            Map<String, Object> leftOperand = parseExpression(expression1);
            if(leftOperand == null){
                if(expression1.toString().substring(0,1).equals("'") && expression1.toString().substring(expression1.toString().length()-1,expression1.toString().length()).equals("'")){
                    operation.add(expression1.toString());
                }else{
                    operation.add("'"+expression1.toString()+"'");
                }
            }else{
                operation.add(leftOperand);
            }

            result.put(operator, operation);
            return result;
        }
        else if (expression instanceof IsBooleanExpression) {
            IsBooleanExpression isBooleanExpression = (IsBooleanExpression) expression;
            String operator = "";
            if(isBooleanExpression.isTrue()){
                operator = "'isTrue'";
            }else if(isBooleanExpression.isNot()){
                operator = "'isFalse'";
            }

            Expression expression1 = isBooleanExpression.getLeftExpression();

            List<Object> operation = new ArrayList<>();
            Map<String, Object> leftOperand = parseExpression(expression1);
            if(leftOperand == null){
                if(expression1.toString().substring(0,1).equals("'") && expression1.toString().substring(expression1.toString().length()-1,expression1.toString().length()).equals("'")){
                    operation.add(expression1.toString());
                }else{
                    operation.add("'"+expression1.toString()+"'");
                }
            }else{
                operation.add(leftOperand);
            }

            result.put(operator, operation);
            return result;
        }else if (expression instanceof ExistsExpression) {
            ExistsExpression existsExpression = (ExistsExpression) expression;
            String operator = "'exists'";

            Expression expression1 = existsExpression.getRightExpression();

            List<Object> operation = new ArrayList<>();
            Map<String, Object> leftOperand = parseExpression(expression1);
            if(leftOperand == null){
                if(expression1.toString().substring(0,1).equals("'") && expression1.toString().substring(expression1.toString().length()-1,expression1.toString().length()).equals("'")){
                    operation.add(expression1.toString());
                }else{
                    operation.add("'"+expression1.toString()+"'");
                }
            }else{
                operation.add(leftOperand);
            }

            result.put(operator, operation);
            return result;
        }else if (expression instanceof SubSelect) {
            String operator = "'query'";

            String subquery = convertSqlToJson(expression.toString());

            result.put(operator, subquery);
            return result;
        } else{
            return null;
        }
    }

    private static abstract class FromItemVisitorAdapter implements FromItemVisitor {
        @Override
        public void visit(Table table) {
        }

        @Override
        public void visit(SubSelect subSelect) {
        }

        @Override
        public void visit(SubJoin subjoin) {
        }

        @Override
        public void visit(LateralSubSelect lateralSubSelect) {
        }

        @Override
        public void visit(ValuesList valuesList) {
        }

        @Override
        public void visit(TableFunction tableFunction) {
        }
    }
}
