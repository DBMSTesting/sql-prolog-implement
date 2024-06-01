package sqlancer.mariadb.ast;

import sqlancer.Randomly;
import sqlancer.common.ast.newast.Node;

import sqlancer.mariadb.oracle.MariaDBNoRECOracle;


import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MariaDBStringVisitor extends MariaDBVisitor {

    private final StringBuilder sb = new StringBuilder();

    public void visit(Node<MariaDBExpression> expr) {
        if (expr instanceof MariaDBConstant) {
            visit((MariaDBConstant) expr);
        } else if (expr instanceof MariaDBJoin) {
            visit((MariaDBJoin) expr);
        } else {
            throw new AssertionError(expr.getClass());
        }
    }
    @Override
    public void visit(MariaDBConstant c) {
        sb.append(c.toString());
    }

    public String getString() {
        return sb.toString();
    }

    @Override
    public void visit(MariaDBJoin join) {
        visit(join.getLeftTable());
        sb.append(" ");
        sb.append(join.getJoinType());
        sb.append(" ");
        if (join.getOuterType() != null) {
            sb.append(join.getOuterType());
        }
        sb.append(" JOIN ");
        visit(join.getRightTable());
        if (join.getOnCondition() != null) {
            sb.append(" ON ");
            visit(join.getOnCondition());
        }

        sb.append(" ");
        visit(join.getLeftTable());
        sb.append(" ");
        switch (join.getJoinType()) {
            case INNER:
                if (Randomly.getBoolean()) {
                    System.out.println("inner");
                    sb.append("INNER ");
                } else {
                    System.out.println("cross");
                    sb.append("CROSS ");
                }
                sb.append("JOIN ");
                break;
            case LEFT:
                System.out.println("left");
                sb.append("LEFT ");
                if (Randomly.getBoolean()) {
                    sb.append(" OUTER ");
                }
                sb.append("JOIN ");
                break;
            case RIGHT:
                System.out.println("right");
                sb.append("RIGHT ");
                if (Randomly.getBoolean()) {
                    sb.append(" OUTER ");
                }
                sb.append("JOIN ");
                break;
            case NATURAL:
                System.out.println("natural");
                sb.append("NATURAL ");
                switch (join.getOuterType()) {
                    case FULL:
                        sb.append("FULL ");
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
        if (join.getJoinType() != MariaDBJoin.JoinType.NATURAL) {
            sb.append("ON ");
            visit(join.getOnCondition());
        }
    }
    @Override
    public void visit(MariaDBPostfixUnaryOperation op) {
        sb.append("(");
        visit(op.getRandomWhereCondition());
        sb.append(" ");
        sb.append(op.getOperator().getTextRepresentation());
        sb.append(")");
    }

    @Override
    public void visit(MariaDBColumnName c) {
        sb.append(c.getColumn().getName());
    }

    @Override
    public void visit(MariaDBSelectStatement s) {
        sb.append("SELECT ");
        if (s.getColumns() == null) {
            sb.append("*");
        } else {
            Random random = new Random();
            switch (random.nextInt(4)){
                case 0 : case 1: case 2:
                    for (int i = 0; i < s.getColumns().size(); i++) {
                        if (i != 0) {
                            sb.append(", ");
                        }
                        visit(s.getColumns().get(i));
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
        int i = 0;
//        for (MariaDBExpression column : s.getColumns()) {
//            if (i++ != 0) {
//                sb.append(", ");
//            }
//            visit(column);
//        }
        sb.append(" FROM ");
        sb.append(s.getTables().stream().map(t -> t.getName()).collect(Collectors.joining(", ")));
        if (!s.getTables().isEmpty() && !s.getJoinTables().isEmpty()) {
            sb.append(", ");
        }
        for (int j = 0; j < s.getJoinTables().size(); j++) {
            if (j != 0) {
                sb.append(", ");
            }
            sb.append(s.getJoinTables().get(j).getName());
            //visit((Node<MariaDBExpression>) s.getJoinTables().get(j));
        }
        if (s.getWhereCondition() != null) {
            sb.append(" WHERE ");
            visit(s.getWhereCondition());
        }
        if (s.getGroupBys().size() != 0) {
            sb.append(" GROUP BY");
            for (i = 0; i < s.getGroupBys().size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                visit(s.getGroupBys().get(i));
            }
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
    public void visit(MariaDBText t) {
        if (t.isPrefix()) {
            sb.append(t.getText());
            visit(t.getExpr());
        } else {
            visit(t.getExpr());
            sb.append(t.getText());
        }
    }

    @Override
    public void visit(MariaDBAggregate aggr) {
        sb.append(aggr.getAggr());
        sb.append("(");
        visit(aggr.getExpr());
        sb.append(")");
    }

    @Override
    public void visit(MariaDBBinaryOperator comp) {
        if (Randomly.getBoolean()) {
            sb.append("(");
            visit(comp.getLeft());
            sb.append(" ");
            sb.append(comp.getOp().getTextRepresentation());
            sb.append(" ");
            visit(comp.getRight());
            sb.append(")");
        }else{
            sb.append("(");
            visit(comp.getLeft());
            sb.append(" ");
            sb.append(comp.getOp().getTextRepresentation());
            sb.append(" ");
            sb.append(" select * from("+ MariaDBNoRECOracle.subquery+" limit 1 ) tempTable ");
            sb.append(")");
        }



    }

    @Override
    public void visit(MariaDBUnaryPrefixOperation op) {
        sb.append("(");
        sb.append(op.getOp().textRepresentation);
        sb.append(" ");
        visit(op.getExpr());
        sb.append(")");
    }

    @Override
    public void visit(MariaDBFunction func) {
        sb.append(func.getFunc().getFunctionName());
        sb.append("(");
        for (int i = 0; i < func.getArgs().size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            Random randomTypes = new Random();
            switch (randomTypes.nextInt(6)){
                case 0: case 1: case 2:
                    visit(func.getArgs().get(i));
                case 3:
                    sb.append("NULL");
                case 4:
                    sb.append("TRUE");
                case 5:
                    sb.append("FALSE");
            }
            //visit(func.getArgs().get(i));
        }
        sb.append(")");

    }

    @Override
    public void visit(MariaDBInOperation op) {
        sb.append("(");
        visit(op.getExpr());
        if (op.isNegated()) {
            sb.append(" NOT");
        }
        sb.append(" IN (");
        visitList(op.getList());
        sb.append("))");
    }

    private void visitList(List<MariaDBExpression> list) {
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            visit(list.get(i));
        }
    }

}
