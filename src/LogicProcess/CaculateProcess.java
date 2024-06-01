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

public class CaculateProcess {
    public static String caculateProcess(String keyword,List<TreeNode> childs, TreeValue treeValue) throws IOException {
        if(childs.size()==2){
            String parameter1 ="";
            if(ScriptProlog.logicKeyWordsList.contains(childs.get(0).val.keywordResult)|| ScriptProlog.FunctionList.contains(childs.get(0).val.keywordResult)){
                parameter1 = childs.get(0).val.tag;
            }else{
                parameter1 = childs.get(0).val.keywordResult;
            }
            String parameter2 ="";
            if(ScriptProlog.logicKeyWordsList.contains(childs.get(1).val.keywordResult)|| ScriptProlog.FunctionList.contains(childs.get(1).val.keywordResult)){
                parameter2 = childs.get(1).val.tag;
            }else{
                parameter2 = childs.get(1).val.keywordResult;
            }
            System.out.println(parameter1+"***"+parameter2);
            return caculate(keyword,parameter1,parameter2,treeValue.keywordResult,treeValue.parameter);
        }else{
            return "参数错误";
        }
    }
    public static String caculate(String keyword,String x,String y,String tempResult,String column) throws IOException {
        Process p;
        System.out.println("输出1："+ x);
        System.out.println("输出2："+ y);
        x = DataChange.dataChange(x,tempResult,column);
        y = DataChange.dataChange(y,tempResult,column);
//        if(!pattern.matcher(x).matches()||!pattern.matcher(y).matches()){
//            x = stringToData(x);
//            y = stringToData(y);
//        }
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        if(keyword.equals("+")){
            out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Condition/add.pl'].\n").getBytes());
            out.write(("add_clause("+x+","+y+",Z).\n").getBytes());
            System.out.println("add_clause("+x+","+y+",Z).");
        }else if(keyword.equals("-")){
            out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Condition/subtract.pl'].\n").getBytes());
            out.write(("subtract_clause("+x+","+y+",Z).\n").getBytes());
            System.out.println("subtract_clause("+x+","+y+",Z).");
        }else if(keyword.equals("*")){
            out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Condition/multiply.pl'].\n").getBytes());
            out.write(("multiply_clause("+x+","+y+",Z).\n").getBytes());
            System.out.println("multiply_clause("+x+","+y+",Z).");
        }else if(keyword.equals("/")){
            out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Condition/divide.pl'].\n").getBytes());
            out.write(("divide_clause("+x+","+y+",Z).\n").getBytes());
            System.out.println("divide_clause("+x+","+y+",Z).");
        }
        //out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Condition/and.pl'].\n").getBytes());

        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            tempResult += (line+" ");
        }
        String result="";
        System.out.println(tempResult);
        result= GetStr.getStr(tempResult);
        return result;
    }
}
