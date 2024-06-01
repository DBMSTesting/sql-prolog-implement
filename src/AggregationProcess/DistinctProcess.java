package AggregationProcess;

import DataProcess.GetStr;
import KeyWordsProcess.ParameterProcess;
import java_prolog.ScriptProlog;
import java_prolog.ScriptPrologCommandOrLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class DistinctProcess {
    public static String distinctProcess(List<ASTStructure.TreeNode> childs, String keywordResult) throws IOException {
        Process p;
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String parameter = ParameterProcess.parameterProcess(childs);
        parameter = parameter.replace(".","^");
        keywordResult=distinct_function(keywordResult,parameter,out,in);
        ScriptProlog.flag=1;
        return keywordResult;
    }
    public static String distinct_function(String tempResult, String parameter, OutputStream out, BufferedReader in) throws IOException {
//        String items = parameter.substring(1,parameter.length()-1);
//        String []item = items.split("\\^");
//        System.out.println(item[0]);
        System.out.println(tempResult);
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Group_operation/distinct.pl'].\n").getBytes());
        if(parameter.equals("[*]")){
            if(tempResult.equals("[]")){
                parameter = "[]";
            }else{
                String []items = tempResult.substring(1,tempResult.length()-1).split("],\\[",2);
                String []parameter_temp = items[0].split(",");
                parameter = "";
                for(String str:parameter_temp){
                    if(str.contains("rowid")){
                        continue;
                    }else{
                        parameter += str+",";
                    }
                }
                parameter = parameter.substring(0,parameter.length()-1)+"]" ;
                //System.out.println("hhhh"+parameter);
            }

        }
        out.write(("distinct_clause("+tempResult+","+parameter+",Z).\n").getBytes());
        System.out.println("distinct_clause("+tempResult+","+parameter+",Z).");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            tempResult += (line+" ");
            //System.out.println(line);
        }
        String result="";
        result= GetStr.getStr(tempResult);
        //System.out.println(result);
        return result;
    }
}
