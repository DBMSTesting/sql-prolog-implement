package sqlancer.mariadb.ast;

import sqlancer.Randomly;

import sqlancer.common.ast.newast.Node;
import sqlancer.common.ast.newast.TableReferenceNode;
import sqlancer.mariadb.MariaDBProvider;
import sqlancer.mariadb.MariaDBSchema;
import sqlancer.mariadb.gen.MariaDBExpressionGenerator;


import java.util.ArrayList;
import java.util.List;

public class MariaDBJoin implements Node<MariaDBExpression> {
    private final TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> leftTable;
    private final TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> rightTable;
    private final MariaDBJoin.JoinType joinType;
    private final MariaDBExpression onCondition;
    private MariaDBJoin.OuterType outerType;

    public enum JoinType {
        INNER, NATURAL, LEFT, RIGHT;

        public static MariaDBJoin.JoinType getRandom() {
            return Randomly.fromOptions(values());
        }
    }

    public enum OuterType {
        FULL, LEFT, RIGHT;

        public static MariaDBJoin.OuterType getRandom() {
            return Randomly.fromOptions(values());
        }
    }

    public MariaDBJoin(TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> leftTable,
                      TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> rightTable, MariaDBJoin.JoinType joinType,
                      MariaDBExpression whereCondition) {
        this.leftTable = leftTable;
        this.rightTable = rightTable;
        this.joinType = joinType;
        this.onCondition = whereCondition;
    }

    public TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> getLeftTable() {
        return leftTable;
    }

    public TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> getRightTable() {
        return rightTable;
    }

    public MariaDBJoin.JoinType getJoinType() {
        return joinType;
    }

    public MariaDBExpression getOnCondition() {
        return onCondition;
    }

    private void setOuterType(MariaDBJoin.OuterType outerType) {
        this.outerType = outerType;
    }

    public MariaDBJoin.OuterType getOuterType() {
        return outerType;
    }

    public static List<Node<MariaDBExpression>> getJoins(
            List<TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable>> tableList, MariaDBProvider.MariaDBGlobalState globalState) {
        List<Node<MariaDBExpression>> joinExpressions = new ArrayList<>();
        while (tableList.size() >= 2 && Randomly.getBooleanWithRatherLowProbability()) {
            Randomly r = new Randomly();
            TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> leftTable = tableList.remove(0);
            TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> rightTable = tableList.remove(0);
            List<MariaDBSchema.MariaDBColumn> columns = new ArrayList<>(leftTable.getTable().getColumns());
            columns.addAll(rightTable.getTable().getColumns());
            MariaDBExpressionGenerator joinGen = new MariaDBExpressionGenerator(r).setColumns(columns);
            switch (MariaDBJoin.JoinType.getRandom()) {
                case INNER:
                    joinExpressions.add(MariaDBJoin.createInnerJoin(leftTable, rightTable, joinGen.getRandomExpression()));
                    break;
                case NATURAL:
                    joinExpressions.add(MariaDBJoin.createNaturalJoin(leftTable, rightTable, MariaDBJoin.OuterType.getRandom()));
                    break;
                case LEFT:
                    joinExpressions
                            .add(MariaDBJoin.createLeftOuterJoin(leftTable, rightTable, joinGen.getRandomExpression()));
                    break;
                case RIGHT:
                    joinExpressions
                            .add(MariaDBJoin.createRightOuterJoin(leftTable, rightTable, joinGen.getRandomExpression()));
                    break;
                default:
                    throw new AssertionError();
            }
        }
        return joinExpressions;
    }

    public static MariaDBJoin createRightOuterJoin(TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> left,
                                                  TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> right, MariaDBExpression predicate) {
        return new MariaDBJoin(left, right, MariaDBJoin.JoinType.RIGHT, predicate);
    }

    public static MariaDBJoin createLeftOuterJoin(TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> left,
                                                 TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> right, MariaDBExpression predicate) {
        return new MariaDBJoin(left, right, MariaDBJoin.JoinType.LEFT, predicate);
    }

    public static MariaDBJoin createInnerJoin(TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> left,
                                             TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> right, MariaDBExpression predicate) {
        return new MariaDBJoin(left, right, MariaDBJoin.JoinType.INNER, predicate);
    }

    public static Node<MariaDBExpression> createNaturalJoin(TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> left,
                                                           TableReferenceNode<MariaDBExpression, MariaDBSchema.MariaDBTable> right, MariaDBJoin.OuterType naturalJoinType) {
        MariaDBJoin join = new MariaDBJoin(left, right, MariaDBJoin.JoinType.NATURAL, null);
        join.setOuterType(naturalJoinType);
        return join;
    }


    


}
