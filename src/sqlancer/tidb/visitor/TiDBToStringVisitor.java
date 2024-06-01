package sqlancer.tidb.visitor;

import sqlancer.Randomly;
import sqlancer.common.visitor.ToStringVisitor;
import sqlancer.tidb.ast.*;
import sqlancer.tidb.ast.TiDBJoin.JoinType;

import java.util.Random;

public class TiDBToStringVisitor extends ToStringVisitor<TiDBExpression> implements TiDBVisitor {


    @Override
    public void visitSpecific(TiDBExpression expr) {
        TiDBVisitor.super.visit(expr);
    }

    @Override
    public void visit(TiDBConstant c) {
        sb.append(c.toString());
    }

    public String getString() {
        return sb.toString();
    }
    public String getSelectString() {
        return sb.toString()+"|||||"+sbwhere;
    }

    @Override
    public void visit(TiDBColumnReference c) {
        if (c.getColumn().getTable() == null) {
            sb.append(c.getColumn().getName());
        } else {
            sb.append(c.getColumn().getFullQualifiedName());
        }
    }

    @Override
    public void visit(TiDBTableReference expr) {
        sb.append(expr.getTable().getName());
    }

    @Override
    public void visit(TiDBSelect select) {
        sb.append("SELECT ");
//        if (select.getHint() != null) {
//            sb.append("/*+ ");
//            visit(select.getHint());
//            sb.append("*/");
//        }
//        visit(select.getFetchColumns());
        if (select.getFetchColumns() == null) {
            sb.append("*");
        } else {
            Random random = new Random();
            switch (random.nextInt(4)){
                case 0 : case 1: case 2:
                    for (int i = 0; i < select.getFetchColumns().size(); i++) {
                        if (i != 0) {
                            sb.append(", ");
                        }
                        visit(select.getFetchColumns().get(i));
                        // MySQL does not allow duplicate column names
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
        sb.append(" FROM ");
        visit(select.getFromList());
        if (!select.getFromList().isEmpty() && !select.getJoinList().isEmpty()) {
            sb.append(", ");
        }
        if (!select.getJoinList().isEmpty()) {
            visit(select.getJoinList());
        }
        if (select.getWhereClause() != null) {
            sb.append(" WHERE ");
            int length = sb.length();
            visit(select.getWhereClause());
            sbwhere = sb.substring(length,sb.length());
        }
        if (!select.getGroupByExpressions().isEmpty()) {
            sb.append(" GROUP BY ");
            visit(select.getGroupByExpressions());
        }
        if (select.getHavingClause() != null) {
            sb.append(" HAVING ");
            visit(select.getHavingClause());
        }
        if (!select.getOrderByExpressions().isEmpty()) {
            sb.append(" ORDER BY ");
            visit(select.getOrderByExpressions());
        }
    }

    public void getDataType(){
        Random randomTypes = new Random();
        switch (randomTypes.nextInt(6)){
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
//            case 7:
//                sb.append(Randomly.getText());
//                break;
//            case 8:
//                sb.append(Randomly.getDate());
//                break;
            case 5:
                sb.append(Randomly.getDoublePrecision());
                break;
//
        }
    }

    @Override
    public void visit(TiDBFunctionCall call) {
        if(call.getFunction().toString().equals("MOD")){
            sb.append(call.getFunction());
            sb.append("(");
            for(int i = 0;i<call.getArgs().size();i++){
                if(i==0){
                    getDataType();
                    sb.append(", ");
                }else{
                    getDataType();
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

    @Override
    public void visit(TiDBJoin join) {
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
        case STRAIGHT:
            sb.append("STRAIGHT_JOIN ");
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
        if (join.getJoinType() != JoinType.NATURAL) {
            onFlag = true;
            sb.append("ON ");
            visit(join.getOnCondition());
            onFlag = false;
        }
    }

    @Override
    public void visit(TiDBText text) {
        sb.append(text.getText());
    }

    @Override
    public void visit(TiDBAggregate aggr) {
        sb.append(aggr.getFunction());
        sb.append("(");
        visit(aggr.getArgs());
        sb.append(")");
    }

    @Override
    public void visit(TiDBCastOperation cast) {
        sb.append("CAST(");
        visit(cast.getExpr());
        sb.append(" AS ");
        sb.append(cast.getType());
        sb.append(")");
    }

    @Override
    public void visit(TiDBBinaryComparisonOperation op) {
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

    @Override
    public void visit(TiDBCase op) {
        sb.append("(CASE ");
        visit(op.getSwitchCondition());
        for (int i = 0; i < op.getConditions().size(); i++) {
            sb.append(" WHEN ");
            visit(op.getConditions().get(i));
            sb.append(" THEN ");
            visit(op.getExpressions().get(i));
        }
        if (op.getElseExpr() != null) {
            sb.append(" ELSE ");
            visit(op.getElseExpr());
        }
        sb.append(" END )");
    }
}
