package sqlancer.mysql.oracle;

//import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import PrologOracle.PrologMain;
import sqlancer.ComparatorHelper;
import sqlancer.Main;
import sqlancer.Randomly;

//import sqlancer.common.query.Query;
import sqlancer.mysql.MySQLGlobalState;

import sqlancer.mysql.MySQLProvider;
import sqlancer.mysql.MySQLVisitor;
import sqlancer.senmanticsCoverage.CompositeRuleCoverage;
import sqlancer.senmanticsCoverage.KeywordCoverage;
import sqlancer.senmanticsCoverage.RuleCoverage;
//import sqlancer.mysql.gen.Constrain;


public class MySQLTLPWhereOracle extends MySQLQueryPartitioningBase {

    public MySQLTLPWhereOracle(MySQLGlobalState state) {
        super(state);
    }

    @Override
    public void check() throws Throwable {
        super.check();
        select.setWhereClause(null);
        //String originalQueryString = MySQLVisitor.asString(select);
        //List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(originalQueryString, errors, state);
        String secondQueryString = "";

        //关键字覆盖度是否提升的判断
        while(KeywordCoverage.keywordcov(secondQueryString)){
            if (Randomly.getBoolean()) {
                select.setOrderByExpressions(gen.generateOrderBys(""));
            }
            select.setOrderByExpressions(Collections.emptyList());
            initializeTernaryPredicateVariants("");
            select.setWhereClause(predicate);
//        String firstQueryString = MySQLVisitor.asString(select);
            select.setWhereClause(negatedPredicate);
            secondQueryString = MySQLVisitor.asString(select);
        }

        //规则覆盖度是否提升的判断
        while(RuleCoverage.rulecov(secondQueryString)){
            if (Randomly.getBoolean()) {
                select.setOrderByExpressions(gen.generateOrderBys(""));
            }
            select.setOrderByExpressions(Collections.emptyList());
            initializeTernaryPredicateVariants("");
            select.setWhereClause(predicate);
//        String firstQueryString = MySQLVisitor.asString(select);
            select.setWhereClause(negatedPredicate);
            secondQueryString = MySQLVisitor.asString(select);
        }

        //组合规则覆盖度是否提升的判断
        while(CompositeRuleCoverage.comrulecov(secondQueryString)){
            if (Randomly.getBoolean()) {
                select.setOrderByExpressions(gen.generateOrderBys(""));
            }
            select.setOrderByExpressions(Collections.emptyList());
            initializeTernaryPredicateVariants("");
            select.setWhereClause(predicate);
//        String firstQueryString = MySQLVisitor.asString(select);
            select.setWhereClause(negatedPredicate);
            secondQueryString = MySQLVisitor.asString(select);
        }

        PrologMain.prologmain(secondQueryString,state.getDatabaseName(), MySQLProvider.content,MySQLProvider.bwUnConsistent);


//        select.setWhereClause(isNullPredicate);
//        String thirdQueryString = MySQLVisitor.asString(select);
//        List<String> combinedString = new ArrayList<>();
//        List<String> secondResultSet = ComparatorHelper.getCombinedResultSet(firstQueryString, secondQueryString,
//                thirdQueryString, combinedString, Randomly.getBoolean(), state, errors);
//        ComparatorHelper.assumeResultSetsAreEqual(resultSet, secondResultSet, originalQueryString, combinedString,
//                state);




//        super.check();
//        select.setWhereClause(null);
//
//        if (Randomly.getBoolean()) {
//            select.setOrderByExpressions(gen.generateOrderBys(""));
//        }
//        select.setOrderByExpressions(Collections.emptyList());
//
//
//        String databaseName = state.getDatabaseName();
//        int number = Integer.parseInt(databaseName.substring(databaseName.length()-1,databaseName.length()));
//        if(number>Main.patterns.size()){
//            number = number-Main.patterns.size();
//        }
//        String patternCov = Main.patterns.get(number);
//        String []cov = patternCov.split("-");
//
//
//        String firstPart = "";
//        String firstpattern = "";
//        String ansOrigin = "";
//        String ansTrue = "";
//        String ansFalse = "";
//        String ansNull = "";
//        for(int i = 0;i< cov.length;i++){
//            String currentCov=cov[i];
//            //System.out.println(currentCov);
//
//            firstpattern= firstpattern+"---"+cov[i];
//
//            //从这里开始循环
//            initializeTernaryPredicateVariants(currentCov);
//            select.setWhereClause(predicate);
//            firstPart = MySQLVisitor.selectAsString(select);
//            //维护一个列表，记录所有的组合规则和规则
//            //到这里结束循环
//            //System.out.println("firstPart: "+i+" "+firstPart);
//            String [] visitResult = firstPart.split("\\|\\|\\|\\|\\|");
//            String[] conditionResult = visitResult[2].split("WHERE",2);
//
//            if(!visitResult[2].equals("NO")){
//
//                if(conditionResult.length==2){
//                    if(i==0){
//                        //System.out.println(visitResult[0]);
//                        ansOrigin = "select (" + visitResult[0] +" limit 1) "+conditionResult[0];
//                        ansTrue = "select (" + visitResult[0] +" limit 1) "+conditionResult[0] + " where ("+conditionResult[1];
//                    }else{
//                        ansTrue = ansTrue+" and "+visitResult[1];
//                    }
//                }
//            }else{
//                String[] noConditionResult = visitResult[0].split("WHERE",2);
//                if(i==0){
//                    ansOrigin = noConditionResult[0];
//                    ansTrue = noConditionResult[0] + " where ("+noConditionResult[1];
//                } else{
//                    ansTrue=ansTrue + " and "+visitResult[1];
//                }
//            }
//        }
        //List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(ansOrigin, errors, state);
//        ansTrue = ansTrue+")";
//        ansFalse = ansTrue + " IS FALSE";
//        ansNull = ansTrue +" IS UNKNOWN";
//        System.out.println(ansOrigin);
//        System.out.println(ansTrue);
        //System.out.println(firstpattern);
        //关键字覆盖度
        //传入参数：语句

        //规则覆盖度
        //传入参数：记录的规则，随机从规则map中选加到原语句中，并判断有没有在已覆盖集合中

        //组合规则覆盖度
        //传入参数：记录的组合规则
        //只记录where之前的内容，或者就在原语句基础上加

        //select.setWhereClause(predicate);
//        for(Query<?> createQuery:Main.createTableList){
//            String []items = createQuery.toString().split("\\s+");
//            if(items.length>3){
//                if(items[0].equals("CREATE")&&items[1].equals("TABLE")){
//                    for(Constrain constrain:Main.createConstrainList){
//                        if(constrain.tableName.equals(items[2])){
//                            //根据蜕变关系重构这条create table语句
//                        }
//                    }
//                }
//            }
//
//        }


//        String firstQueryString = MySQLVisitor.asString(select);
//
//
//        select.setWhereClause(negatedPredicate);
//        String secondQueryString = MySQLVisitor.asString(select);
//        select.setWhereClause(isNullPredicate);
//        String thirdQueryString = MySQLVisitor.asString(select);

//        List<String> combinedString = new ArrayList<>();
//
//        List<String> secondResultSet = ComparatorHelper.getCombinedResultSet(ansTrue, ansFalse,
//                ansNull, combinedString, Randomly.getBoolean(), state, errors);
//        ComparatorHelper.assumeResultSetsAreEqual(resultSet, secondResultSet, ansOrigin, combinedString,
//                state);
        Main.totalQuery++;
    }

}
