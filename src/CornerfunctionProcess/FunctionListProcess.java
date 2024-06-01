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

public class FunctionListProcess {
    public static String functionProcess(String keyword,List<TreeNode> childs, TreeValue treeValue) throws IOException {
        if(ScriptPrologCommandOrLogic.LogicListSpcParameter.contains(keyword)){
            String parameter0 ="";
            if(ScriptProlog.logicKeyWordsList.contains(childs.get(0).val.keywordResult)){
                parameter0 = childs.get(0).val.tag;
            }else{
                parameter0 = childs.get(0).val.keywordResult;
            }
            String str = "[";
            for(int i=1;i<childs.size();i++){
                String parameter1 ="";
                if(ScriptProlog.logicKeyWordsList.contains(childs.get(i).val.keywordResult)){
                    parameter1 = childs.get(i).val.tag;
                }else{
                    parameter1 = childs.get(i).val.keywordResult;
                }
                if(parameter1.contains(".") && !(parameter1.substring(0,1).equals("'") && parameter1.substring(parameter1.length()-1).equals("'"))){
                    str+=(StringToData.stringToData(parameter1,treeValue.keywordResult,treeValue.parameter)+",");
                }else{
                    str+=(parameter1+",");
                }
                str+=(DataChange.dataChange(parameter1,treeValue.keywordResult,treeValue.parameter)+",");
            }
            str=str.substring(0,str.length()-1);
            str+="]";
            return functionList(keyword,parameter0,str,treeValue.keywordResult,treeValue.parameter);
        }else if(ScriptPrologCommandOrLogic.LogicListParameter.contains(keyword)){
            String parameter0 ="";
            if(ScriptProlog.logicKeyWordsList.contains(childs.get(0).val.keywordResult)){
                parameter0 = childs.get(0).val.tag;
            }else{
                parameter0 = childs.get(0).val.keywordResult;
            }
            for(int i=1;i<childs.size();i++){
                String parameter1 ="";
                if(ScriptProlog.logicKeyWordsList.contains(childs.get(i).val.keywordResult)){
                    parameter1 = childs.get(i).val.tag;
                }else{
                    parameter1 = childs.get(i).val.keywordResult;
                }
                String result = functionParas(keyword,parameter0,parameter1,treeValue.keywordResult,treeValue.parameter);
                parameter0 = result;
            }
            return parameter0;
        }
        return "关键字不在范围内";


    }
    public static String functionList(String keyword,String x,String y,String tempResult,String column) throws IOException {
        Process p;

        x = DataChange.dataChange(x,tempResult,column);
        y = DataChange.dataChange(y,tempResult,column);
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Cornor_function/"+keyword+".pl'].\n").getBytes());
        out.write((keyword+"_clause("+x+","+y+",Z).\n").getBytes());
        System.out.println(keyword+"_clause("+y+",Z).");
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
    public static String functionParas(String keyword,String x,String y,String tempResult,String column) throws IOException {
        Process p;

        x = DataChange.dataChange(x,tempResult,column);
        y = DataChange.dataChange(y,tempResult,column);
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Cornor_function/"+keyword+".pl'].\n").getBytes());
        out.write((keyword+"_clause("+x+","+y+",Z).\n").getBytes());
        System.out.println(keyword+"_clause("+y+",Z).");
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
