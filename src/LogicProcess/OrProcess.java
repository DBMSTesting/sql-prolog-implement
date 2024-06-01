package LogicProcess;

import DataProcess.DataChange;
import DataProcess.GetStr;
import java_prolog.ScriptProlog;
import java_prolog.ScriptPrologCommandOrLogic;
import ASTStructure.TreeValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class OrProcess {
    public static String orProcess(List<ASTStructure.TreeNode> childs, TreeValue treeValue) throws IOException {
        if(childs.size()>=2){
            String result = "false";
            for(int i = 0;i<childs.size();i++){
                String parameter1 ="";
                if(ScriptProlog.logicKeyWordsList.contains(childs.get(i).val.keywordResult)|| ScriptProlog.FunctionList.contains(childs.get(i).val.keywordResult)){
                    parameter1 = childs.get(i).val.tag;
                }else{
                    parameter1 = childs.get(i).val.keywordResult;
                }
                result = or(result,parameter1,treeValue.keywordResult,treeValue.parameter);
            }
            return result;
        }else{
            return "参数错误";
        }
//        if(childs.size()==2){
//            String parameter1 = childs.get(0).val.keywordResult;
//            String parameter2 = childs.get(1).val.keywordResult;
//            return or(parameter1,parameter2,treeValue.keywordResult,treeValue.parameter);
//        }else{
//            return "参数错误";
//        }
    }
    public static String or(String x,String y,String tempResult,String column) throws IOException {
        Process p;
        System.out.println("输出验证");
        System.out.println(x);
        System.out.println(y);
        x = DataChange.dataChange(x,tempResult,column);
        y = DataChange.dataChange(y,tempResult,column);
//        if(!pattern.matcher(x).matches()||!pattern.matcher(y).matches()){
//            x = stringToData(x);
//            y = stringToData(y);
//        }
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Condition/or.pl'].\n").getBytes());
        out.write(("or_clause("+x+","+y+",Z).\n").getBytes());
        System.out.println("or_clause("+x+","+y+",Z).");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            tempResult += (line+" ");
        }
        String result="";
        result= GetStr.getStr(tempResult);
        return result;
    }
}
