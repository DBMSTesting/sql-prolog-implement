package sqlancer.duckdb;

import sqlancer.Randomly;
import sqlancer.common.ast.newast.NewToStringVisitor;
import sqlancer.common.ast.newast.Node;
import sqlancer.duckdb.ast.DuckDBConstant;
import sqlancer.duckdb.ast.DuckDBExpression;
import sqlancer.duckdb.ast.DuckDBJoin;
import sqlancer.duckdb.ast.DuckDBSelect;


public class DuckDBToStringVisitor extends NewToStringVisitor<DuckDBExpression> {

    @Override
    public void visitSpecific(Node<DuckDBExpression> expr) {
        if (expr instanceof DuckDBConstant) {
            visit((DuckDBConstant) expr);
        } else if (expr instanceof DuckDBSelect) {
            visit((DuckDBSelect) expr);
        } else if (expr instanceof DuckDBJoin) {
            visit((DuckDBJoin) expr);
        } else {
            System.out.println(expr.toString());
            throw new AssertionError(expr.getClass());
        }
    }

    private void visit(DuckDBJoin join) {
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
            case POSITIONAL:
                System.out.println("positional");
                sb.append("POSITIONAL ");
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
        if (join.getJoinType() != DuckDBJoin.JoinType.NATURAL || join.getJoinType() != DuckDBJoin.JoinType.POSITIONAL) {
            sb.append("ON ");
            visit(join.getOnCondition());
        }
    }

    private void visit(DuckDBConstant constant) {
        sb.append(constant.toString());
    }

    private void visit(DuckDBSelect select) {
        sb.append("SELECT ");
        if (select.isDistinct()) {
            sb.append("DISTINCT ");
//            Random random = new Random();
//            switch (random.nextInt(3)){
//                case 0:
//                    sb.append("MIN(COLUMNS(*)), ");
//                case 1:
//                    sb.append("MAX(COLUMNS(*)), ");
//                case 2:
//                    sb.append("SUM(COLUMNS(*)), ");
//                case 3:
//                    sb.append("COUNT(COLUMNS(*)), ");
//            }

        }
        visit(select.getFetchColumns());
        sb.append(" FROM ");
        for (int i = 0; i < select.getFromList().size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            visit(select.getFromList().get(i));
        }
        //visit(select.getFromList());
        if (!select.getFromList().isEmpty() && !select.getJoinList().isEmpty()) {
            sb.append(", ");
        }
        for (int i = 0; i < select.getJoinList().size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            visit(select.getJoinList().get(i));
        }
//        if (!select.getJoinList().isEmpty()) {
//            visit(select.getJoinList());
//        }
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
        if (select.getLimitClause() != null) {
            sb.append(" LIMIT ");
            visit(select.getLimitClause());
        }
        if (select.getOffsetClause() != null) {
            sb.append(" OFFSET ");
            visit(select.getOffsetClause());
        }
    }

    public static String asString(Node<DuckDBExpression> expr) {
        DuckDBToStringVisitor visitor = new DuckDBToStringVisitor();
        visitor.visit(expr);
        return visitor.get();
    }

    public static String asSelectString(Node<DuckDBExpression> expr) {
        DuckDBToStringVisitor visitor = new DuckDBToStringVisitor();
        visitor.visit(expr);
        return visitor.getSelect();
    }


}
