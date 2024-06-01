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

public class GreaterProcess {

    public static String greaterProcess(List<ASTStructure.TreeNode> childs, TreeValue treeValue) throws IOException {
        if (childs.size() == 2) {
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
            if(parameter1.equals("query")){
                parameter1 = childs.get(0).val.currentValue;
            }
            if(parameter2.equals("query")){
                parameter2 = treeValue.currentValue;
            }
            return biger(parameter1, parameter2, treeValue.keywordResult, treeValue.parameter);
        } else {
            return "参数错误";
        }
    }

    public static String biger(String x, String y, String tempResult, String column) throws IOException {
        Process p;
        System.out.println(x+"..."+y+"..."+tempResult);
        x = DataChange.dataChange(x, tempResult, column);
        y = DataChange.dataChange(y, tempResult, column);
//        if(!pattern.matcher(x).matches()||!pattern.matcher(y).matches()){
//            x = stringToData(x);
//            y = stringToData(y);
//        }
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['" + ScriptPrologCommandOrLogic.prologMainFile + "/Condition/bigger.pl'].\n").getBytes());
        out.write(("bigger_clause(" + x + "," + y + ",Z).\n").getBytes());
        System.out.println("bigger_clause(" + x + "," + y + ",Z).");
        out.flush();
        out.close();
        String line;
        while ((line = in.readLine()) != null) {
            tempResult += (line + " ");
            //System.out.println(line);
        }
        String result = "";
        result = GetStr.getStr(tempResult);
        //System.out.println(result);
        return result;
    }
}
