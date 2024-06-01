package LogicProcess;

import ASTStructure.TreeNode;
import ASTStructure.TreeValue;
import DataProcess.DataChange;
import DataProcess.GetStr;
import java_prolog.ScriptPrologCommandOrLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class MissingProcess {
    public static String missProcess(List<TreeNode> childs, TreeValue treeValue) throws IOException {
        if(childs.size()==1){
            String parameter1 = childs.get(0).val.keywordResult;
            return missing(parameter1,treeValue.keywordResult,treeValue.parameter);
        }else{
            return "参数错误";
        }
    }
    public static String missing(String x, String tempResult, String column) throws IOException {
        Process p;
        x = DataChange.dataChange(x, tempResult, column);
//        if(!pattern.matcher(x).matches()||!pattern.matcher(y).matches()){
//            x = stringToData(x);
//            y = stringToData(y);
//        }
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['" + ScriptPrologCommandOrLogic.prologMainFile + "/Condition/missing.pl'].\n").getBytes());
        out.write(("missing_clause(" + x  + ",Z).\n").getBytes());
        System.out.println("missing_clause(" + x  + ",Z).");
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
