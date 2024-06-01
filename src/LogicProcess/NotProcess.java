package LogicProcess;

import ASTStructure.TreeNode;
import ASTStructure.TreeValue;
import DataProcess.DataChange;
import DataProcess.GetStr;
import java_prolog.ScriptProlog;
import java_prolog.ScriptPrologCommandOrLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class NotProcess {
    public static String notProcess(List<TreeNode> childs, TreeValue treeValue) throws IOException {
        if(childs.size()==1){
            String parameter1 ="";
            if(ScriptProlog.logicKeyWordsList.contains(childs.get(0).val.keywordResult)|| ScriptProlog.FunctionList.contains(childs.get(0).val.keywordResult)){
                parameter1 = childs.get(0).val.tag;
            }else{
                parameter1 = childs.get(0).val.keywordResult;
            }
            return not(parameter1,treeValue.keywordResult,treeValue.parameter);
        }else{
            return "参数错误";
        }
    }
    public static String not(String x,String tempResult,String column) throws IOException {
        Process p;
//        if(!pattern.matcher(x).matches() && !(x.equals("true")) && !(x.equals("false"))&& !(x.equals("null"))){
//            x=stringToData(x,tempResult,column);
//        }
        System.out.println("hhh"+x+" "+tempResult+" "+column+"hhh");
        x = DataChange.dataChange(x,tempResult,column);
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Condition/not.pl'].\n").getBytes());
        out.write(("not_clause("+x+",Z).\n").getBytes());
        System.out.println("not_clause("+x+",Z).\n");
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
