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

public class CountProcess {
    public static String countProcess(List<ASTStructure.TreeNode> childs, String keywordResult) throws IOException {
        Process p;
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String parameter = ParameterProcess.parameterProcess(childs);
        parameter = parameter.replace(".","^");
        keywordResult=count_function(keywordResult,parameter,out,in);
        ScriptProlog.flag=1;
        return keywordResult;
    }
    public static String count_function(String tempResult, String parameter, OutputStream out, BufferedReader in) throws IOException {
//        String items = parameter.substring(1,parameter.length()-1);
//        String []item = items.split("\\.");
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Aggregation_operation/count.pl'].\n").getBytes());
        out.write(("count_clause("+tempResult+","+parameter+",Z).\n").getBytes());
        System.out.println("count_clause("+tempResult+","+parameter+",Z).");
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
