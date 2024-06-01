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

public class AndProcess {
    public static String andProcess(List<ASTStructure.TreeNode> childs, TreeValue treeValue) throws IOException {
        if(childs.size()>=2){
            String result = "true";
            for(int i = 0;i<childs.size();i++){
                String parameter1 ="";
                if(ScriptProlog.logicKeyWordsList.contains(childs.get(i).val.keywordResult)|| ScriptProlog.FunctionList.contains(childs.get(i).val.keywordResult)){
                    parameter1 = childs.get(i).val.tag;
                }else{
                    parameter1 = childs.get(i).val.keywordResult;
                }

                result = and(result,parameter1,treeValue.keywordResult,treeValue.parameter);
            }
            return result;
        }else{
            return "参数错误";
        }
    }
    public static String and(String x,String y,String tempResult,String column) throws IOException {
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
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Condition/and.pl'].\n").getBytes());
        out.write(("and_clause("+x+","+y+",Z).\n").getBytes());
        System.out.println("and_clause("+x+","+y+",Z).");
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
