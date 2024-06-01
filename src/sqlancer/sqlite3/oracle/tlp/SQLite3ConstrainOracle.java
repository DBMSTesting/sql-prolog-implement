package sqlancer.sqlite3.oracle.tlp;

import sqlancer.ComparatorHelper;
import sqlancer.Main;
import sqlancer.Randomly;
import sqlancer.SQLConnection;
import sqlancer.common.query.Query;
import sqlancer.common.query.SQLQueryAdapter;
import sqlancer.mysql.gen.Constrain;
import sqlancer.sqlite3.SQLite3GlobalState;
import sqlancer.sqlite3.SQLite3Visitor;

//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLite3ConstrainOracle extends SQLite3TLPBase {
    private String generatedQueryString;

    public SQLite3ConstrainOracle(SQLite3GlobalState state) {
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
//        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(originalQueryString, errors, state);

        boolean orderBy = Randomly.getBooleanWithSmallProbability();
        if (orderBy) {
            select.setOrderByExpressions(gen.generateOrderBys());
        }
        select.setWhereClause(predicate);
        String firstQueryString = SQLite3Visitor.asSelectString(select);
        String [] array = firstQueryString.split("\\|\\|\\|\\|\\|");
        String ansTrue = "";
        //String ansFalse = "";
        //String ansNull = "";
        if(!array[1].equals("")){
            ansTrue = originalQueryString + " where "+array[1];
            //ansFalse = originalQueryString+ " where not( "+array[1] +")";
            //ansNull = originalQueryString+ " where ( "+array[1] +") is unknown";
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
        List<String> resultSet = ComparatorHelper.getResultSetFirstColumnAsString(ansTrue, errors, state);


        for(Query<SQLConnection> createQuery: Main.createTableList){
            String []items = createQuery.toString().split("\\s+");
            if(items.length>3){
                if(items[0].equals("CREATE")&&items[1].equals("TABLE")){
                    List<String> statementList = new ArrayList<>();
                    for(Constrain constrain:Main.createConstrainList){
                        if(constrain.tableName.equals(items[2])){
                            //根据蜕变关系重构这条create table语句
                            statementList = ConstrainToTrigger(constrain,statementList);
                            break;
                        }
                    }
                    for(String statement:statementList){
                        Query<SQLConnection> query = new SQLQueryAdapter(statement.toString(), errors);
                        state.executeStatement(query);
                    }
                }else{
                    state.executeStatement(createQuery);
                }
            }else{
                state.executeStatement(createQuery);
            }

        }
        List<String> secondResultSet = ComparatorHelper.getResultSetFirstColumnAsString(ansTrue, errors, state);
        ComparatorHelper.assumeResultSetsAreEqual(resultSet, secondResultSet, ansTrue, ansTrue,
                state);
//        List<String> combinedString = new ArrayList<>();
//        //List<String> secondResultSet = ComparatorHelper.getCombinedResultSet(ansTrue, ansFalse,ansNull, combinedString, !orderBy, state, errors);
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

    @Override
    public String getLastQueryString() {
        return generatedQueryString;
    }
}
