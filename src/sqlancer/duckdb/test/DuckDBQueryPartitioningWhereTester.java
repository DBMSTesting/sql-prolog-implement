package sqlancer.duckdb.test;

//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sqlancer.ComparatorHelper;
import sqlancer.Randomly;
import sqlancer.duckdb.DuckDBErrors;
import sqlancer.duckdb.DuckDBProvider.DuckDBGlobalState;
import sqlancer.duckdb.DuckDBToStringVisitor;

public class DuckDBQueryPartitioningWhereTester extends DuckDBQueryPartitioningBase {

    public DuckDBQueryPartitioningWhereTester(DuckDBGlobalState state) {
        super(state);
        DuckDBErrors.addGroupByErrors(errors);
    }

    @Override
    public void check() throws Exception {
//        super.check();
//        select.setWhereClause(null);
//        String originalQueryString = DuckDBToStringVisitor.asString(select);
//
//        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(originalQueryString, errors, state);
//
//        boolean orderBy = Randomly.getBooleanWithRatherLowProbability();
//        if (orderBy) {
//            select.setOrderByExpressions(gen.generateOrderBys(""));
//        }
//        select.setWhereClause(predicate);
//        String firstQueryString = DuckDBToStringVisitor.asString(select);
//        select.setWhereClause(negatedPredicate);
//        String secondQueryString = DuckDBToStringVisitor.asString(select);
//        select.setWhereClause(isNullPredicate);
//        String thirdQueryString = DuckDBToStringVisitor.asString(select);
//        List<String> combinedString = new ArrayList<>();
//        List<String> secondResultSet = ComparatorHelper.getCombinedResultSet(firstQueryString, secondQueryString,
//                thirdQueryString, combinedString, !orderBy, state, errors);
//        ComparatorHelper.assumeResultSetsAreEqual(resultSet, secondResultSet, originalQueryString, combinedString,
//                state, DuckDBQueryPartitioningBase::canonicalizeResultValue);

        super.check();
        select.setWhereClause(null);
        String originalQueryString = DuckDBToStringVisitor.asSelectString(select);
        String []orgin = originalQueryString.split("\\|\\|\\|\\|\\|");
        originalQueryString = orgin[0];
        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(originalQueryString, errors, state);

        boolean orderBy = Randomly.getBooleanWithSmallProbability();
        if (orderBy) {
            select.setOrderByExpressions(gen.generateOrderBys(""));
        }
        select.setWhereClause(predicate);
        String firstQueryString = DuckDBToStringVisitor.asSelectString(select);
        String [] array = firstQueryString.split("\\|\\|\\|\\|\\|");
        String ansTrue = "";
        String ansFalse = "";
        String ansNull = "";
        if(!array[1].equals("")){
            ansTrue = originalQueryString + " where "+array[1];
            ansFalse = originalQueryString+ " where not( "+array[1] +")";
            ansNull = originalQueryString+ " where ( "+array[1] +") is unknown";
        }
//        select.setWhereClause(negatedPredicate);
//        String secondQueryString = SQLite3Visitor.asString(select);
//        select.setWhereClause(isNullPredicate);
//        String thirdQueryString = SQLite3Visitor.asString(select);
//        List<String> combinedString = new ArrayList<>();
//        List<String> secondResultSet = ComparatorHelper.getCombinedResultSet(firstQueryString, secondQueryString,
//                thirdQueryString, combinedString, !orderBy, state, errors);
//        ComparatorHelper.assumeResultSetsAreEqual(resultSet, secondResultSet, originalQueryString, combinedString,
//                state);
        List<String> combinedString = new ArrayList<>();
        List<String> secondResultSet = ComparatorHelper.getCombinedResultSet(ansTrue, ansFalse,
                ansNull, combinedString, !orderBy, state, errors);
        ComparatorHelper.assumeResultSetsAreEqual(resultSet, secondResultSet, originalQueryString, combinedString,
                state);
    }

}
