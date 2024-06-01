package sqlancer.mysql.ast;

import sqlancer.Randomly;
import sqlancer.mysql.MySQLGlobalState;
import sqlancer.mysql.MySQLSchema;
import sqlancer.mysql.gen.MySQLExpressionGenerator;


import java.util.ArrayList;
import java.util.List;

public class MySQLJoin implements MySQLExpression {

//    @Override
//    public MySQLConstant getExpectedValue() {
//        throw new UnsupportedOperationException();
//    }

    private final MySQLExpression leftTable;
    private final MySQLExpression rightTable;
    private final MySQLJoin.JoinType joinType;
    private final MySQLExpression onCondition;
    private MySQLJoin.NaturalJoinType outerType;

    public enum JoinType {
        INNER, NATURAL, RIGHT,LEFT;

        public static MySQLJoin.JoinType getRandom() {
            return Randomly.fromOptions(values());
        }
    }

    public enum NaturalJoinType {
        INNER, LEFT, RIGHT;

        public static MySQLJoin.NaturalJoinType getRandom() {
            return Randomly.fromOptions(values());
        }
    }



    public MySQLJoin(MySQLExpression leftTable, MySQLExpression rightTable, MySQLJoin.JoinType joinType,
                    MySQLExpression whereCondition) {
        this.leftTable = leftTable;
        this.rightTable = rightTable;
        this.joinType = joinType;
        this.onCondition = whereCondition;
    }

    public MySQLExpression getLeftTable() {
        return leftTable;
    }

    public MySQLExpression getRightTable() {
        return rightTable;
    }

    public MySQLJoin.JoinType getJoinType() {
        return joinType;
    }

    public MySQLExpression getOnCondition() {
        return onCondition;
    }

    public static MySQLJoin createNaturalJoin(MySQLExpression left, MySQLExpression right, MySQLJoin.NaturalJoinType type) {
        MySQLJoin MySQLJoin = new MySQLJoin(left, right, JoinType.NATURAL, null);
        MySQLJoin.setNaturalJoinType(type);
        return MySQLJoin;
    }

    public static MySQLJoin createInnerJoin(MySQLExpression left, MySQLExpression right, MySQLExpression onClause) {
        return new MySQLJoin(left, right, MySQLJoin.JoinType.INNER, onClause);
    }

    public static MySQLJoin createLeftOuterJoin(MySQLExpression left, MySQLExpression right, MySQLExpression onClause) {
        return new MySQLJoin(left, right, MySQLJoin.JoinType.LEFT, onClause);
    }

    public static MySQLJoin createRightOuterJoin(MySQLExpression left, MySQLExpression right, MySQLExpression onClause) {
        return new MySQLJoin(left, right, MySQLJoin.JoinType.RIGHT, onClause);
    }


    private void setNaturalJoinType(MySQLJoin.NaturalJoinType outerType) {
        this.outerType = outerType;
    }

    public MySQLJoin.NaturalJoinType getNaturalJoinType() {
        return outerType;
    }

    public static List<MySQLExpression> getJoins(List<MySQLExpression> tableList, MySQLGlobalState globalState) {
        List<MySQLExpression> joinExpressions = new ArrayList<>();
        while (tableList.size() >= 2) {
            MySQLTableReference leftTable = (MySQLTableReference) tableList.remove(0);
            MySQLTableReference rightTable = (MySQLTableReference) tableList.remove(0);
            List<MySQLSchema.MySQLColumn> columns = new ArrayList<>(leftTable.getTable().getColumns());
            columns.addAll(rightTable.getTable().getColumns());
            MySQLExpressionGenerator joinGen = new MySQLExpressionGenerator(globalState).setColumns(columns);
            switch (MySQLJoin.JoinType.getRandom()) {
                case INNER:

                    joinExpressions.add(MySQLJoin.createInnerJoin(leftTable, rightTable, joinGen.generateExpression("")));
                    break;
                case NATURAL:

                    joinExpressions.add(MySQLJoin.createNaturalJoin(leftTable, rightTable, MySQLJoin.NaturalJoinType.getRandom()));
                    break;
                case LEFT:

                    joinExpressions.add(MySQLJoin.createLeftOuterJoin(leftTable, rightTable, joinGen.generateExpression("")));
                    break;
                case RIGHT:

                    joinExpressions.add(MySQLJoin.createRightOuterJoin(leftTable, rightTable, joinGen.generateExpression("")));
                    break;
                default:

                    throw new AssertionError();
            }
        }
        return joinExpressions;
    }

}
