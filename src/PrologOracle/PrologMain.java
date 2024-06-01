package PrologOracle;

import Comparison.Compare;
import GetDatabaseData.DatabaseInfoBuilder;
import java_prolog.ScriptMysql;
import java_prolog.ScriptProlog;
import java_prolog.SqlToJsonConverter;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class PrologMain {
    public static String prologCommand = "/opt/local/bin/swipl";
    public static String prologMainFile = "/Users/tashou/sql-prolog/src/prolog";
//    public static Process p;
//    public static OutputStream out;
//    public static BufferedReader in;
//
//    public static String totalLine;
//    public static int flag = 0;

    //String content = DatabaseInfoBuilder.buildDatabaseInfo("database"+i+"-cur.log");
    public static void prologmain(String sql,String database,String content,BufferedWriter bwUnConsistent) throws Throwable {

        //获得dbms的执行结果
        String resultSQL = ScriptMysql.scriptMysql(sql,database);

        //获得prolog的执行结果
        String resultProlog = "";
        String sqljson = SqlToJsonConverter.convertSqlToJson(sql);
        int sum = 0;
        for(int j = 0;j<sqljson.length();j++){
            if(sqljson.substring(j,j+1).equals("'")){
                sum ++;
            }
        }if(sum%2!=0){
            resultProlog = "引号错误";
        }
        sqljson = sqljson.replace("\'","");
        sqljson = sqljson.replace("\"","\'");
        try{
            resultProlog = ScriptProlog.caculateTree(sqljson,content);
            System.out.println(resultProlog);
        }catch (Exception e){
            e.printStackTrace();
        }

        //结果比较并筛选出不一致现象
        Compare.compareResult(resultProlog,resultSQL,bwUnConsistent);
    }
}
