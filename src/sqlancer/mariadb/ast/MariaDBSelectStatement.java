package sqlancer.mariadb.ast;

import java.util.ArrayList;
import java.util.List;

import sqlancer.mariadb.MariaDBSchema.MariaDBTable;

public class MariaDBSelectStatement extends MariaDBExpression {

    public enum MariaDBSelectType {
        DISTINCT,ALL
    }

    private List<MariaDBExpression> groupBys = new ArrayList<>();
    private List<MariaDBExpression> columns = new ArrayList<>();
    private List<MariaDBTable> tables = new ArrayList<>();
    private List<MariaDBTable> joinTables = new ArrayList<>();
    private MariaDBSelectType selectType = MariaDBSelectType.DISTINCT;
    private MariaDBExpression whereCondition;

    public void setGroupByClause(List<MariaDBExpression> groupBys) {
        this.groupBys = groupBys;
    }

    public void setFetchColumns(List<MariaDBExpression> columns) {
        this.columns = columns;

    }

    public void setFromTables(List<MariaDBTable> tables) {
        this.tables = tables;
    }
    public void setJoinTables(List<MariaDBTable> joinTables) {
        this.joinTables = joinTables;
    }

    public void setSelectType(MariaDBSelectType selectType) {
        this.selectType = selectType;
    }

    public void setWhereClause(MariaDBExpression whereCondition) {
        this.whereCondition = whereCondition;
    }

    public List<MariaDBExpression> getColumns() {
        return columns;
    }

    public List<MariaDBExpression> getGroupBys() {
        return groupBys;
    }

    public MariaDBSelectType getSelectType() {
        return selectType;
    }

    public List<MariaDBTable> getTables() {
        return tables;
    }
    public List<MariaDBTable> getJoinTables() {
        return joinTables;
    }

    public MariaDBExpression getWhereCondition() {
        return whereCondition;
    }

}
