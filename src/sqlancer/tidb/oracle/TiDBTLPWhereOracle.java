package sqlancer.tidb.oracle;

//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sqlancer.ComparatorHelper;
import sqlancer.Randomly;
import sqlancer.tidb.TiDBErrors;
import sqlancer.tidb.TiDBProvider.TiDBGlobalState;
import sqlancer.tidb.visitor.TiDBVisitor;

public class TiDBTLPWhereOracle extends TiDBTLPBase {

    private String generatedQueryString;

    public TiDBTLPWhereOracle(TiDBGlobalState state) {
        super(state);
        TiDBErrors.addExpressionErrors(errors);
    }

    @Override
    public void check() throws Exception {
        super.check();
        select.setWhereClause(null);
        String originalQueryString = TiDBVisitor.asSelectString(select);
        String []orgin = originalQueryString.split("\\|\\|\\|\\|\\|");
        originalQueryString = orgin[0];
        generatedQueryString = originalQueryString;
        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(originalQueryString, errors, state);

        boolean orderBy = Randomly.getBooleanWithRatherLowProbability();
        if (orderBy) {
            select.setOrderByExpressions(gen.generateOrderBys(""));
        }
        select.setWhereClause(predicate);
        String firstQueryString = TiDBVisitor.asSelectString(select);

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
//        String secondQueryString = TiDBVisitor.asString(select);
//        select.setWhereClause(isNullPredicate);
//        String thirdQueryString = TiDBVisitor.asString(select);
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
