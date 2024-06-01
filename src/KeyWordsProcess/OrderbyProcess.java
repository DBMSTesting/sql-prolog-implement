package KeyWordsProcess;

import DataProcess.GetStr;
import java_prolog.ScriptPrologCommandOrLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class OrderbyProcess {
    public static String orderbyProcess(List<ASTStructure.TreeNode> childs, String keywordResult) throws IOException {
        Process p;
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out4 = p.getOutputStream ();
        BufferedReader in4 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        String parameter = ParameterProcess.parameterProcess(childs);
        parameter = parameter.replace(".","^");
        keywordResult=orderby_function(out4,keywordResult,parameter,in4);
        System.out.println("ORDER BY结果："+keywordResult);
        return keywordResult;
    }
    public static String orderby_function(OutputStream out,String r,String parameter,BufferedReader in) throws IOException {
        String []order = parameter.split(" ");

        if(order[order.length-1].equals("ASC")){
            out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Group_operation/orderby.pl'].\n").getBytes());
            out.write(("orderby_clause("+r+","+order[0]+","+"asc,Z).\n").getBytes());
        }else if(order[order.length-1].equals("DESC")){
            out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Group_operation/orderby.pl'].\n").getBytes());
            out.write(("orderby_clause("+r+","+order[0]+","+"desc,Z).\n").getBytes());
        }else{
            out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Group_operation/orderby.pl'].\n").getBytes());
            out.write(("orderby_clause("+r+","+order[0]+","+"asc,Z).\n").getBytes());
        }
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            r += (line+" ");
        }

        String result="";
        result= GetStr.getStr(r);
        //System.out.println(result);
        return result;
    }

}
