package LogicProcess;

import DataProcess.DataChange;
import DataProcess.GetStr;
import DataProcess.StringToData;
import java_prolog.ScriptProlog;
import java_prolog.ScriptPrologCommandOrLogic;
import ASTStructure.TreeValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class InProcess {

    public static String inProcess(List<ASTStructure.TreeNode> childs, TreeValue treeValue) throws IOException {
        if(childs.size()==2){
            String parameter1 ="";
            if(ScriptProlog.logicKeyWordsList.contains(childs.get(0).val.keywordResult) || ScriptProlog.FunctionList.contains(childs.get(0).val.keywordResult)){
                parameter1 = childs.get(0).val.tag;
            }else{
                parameter1 = childs.get(0).val.keywordResult;
            }
            String parameter2 ="";
            if(ScriptProlog.logicKeyWordsList.contains(childs.get(1).val.keywordResult)|| ScriptProlog.FunctionList.contains(childs.get(1).val.keywordResult)){
                parameter2 = childs.get(1).val.tag.substring(1,childs.get(1).val.tag.length()-1);
            }else{
                parameter2 = childs.get(1).val.keywordResult.substring(1,childs.get(1).val.keywordResult.length()-1);
            }
            return in(parameter1,parameter2,treeValue.keywordResult,treeValue.parameter);
        }else{
            return "参数错误";
        }
    }
    public static String in(String x,String y,String tempResult,String column) throws IOException {
        x = DataChange.dataChange(x,tempResult,column);
        Process p;
        String []items = y.split(",");
        String str = "[";
        for(int i=0;i<items.length;i++){
            if(items[i].contains(".") && !(items[i].substring(0,1).equals("'") && items[i].substring(items[i].length()-1,items[i].length()).equals("'"))){
                String data1= StringToData.stringToData(items[i],tempResult,column);
                str+=(DataChange.dataChange(data1,tempResult,column)+",");
                System.out.println(x);
            }else{
                str+=(DataChange.dataChange(items[i],tempResult,column)+",");
            }

        }
        str=str.substring(0,str.length()-1);
        str+="]";
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Condition/in.pl'].\n").getBytes());
        out.write(("in_clause("+x+","+str+",0,Z).\n").getBytes());
        System.out.println("in_clause("+x+","+str+",0,Z).");
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
