package BitProcess;

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

public class BitTwoParaProcess {
    public static String bitTwoProcess(String keyword , List<TreeNode> childs, TreeValue treeValue) throws IOException {
        if(childs.size()==2){
            String parameter1 ="";
            if(ScriptProlog.logicKeyWordsList.contains(childs.get(0).val.keywordResult)){
                parameter1 = childs.get(0).val.tag;
            }else{
                parameter1 = childs.get(0).val.keywordResult;
            }

            String parameter2 ="";
            if(ScriptProlog.logicKeyWordsList.contains(childs.get(1).val.keywordResult)){
                parameter2 = childs.get(1).val.tag;
            }else{
                parameter2 = childs.get(1).val.keywordResult;
            }

            return bitTwoPara(keyword,parameter1,parameter2,treeValue.keywordResult,treeValue.parameter);
        }else{
            return "参数错误";
        }
    }
    public static String bitTwoPara(String keyword,String x,String y,String tempResult,String column) throws IOException {
        Process p;
//        if(!pattern.matcher(x).matches() && !(x.equals("true")) && !(x.equals("false"))&& !(x.equals("null"))){
//            x=stringToData(x,tempResult,column);
//        }
        System.out.println("hhh"+x+" "+tempResult+" "+column+"hhh");
        x = DataChange.dataChange(x,tempResult,column);
        y = DataChange.dataChange(y,tempResult,column);
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        if(keyword.equals("&")){
            keyword = "bit_and";
        }else if(keyword.equals("|")){
            keyword = "bit_or";
        }else if(keyword.equals("^")){
            keyword = "bit_xor";
        }else if(keyword.equals("<<")){
            keyword = "bit_shift_left";
        }else if(keyword.equals(">>")){
            keyword = "bit_shift_right";
        }
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Bit/"+keyword+".pl'].\n").getBytes());
        out.write((keyword+"_clause("+x+","+y+",Z).\n").getBytes());
        System.out.println(keyword+"_clause("+x+","+y+",Z).\n");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            tempResult += (line+" ");
            //System.out.println(line);
        }
        String result="";
        System.out.println(tempResult);
        result= GetStr.getStr(tempResult);
        System.out.println("位运算符结果："+result);
        return result;
    }
}
