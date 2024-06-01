package sqlancer.mysql.oracle;

import sqlancer.*;
import sqlancer.common.query.Query;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.mysql.MySQLGlobalState;
//import sqlancer.mysql.MySQLOptions;
//import sqlancer.mysql.MySQLToStringVisitor;
import sqlancer.mysql.MySQLVisitor;
import sqlancer.mysql.gen.Constrain;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.Statement;
import java.util.ArrayList;
//import java.util.Collections;
import java.util.List;

public class MySQLConstrainOracle extends MySQLQueryPartitioningBase{
    public MySQLConstrainOracle(MySQLGlobalState state) {
        super(state);
    }
    @Override
    public void check() throws Throwable {
        super.check();
        select.setWhereClause(null);
        //String originalQueryString = DuckDBToStringVisitor.asString(select);



        boolean orderBy = Randomly.getBooleanWithRatherLowProbability();
        if (orderBy) {
            select.setOrderByExpressions(gen.generateOrderBys(""));
        }
//        select.setWhereClause(predicate);
//        String firstQueryString = DuckDBToStringVisitor.asString(select);
        select.setWhereClause(negatedPredicate);
        String secondQueryString = MySQLVisitor.asString(select);
//        select.setWhereClause(isNullPredicate);
//        String thirdQueryString = DuckDBToStringVisitor.asString(select);
        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(secondQueryString, errors, state);
        //List<String> combinedString = new ArrayList<>();
        //List<String> secondResultSet = ComparatorHelper.getCombinedResultSet(firstQueryString, secondQueryString,thirdQueryString, combinedString, !orderBy, state, errors);


//        super.check();
//        select.setWhereClause(null);
//        String originalQueryString = DuckDBToStringVisitor.asSelectString(select);
//        String []orgin = originalQueryString.split("\\|\\|\\|\\|\\|");
//        originalQueryString = orgin[0];
////        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(originalQueryString, errors, state);
//
//        boolean orderBy = Randomly.getBooleanWithSmallProbability();
//        if (orderBy) {
//            select.setOrderByExpressions(gen.generateOrderBys(""));
//        }
//        select.setWhereClause(predicate);
//        String firstQueryString = DuckDBToStringVisitor.asSelectString(select);
//        String [] array = firstQueryString.split("\\|\\|\\|\\|\\|");
//        String ansTrue = "";
//        //String ansFalse = "";
//        //String ansNull = "";
//        if(!array[1].equals("")){
//            ansTrue = originalQueryString + " where "+array[1];
//            //ansFalse = originalQueryString+ " where not( "+array[1] +")";
//            //ansNull = originalQueryString+ " where ( "+array[1] +") is unknown";
//        }
//        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(ansTrue, errors, state);

        for(Query<SQLConnection> createQuery: Main.createTableList){
            String []items = createQuery.toString().split("\\s+");
            if(items.length>3){
                System.out.println(createQuery.getQueryString());
                if(items[0].equals("CREATE")&&items[1].equals("TABLE")){
                    System.out.println(111);
                    List<String> statementList = new ArrayList<>();
                    System.out.println(Main.createConstrainList);
                    for(Constrain constrain:Main.createConstrainList){
                        if(constrain.tableName.equals(items[2])){
                            //根据蜕变关系重构这条create table语句
                            statementList = ConstrainToTrigger(constrain,statementList);
                            break;
                        }
                    }
                    for(String statement:statementList){
                        Query<SQLConnection> query = new SQLQueryAdapter(statement.toString(), errors);
                        System.out.println(query.getQueryString());
                        state.executeStatement(query);
                    }
                }else{
                    System.out.println(createQuery.getQueryString());
                    System.out.println(222);
                    state.executeStatement(createQuery);
                }
            }else{
                System.out.println(createQuery.getQueryString());
                System.out.println(333);
                state.executeStatement(createQuery);
            }

        }
        List<String> secondResultSet = ComparatorHelper.getResultSetFirstColumnAsString(secondQueryString, errors, state);
        ComparatorHelper.assumeResultSetsAreEqual(resultSet, secondResultSet, secondQueryString, secondQueryString,
                state);

//        super.check();
//        System.out.println("jjjjjjjjjjj"+state.getDatabaseName());
//
//        select.setWhereClause(null);
//        //String originalQueryString = MySQLVisitor.asString(select);
//        //System.out.println(originalQueryString);
//
//
//
//
//        //List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(originalQueryString, errors, state);
//
//        if (Randomly.getBoolean()) {
//            System.out.println("jjjjjjjjjjjhhhhh"+state.getDatabaseName());
//            select.setOrderByExpressions(gen.generateOrderBys(""));
//        }
//        select.setOrderByExpressions(Collections.emptyList());
//
//        System.out.println("hhhhhhhhhhhh"+state.getDatabaseName());
//
//
//        String databaseName = state.getDatabaseName();
//        int number = Integer.parseInt(databaseName.substring(databaseName.length()-1,databaseName.length()));
//        //System.out.println(number);
//        if(number> Main.patterns.size()){
//            number = number-Main.patterns.size();
//        }
//        String patternCov = Main.patterns.get(number);
//        //System.out.println(patternCov);
//        String []cov = patternCov.split("-");
//        //System.out.println(cov.length);
//        //System.out.println(cov[0]);
//
//        String firstPart = "";
//        String firstpattern = "";
//        //String ansOrigin = "";
//        String ansTrue = "";
//        //String ansFalse = "";
//        //String ansNull = "";
//
//
//        System.out.println("ansTrue"+state.getDatabaseName());
//        System.out.println(cov.length);
//        for(int i = 0;i< cov.length;i++){
//            System.out.println("1");
//            String currentCov=cov[i];
//            //System.out.println(currentCov);
//
//            System.out.println("2");
//            firstpattern= firstpattern+"---"+cov[i];
//
//            System.out.println("3");
//            initializeTernaryPredicateVariants(currentCov);
//            System.out.println("4");
//            select.setWhereClause(predicate);
//            System.out.println("5");
//            firstPart = MySQLVisitor.selectAsString(select);
//            System.out.println("6");
//            //System.out.println("firstPart: "+i+" "+firstPart);
//            String [] visitResult = firstPart.split("\\|\\|\\|\\|\\|");
//            System.out.println("7");
//            String[] conditionResult = visitResult[2].split("WHERE",2);
//
//            System.out.println("8");
//            if(!visitResult[2].equals("NO")){
//
//                System.out.println("9");
//                if(conditionResult.length==2){
//                    System.out.println("10");
//                    if(i==0){
//                        //System.out.println(visitResult[0]);
//                        //ansOrigin = "select (" + visitResult[0] +" limit 1) "+conditionResult[0];
//                        ansTrue = "select (" + visitResult[0] +" limit 1) "+conditionResult[0] + " where ("+conditionResult[1];
//                    }else{
//                        ansTrue = ansTrue+" and "+visitResult[1];
//                    }
//                }
//            }else{
//                System.out.println("11");
//                String[] noConditionResult = visitResult[0].split("WHERE",2);
//                if(i==0){
//
//                    //ansOrigin = noConditionResult[0];
//                    ansTrue = noConditionResult[0] + " where ("+noConditionResult[1];
//                } else{
//                    ansTrue=ansTrue + " and "+visitResult[1];
//                }
//            }
//        }
////        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(ansOrigin, errors, state);
//        ansTrue = ansTrue+")";
////        ansFalse = ansTrue + " IS FALSE";
////        ansNull = ansTrue +" IS UNKNOWN";
////        System.out.println(ansOrigin);
//        System.out.println("ansTrue: "+ansTrue);
//        //System.out.println(firstpattern);
//
//        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(ansTrue, errors, state);

        //select.setWhereClause(predicate);
        //清除list的生命周期
//        String username = "root";
//        String password = "MySQLTesting";
//        String host = state.getOptions().getHost();
//        int port = state.getOptions().getPort();
//        if (host == null) {
//            host = MySQLOptions.DEFAULT_HOST;
//        }
//        if (port == MainOptions.NO_SET_PORT) {
//            port = MySQLOptions.DEFAULT_PORT;
//        }
//        host = "211.81.52.44";
//        port = 3306;
//        String databasename = state.getDatabaseName();
//        state.getState().logStatement("DROP DATABASE IF EXISTS " + databasename);
//        state.getState().logStatement("CREATE DATABASE " + databasename);
//        state.getState().logStatement("USE " + databasename);
//        String url = String.format("jdbc:mysql://%s:%d?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
//                host, port);
//        Connection con = DriverManager.getConnection(url, username, password);
//        try (Statement s = con.createStatement()) {
//            s.execute("DROP DATABASE IF EXISTS " + databasename);
//        }
//        try (Statement s = con.createStatement()) {
//            s.execute("CREATE DATABASE " + databasename);
//        }
//        try (Statement s = con.createStatement()) {
//            s.execute("USE " + databasename);
//        }
//        for(Query<SQLConnection> createQuery:Main.createTableList){
//            String []items = createQuery.toString().split("\\s+");
//            if(items.length>3){
//                if(items[0].equals("CREATE")&&items[1].equals("TABLE")){
//                    List<String> statementList = new ArrayList<>();
//                    for(Constrain constrain:Main.createConstrainList){
//                        if(constrain.tableName.equals(items[2])){
//                            //根据蜕变关系重构这条create table语句
//                            statementList = ConstrainToTrigger(constrain,statementList);
//                            break;
//                        }
//                    }
//                    for(String statement:statementList){
//                        Query<SQLConnection> query = new SQLQueryAdapter(statement.toString(), errors);
//                        System.out.println(query.getQueryString());
//                        state.executeStatement(query);
//                    }
//                }else{
//                    System.out.println(createQuery.getQueryString());
//                    state.executeStatement(createQuery);
//                }
//            }else{
//                System.out.println(createQuery.getQueryString());
//                state.executeStatement(createQuery);
//            }
//
//        }
//
//        List<String> secondResultSet = ComparatorHelper.getResultSetFirstColumnAsString(ansTrue, errors, state);
//        ComparatorHelper.assumeResultSetsAreEqual(resultSet, secondResultSet, ansTrue, ansTrue,
//                state);
//        Main.totalQuery++;

//        String firstQueryString = MySQLVisitor.asString(select);
//
//
//        select.setWhereClause(negatedPredicate);
//        String secondQueryString = MySQLVisitor.asString(select);
//        select.setWhereClause(isNullPredicate);
//        String thirdQueryString = MySQLVisitor.asString(select);
        //List<String> combinedString = new ArrayList<>();

//        List<String> secondResultSet = ComparatorHelper.getCombinedResultSet(firstQueryString, secondQueryString,
//                thirdQueryString, combinedString, Randomly.getBoolean(), state, errors);
//        ComparatorHelper.assumeResultSetsAreEqual(resultSet, secondResultSet, originalQueryString, combinedString,
//                state);


    }
    public List<String> ConstrainToTrigger(Constrain constrain,List<String> list){
        list.add(constrain.withoutConstrain);

        for(int i = 0; i < constrain.columnName.size(); i++){
            ConstrainProcess(constrain.tableName,constrain.columnName.get(i),constrain.constrain.get(i),list,constrain.constrainContent.get(i));
        }
        return list;
    }
    public void ConstrainProcess(String tableName,String columnName,List<String> constrain,List<String>statement,List<String> constrainContent){
        for(int i=0;i<constrain.size();i++){
            switch (constrain.get(i)){
                case "PRIMARY KEY":
                    PrimaryKey(tableName,columnName,statement);
                    break;
                case "FOREIGN KEY" :
                    ForeignKey(tableName,columnName,statement,constrainContent.get(i).split("\\|\\|\\|\\|\\|")[1],constrainContent.get(i).split("\\(")[0]);
                case "UNIQUE":
                    Unique(tableName,columnName,statement);
                    break;
                case "NULL":
                    Null(tableName,columnName,statement);
                    break;
                case "NOT NULL":
                    NotNull(tableName,columnName,statement);
                    break;
                case "DECIMAL": case "TINYINT": case "SMALLINT": case "MEDIUMINT": case "INT": case "BIGINT": case "TINYTEXT": case "TEXT": case "MEDIUMTEXT": case "LONGTEXT": case "FLOAT": case "DOUBLE": case "UNSIGNED": case "ZEROFILL":
                    Type(tableName,columnName,statement,constrain.get(i),constrainContent.get(i));
                    break;
                case "CHECK":
                    Check(tableName,columnName,statement,constrainContent.get(i));
                    break;
                case "DEFAULT":
                    Default(tableName,columnName,statement,constrainContent.get(i));
                    break;
            }
        }
    }
    public void PrimaryKey(String tableName,String columnName,List<String>statement){
        StringBuilder createTrigger = new StringBuilder(
                "CREATE TRIGGER enforce_pk_"+tableName+"_"+columnName+" AFTER INSERT ON " + tableName + " " +
                        "BEGIN " +
                        "DELETE FROM " + tableName + " WHERE " + columnName + " = NEW." + columnName + " AND ROWID != NEW.ROWID; " +
                        "END;"
        );
        statement.add(createTrigger.toString());
    }
    public void ForeignKey(String tableName,String columnName,List<String>statement,String cascade,String parentTable){
        if(cascade.equals("DELETE")){
            StringBuilder createTrigger = new StringBuilder(
                    "CREATE TRIGGER cascade_delete_"+tableName+"_"+columnName+" AFTER DELETE ON " + parentTable + " " +
                            "BEGIN " +
                            "DELETE FROM " + tableName + " WHERE " + columnName + " = OLD." + columnName + "; " +
                            "END;"
            );
            statement.add(createTrigger.toString());
        }else if(cascade.equals("UPDATE")){
            StringBuilder createTrigger = new StringBuilder(
                    "CREATE TRIGGER cascade_update_"+tableName+"_"+columnName+" AFTER UPDATE ON " + parentTable + " " +
                            "BEGIN " +
                            "UPDATE " + tableName + " SET "+columnName+" = NEW."+columnName+"WHERE " + columnName + " = OLD." + columnName + "; " +
                            "END;"
            );
            statement.add(createTrigger.toString());
        }

    }
    public void Unique(String tableName,String columnName,List<String>statement){
        StringBuilder createTrigger = new StringBuilder(
                "CREATE TRIGGER enforce_unique_"+tableName+"_"+columnName+" AFTER INSERT ON " + tableName + " " +
                        "BEGIN " +
                        "DELETE FROM " + tableName + " WHERE " + columnName + " = NEW." + columnName + " AND ROWID != NEW.ROWID; " +
                        "END;"
        );
        statement.add(createTrigger.toString());
    }
    public void Null(String tableName,String columnName,List<String>statement){
        StringBuilder createTrigger = new StringBuilder(
                "CREATE TRIGGER enforce_null_"+tableName+"_"+columnName+" AFTER INSERT ON " + tableName + " " +
                        "BEGIN " +
                        "DELETE FROM " + tableName + " WHERE " + columnName + " IS NOT NULL; " +
                        "END;"
        );
        statement.add(createTrigger.toString());
    }
    public void NotNull(String tableName,String columnName,List<String>statement){
        StringBuilder createTrigger = new StringBuilder(
                "CREATE TRIGGER enforce_not_null_"+tableName+"_"+columnName+" AFTER INSERT ON " + tableName + " " +
                        "BEGIN " +
                        "DELETE FROM " + tableName + " WHERE " + columnName + " IS NULL; " +
                        "END;"
        );
        statement.add(createTrigger.toString());
    }
    public void Type(String tableName,String columnName,List<String>statement,String type,String typeContent){
        StringBuilder createTrigger = new StringBuilder(
                "CREATE TRIGGER check_type_"+tableName+"_"+columnName+" AFTER INSERT ON " + tableName + " " +
                        "BEGIN " +
                        "DELETE FROM " + tableName + " WHERE typeof(" + columnName + ") != '"+type+"'; " +
                        "END;"
        );
        statement.add(createTrigger.toString());
    }
    public void Check(String tableName,String columnName,List<String>statement,String typeContent){
        StringBuilder createTrigger = new StringBuilder(
                "CREATE TRIGGER enforce_check_"+tableName+"_"+columnName+" AFTER INSERT ON " + tableName + " " +
                        "BEGIN " +
                        "DELETE FROM " + tableName + " WHERE not(" + typeContent + ") and (" +typeContent+") is unknown; " +
                        "END;"
        );
        statement.add(createTrigger.toString());
    }
    public void Default(String tableName,String columnName,List<String>statement,String typeContent){
        StringBuilder createTrigger = new StringBuilder(
                "CREATE TRIGGER enforce_default_"+tableName+"_"+columnName+" AFTER INSERT ON " + tableName + " " +
                        "BEGIN " +
                        "UPDATE " + tableName + " set " + columnName + "='"+typeContent+"'" + " where "+columnName+"IS NULL; " +
                        "END;"
        );
        statement.add(createTrigger.toString());
    }
}
