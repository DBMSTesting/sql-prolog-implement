package sqlancer.mysql;

import sqlancer.Randomly;
import sqlancer.common.visitor.ToStringVisitor;
import sqlancer.mysql.ast.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class CheckToStringVisitor extends ToStringVisitor<MySQLExpression> implements MySQLVisitor{
    int ref;
    boolean selectSubQueryFlag = false;

    @Override
    public void visitSpecific(MySQLExpression expr) {
        MySQLVisitor.super.visit(expr);
    }

    @Override
    public void visit(MySQLSelect s) {
        sb.append("SELECT ");
        switch (s.getFromOptions()) {
            case DISTINCT:
                sb.append("DISTINCT ");
                break;
//        case ALL:
//            sb.append(Randomly.fromOptions("ALL ", ""));
//            break;
//        case DISTINCTROW:
//            sb.append("DISTINCTROW ");
//            break;
            default:
                throw new AssertionError();
        }
        sb.append(s.getModifiers().stream().collect(Collectors.joining(" ")));
        if (s.getModifiers().size() > 0) {
            sb.append(" ");
        }
        if (s.getFetchColumns() == null) {
            sb.append("*");
        } else {
            Random random = new Random();
            switch (random.nextInt(4)){
                case 0 : case 1: case 2:
                    selectSubQueryFlag = false;
                    for (int i = 0; i < s.getFetchColumns().size(); i++) {
                        if (i != 0) {
                            sb.append(", ");
                        }
                        visit(s.getFetchColumns().get(i));
                        // MySQL does not allow duplicate column names
                        sb.append(" AS ");
                        sb.append("ref");
                        sb.append(ref++);
                    }
                    break;

                case 3:
                    getDataType();
                    sb.append(" ");
                    break;
                //select子查询
//                case 4:
//                    selectSubQueryFlag = true;
//                    break;

            }


        }
        int lengthSelect = sb.length();
        sb.append(" FROM ");
        for (int i = 0; i < s.getFromList().size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            visit(s.getFromList().get(i));
        }

        if (!s.getFromList().isEmpty() && !s.getJoinList().isEmpty()) {
            sb.append(", ");
        }
        for (int i = 0; i < s.getJoinList().size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            visit(s.getJoinList().get(i));
        }


//        for (MySQLExpression j : s.getJoinList()) {
//            visit(j);
//        }

        if (s.getWhereClause() != null) {
            MySQLExpression whereClause = s.getWhereClause();
            sb.append(" WHERE ");
            int length = sb.length();

            visit(whereClause);
            sbwhere = sb.substring(length,sb.length());


        }
        if (s.getGroupByExpressions() != null && s.getGroupByExpressions().size() > 0) {
            sb.append(" ");
            sb.append("GROUP BY ");
            List<MySQLExpression> groupBys = s.getGroupByExpressions();
            for (int i = 0; i < groupBys.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                visit(groupBys.get(i));
            }
        }
        if (!s.getOrderByExpressions().isEmpty()) {
            sb.append(" ORDER BY ");
            Random random = new Random();
            switch (random.nextInt(2)) {
                case 0:
                case 1:
                    List<MySQLExpression> orderBys = s.getOrderByExpressions();
                    for (int i = 0; i < orderBys.size(); i++) {
                        if (i != 0) {
                            sb.append(", ");
                        }
                        visit(s.getOrderByExpressions().get(i));
                    }
                    break;
//                case 2:
//                    getDataType();
//                    sb.append(" ");
//                    break;

            }
        }
        if (s.getLimitClause() != null) {
            sb.append(" LIMIT ");
            visit(s.getLimitClause());
        }

        if (s.getOffsetClause() != null) {
            sb.append(" OFFSET ");
            visit(s.getOffsetClause());
        }
        sbSelectSubQuery = sb.substring(lengthSelect,sb.length());
    }

    @Override
    public void visit(MySQLFunctionCall call) {
        if(call.getFunction().toString().equals("MOD")){
            sb.append(call.getFunction());
            sb.append("(");
            for(int i = 0;i<call.getArgs().size();i++){
                if(i==0){
                    //getDataType();
                    BigDecimal negativeNumber = generateNegativeNumberAsString();
                    sb.append(negativeNumber);
                    sb.append(", ");
                }else{
                    //getDataType();
                    double nonZeroConstant = generateNonZeroConstant();
                    sb.append(nonZeroConstant);
                }

            }
            sb.append(")");
        }else{
            funcFlag = true;
            sb.append(call.getFunction());
            sb.append("(");
            visit(call.getArgs());
            sb.append(")");
        }

    }
//    public static void main(String[] args) {
//        // 生成字符串形式的负数
//        BigDecimal negativeNumber = generateNegativeNumberAsString();
//        System.out.println("负数: " + negativeNumber);
//
//        // 生成非零的常数
//        double nonZeroConstant = generateNonZeroConstant();
//        System.out.println("非零常数: " + nonZeroConstant);
//    }

    // 生成字符串形式的负数
    private static BigDecimal generateNegativeNumberAsString() {
        Random random = new Random();
        double negativeValue = -random.nextDouble() * 100; // 生成负数的范围
        return new BigDecimal(Double.toString(negativeValue));
    }

    // 生成非零的常数
    private static double generateNonZeroConstant() {
        Random random = new Random();
        double nonZeroValue = random.nextDouble() + 0.1; // 生成大于0的小数
        return nonZeroValue;
    }

    public void getDataType(){
        Random randomTypes = new Random();
        switch (randomTypes.nextInt(8)){
            case 0: case 2:
                sb.append(Randomly.getIntegerType());
                break;
            case 1:
                sb.append(Randomly.getBigint());
                break;
//            case 2:
//                sb.append(Randomly.getBytea());
//                break;
//            case 3:
//                sb.append(Randomly.getCharacterVarying(9));
//                break;
//            case 4:
//                sb.append(Randomly.geCharacter(9));
//                break;
            case 3:
                sb.append(Randomly.getSmallint());
                break;
            case 4:
                sb.append(Randomly.getReal());
                break;
            case 7:
                sb.append(Randomly.scientificNotation2String());
                break;
//            case 8:
//                sb.append(Randomly.getDate());
//                break;
            case 5:
                sb.append(Randomly.getDoublePrecision());
                break;
            case 6:
                sb.append(Randomly.getNumberString());
                break;
//
        }
    }

    @Override
    public void visit(MySQLConstant constant) {
        sb.append(constant.getTextRepresentation());
    }

    @Override
    public String get() {
        return sb.toString();
    }

    public String getSelect(){
        String sh ="";
        if(selectSubQueryFlag){
            sh = sb.toString()+"|||||"+sbwhere+"|||||"+sbSelectSubQuery;
        }else{
            sh = sb.toString()+"|||||"+sbwhere+"|||||"+"NO";
        }

        sbwhere = "";
        sbSelectSubQuery = "";
        selectSubQueryFlag = false;
        return sh;
    }

    @Override
    public void visit(MySQLColumnReference column) {
        sb.append(column.getColumn().getFullQualifiedName());
    }

    @Override
    public void visit(MySQLUnaryPostfixOperation op) {
        sb.append("(");
        visit(op.getExpression());
        sb.append(")");
        sb.append(" IS ");
        if (op.isNegated()) {
            sb.append("NOT ");
        }
        switch (op.getOperator()) {
            case IS_FALSE:
                sb.append("FALSE");
                break;
            case IS_NULL:
                if (Randomly.getBoolean()) {
                    sb.append("UNKNOWN");
                } else {
                    sb.append("NULL");
                }
                break;
            case IS_TRUE:
                sb.append("TRUE");
                break;
            default:
                throw new AssertionError(op);
        }
    }

    @Override
    public void visit(MySQLJoin join) {
        sb.append(" ");
        visit(join.getLeftTable());
        sb.append(" ");
        switch (join.getJoinType()) {
            case INNER:
                if (Randomly.getBoolean()) {

                    sb.append("INNER ");
                } else {

                    sb.append("CROSS ");
                }
                sb.append("JOIN ");
                break;
            case LEFT:

                sb.append("LEFT ");
                if (Randomly.getBoolean()) {
                    sb.append(" OUTER ");
                }
                sb.append("JOIN ");
                break;
            case RIGHT:

                sb.append("RIGHT ");
                if (Randomly.getBoolean()) {
                    sb.append(" OUTER ");
                }
                sb.append("JOIN ");
                break;
            case NATURAL:

                sb.append("NATURAL ");
                switch (join.getNaturalJoinType()) {
                    case INNER:
                        break;
                    case LEFT:
                        sb.append("LEFT ");
                        break;
                    case RIGHT:
                        sb.append("RIGHT ");
                        break;
                    default:
                        throw new AssertionError();
                }
                sb.append("JOIN ");
                break;
            default:
                throw new AssertionError();
        }
        visit(join.getRightTable());
        sb.append(" ");
        if (join.getJoinType() != MySQLJoin.JoinType.NATURAL) {
            sb.append("ON ");
            visit(join.getOnCondition());
        }
    }

    @Override
    public void visit(MySQLComputableFunction f) {
        sb.append(f.getFunction().getName());
        if(f.getFunction().getName().equals("MOD")){
            sb.append("(");
            for (int i = 0; i < f.getArguments().length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                getDataType();
            }
            sb.append(")");
        }else{
            sb.append("(");
            for (int i = 0; i < f.getArguments().length; i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                Random randomTypes = new Random();
                switch (randomTypes.nextInt(6)){
                    case 0: case 1: case 2:
                        visit(f.getArguments()[i]);
                        break;
                    case 3:
                        sb.append("NULL");
                        break;
                    case 4:
                        sb.append("TRUE");
                        break;
                    case 5:
                        sb.append("FALSE");
                        break;
                }

            }
            sb.append(")");
        }

    }

    @Override
    public void visit(MySQLBinaryLogicalOperation op) {

        sb.append("(");
        visit(op.getLeft());
        sb.append(")");
        sb.append(" ");
        sb.append(op.getTextRepresentation());
        sb.append(" ");
        sb.append("(");
        visit(op.getRight());
        sb.append(")");
    }

    @Override
    public void visit(MySQLBinaryComparisonOperation op) {
        if(op.getOp().getTextRepresentation().equals("+") || op.getOp().getTextRepresentation().equals("-") || op.getOp().getTextRepresentation().equals("*") || op.getOp().getTextRepresentation().equals("/")){
            sb.append("(");
            getDataType();
            sb.append(") ");
            sb.append(op.getOp().getTextRepresentation());
            sb.append(" (");
            getDataType();
            sb.append(")");
        }else{
            if(op.getSubquery()!=null){
                sb.append("(");
                visit(op.getLeft());
                sb.append(") ");
                sb.append(op.getOp().getTextRepresentation());
                sb.append(" (");
                sb.append(op.getSubquery());
                sb.append(")");
            }else{
                sb.append("(");
                visit(op.getLeft());
                sb.append(") ");
                sb.append(op.getOp().getTextRepresentation());
                sb.append(" (");
                visit(op.getRight());
                sb.append(")");
            }
        }

    }
    @Override
    public void visit(MySQLBinaryComparisonCheckOperation op) {
        if(op.getOp().getTextRepresentation().equals("+") || op.getOp().getTextRepresentation().equals("-") || op.getOp().getTextRepresentation().equals("*") || op.getOp().getTextRepresentation().equals("/")){
            sb.append("(");
            getDataType();
            sb.append(") ");
            sb.append(op.getOp().getTextRepresentation());
            sb.append(" (");
            getDataType();
            sb.append(")");
        }else{
            if(op.getSubquery()!=null){
                sb.append("(");
                visit(op.getLeft());
                sb.append(") ");
                sb.append(op.getOp().getTextRepresentation());
                sb.append(" (");
                sb.append(op.getSubquery());
                sb.append(")");
            }else{
                sb.append("(");
                visit(op.getLeft());
                sb.append(") ");
                sb.append(op.getOp().getTextRepresentation());
                sb.append(" (");
                visit(op.getRight());
                sb.append(")");
            }
        }

    }

    @Override
    public void visit(MySQLCastOperation op) {
        sb.append("CAST(");
        visit(op.getExpr());
        sb.append(" AS ");
        sb.append(op.getType());
        sb.append(")");
    }

    @Override
    public void visit(MySQLInOperation op) {
        if(op.getSubquery()!=null){
            sb.append("(");
            visit(op.getExpr());
            sb.append(")");
            if (!op.isTrue()) {
                sb.append(" NOT");
            }
            sb.append(" IN ");
            sb.append("(");
            sb.append(op.getSubquery());
            sb.append(")");
        }else{
            sb.append("(");
            visit(op.getExpr());
            sb.append(")");
            if (!op.isTrue()) {
                sb.append(" NOT");
            }
            sb.append(" IN ");
            sb.append("(");
            for (int i = 0; i < op.getListElements().size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                visit(op.getListElements().get(i));
            }
            sb.append(")");
        }

    }

    @Override
    public void visit(MySQLBinaryOperation op) {
        sb.append("(");
        visit(op.getLeft());
        sb.append(") ");
        sb.append(op.getOp().getTextRepresentation());
        sb.append(" (");
        visit(op.getRight());
        sb.append(")");
    }

    @Override
    public void visit(MySQLOrderByTerm op) {
        visit(op.getExpr());
        sb.append(" ");
        sb.append(op.getOrder() == MySQLOrderByTerm.MySQLOrder.ASC ? "ASC" : "DESC");
    }

    @Override
    public void visit(MySQLExists op) {
        sb.append(" EXISTS (");
        visit(op.getExpr());
        sb.append(")");
    }

    @Override
    public void visit(MySQLStringExpression op) {
        sb.append(op.getStr());
    }

    @Override
    public void visit(MySQLBetweenOperation op) {
        sb.append("(");
        visit(op.getExpr());
        sb.append(") BETWEEN (");
        visit(op.getLeft());
        sb.append(") AND (");
        visit(op.getRight());
        sb.append(")");
    }

    @Override
    public void visit(MySQLTableReference ref) {
        sb.append(ref.getTable().getName());
    }

    @Override
    public void visit(MySQLCollate collate) {
        sb.append("(");
        visit(collate.getExpression());
        sb.append(" ");
        sb.append(collate.getOperatorRepresentation());
        sb.append(")");
    }
}
