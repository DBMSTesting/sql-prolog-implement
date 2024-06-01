package sqlancer.sqlite3.oracle.tlp;

//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sqlancer.ComparatorHelper;
import sqlancer.Randomly;
import sqlancer.sqlite3.SQLite3GlobalState;
import sqlancer.sqlite3.SQLite3Visitor;

public class SQLite3TLPWhereOracle extends SQLite3TLPBase {

    private String generatedQueryString;

    public SQLite3TLPWhereOracle(SQLite3GlobalState state) {
        super(state);
    }

    @Override
    public void check() throws Exception {
        super.check();
        select.setWhereClause(null);
        String originalQueryString = SQLite3Visitor.asSelectString(select);
        String []orgin = originalQueryString.split("\\|\\|\\|\\|\\|");
        originalQueryString = orgin[0];
        generatedQueryString = originalQueryString;
        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(originalQueryString, errors, state);

        boolean orderBy = Randomly.getBooleanWithSmallProbability();
        if (orderBy) {
            select.setOrderByExpressions(gen.generateOrderBys());
        }
        select.setWhereClause(predicate);
        String firstQueryString = SQLite3Visitor.asSelectString(select);
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

    @Override
    public String getLastQueryString() {
        return generatedQueryString;
    }

}
