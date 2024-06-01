package sqlancer.mysql.oracle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sqlancer.common.gen.ExpressionGenerator;
import sqlancer.common.oracle.TernaryLogicPartitioningOracleBase;
import sqlancer.common.oracle.TestOracle;
import sqlancer.mysql.MySQLErrors;
import sqlancer.mysql.MySQLGlobalState;
import sqlancer.mysql.MySQLSchema;
import sqlancer.mysql.MySQLSchema.MySQLTable;
import sqlancer.mysql.MySQLSchema.MySQLTables;
import sqlancer.mysql.ast.*;
import sqlancer.mysql.gen.MySQLExpressionGenerator;


public abstract class MySQLQueryPartitioningBase extends
        TernaryLogicPartitioningOracleBase<MySQLExpression, MySQLGlobalState> implements TestOracle<MySQLGlobalState> {

    MySQLSchema s;
    MySQLTables targetTables;
    MySQLExpressionGenerator gen;
    MySQLSelect select;


    public MySQLQueryPartitioningBase(MySQLGlobalState state) {
        super(state);
        MySQLErrors.addExpressionErrors(errors);
    }

    @Override
    public void check() throws Throwable {
        //System.out.println("a");
        s = state.getSchema();
        //System.out.println("b");
        targetTables = s.getRandomTableNonEmptyTables();
        //System.out.println("c");
        gen = new MySQLExpressionGenerator(state).setColumns(targetTables.getColumns());
        //System.out.println("d");
        initializeTernaryPredicateVariants("");
        //System.out.println("e");
        select = new MySQLSelect();
        //System.out.println("f");
        select.setFetchColumns(generateFetchColumns());
        //System.out.println("g");
        List<MySQLTable> tables = targetTables.getTables();
        //System.out.println("h");
        List<MySQLExpression> tableList = tables.stream().map(t -> new MySQLTableReference(t))
                .collect(Collectors.toList());
        //System.out.println("i");
        // List<MySQLExpression> joins = MySQLJoin.getJoins(tableList, state);
        List<MySQLExpression> joins = MySQLJoin.getJoins(tableList, state);
        //System.out.println("j");
        select.setJoinList(joins);
        //System.out.println("k");
        select.setFromList(tableList);
        //System.out.println("l");
        select.setWhereClause(null);
        //System.out.println("m");
        // select.setJoins(joins);

    }

    List<MySQLExpression> generateFetchColumns() {
        return Arrays.asList(MySQLColumnReference.create(targetTables.getColumns().get(0), null));
    }

    @Override
    protected ExpressionGenerator<MySQLExpression> getGen() {
        return gen;
    }

}
