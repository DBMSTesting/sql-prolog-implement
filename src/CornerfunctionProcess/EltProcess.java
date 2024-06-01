package CornerfunctionProcess;

import ASTStructure.TreeNode;
import ASTStructure.TreeValue;
import DataProcess.DataChange;
import DataProcess.GetStr;
import DataProcess.StringToData;
import java_prolog.ScriptProlog;
import java_prolog.ScriptPrologCommandOrLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class EltProcess {
    public static String eltProcess(List<TreeNode> childs, TreeValue treeValue) throws IOException {
        if(childs.size()==1){
            String parameter1 ="";
            if(ScriptProlog.logicKeyWordsList.contains(childs.get(0).val.keywordResult)|| ScriptProlog.FunctionList.contains(childs.get(0).val.keywordResult)){
                parameter1 = childs.get(0).val.tag;
            }else{
                parameter1 = childs.get(0).val.keywordResult;
            }

            return elt(parameter1,treeValue.keywordResult,treeValue.parameter);
        }else{
            return "参数错误";
        }
    }
    public static String elt(String y,String tempResult,String column) throws IOException {
        Process p;
        String []items = y.split(",");
        String str = "[";
        for(int i=0;i<items.length;i++){
            if(items[i].contains(".") && !(items[i].substring(0,1).equals("'") && items[i].substring(items[i].length()-1,items[i].length()).equals("'"))){
                str+=(StringToData.stringToData(items[i],tempResult,column)+",");
            }else{
                str+=(items[i]+",");
            }
            str+=(DataChange.dataChange(items[i],tempResult,column)+",");
        }
        str=str.substring(0,str.length()-1);
        str+="]";
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Cornor_function/elt.pl'].\n").getBytes());
        out.write(("elt_clause("+str+",Z).\n").getBytes());
        System.out.println("elt_clause("+str+",Z).");
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
