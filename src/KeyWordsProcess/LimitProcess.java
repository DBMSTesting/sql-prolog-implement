package KeyWordsProcess;

import ASTStructure.TreeNode;
import DataProcess.GetStr;
import java_prolog.ScriptPrologCommandOrLogic;

import java.io.*;
import java.util.List;

public class LimitProcess {
    public static String limitProcess(List<TreeNode> childs, String keywordResult) throws IOException{
        Process p;
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        String parameter = ParameterProcess.parameterProcess(childs);
        String tempResult = limit_function(out,keywordResult,parameter,in);

        return tempResult;
    }

    public static String limit_function(OutputStream out,String r,String parameter,BufferedReader in) throws IOException{
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Query_Specification/limit.pl'].\n").getBytes());
        parameter = parameter.substring(1,parameter.length()-1);
        out.write(("limit_final("+r+","+parameter+",Z).\n").getBytes());
        System.out.println("limit_final("+r+","+parameter+",Z).\n");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            r += (line+" ");
            //System.out.println(line);
        }
        String result="";
        //result=getLimitResult("/Users/tashou/STEST/limit_result.txt",parameter);
        result = GetStr.getStr(r);
        System.out.println(result);
        return result;
    }

    public static String getLimitResult(String file,String parameter) throws IOException {
        parameter = "["+parameter+"]";
        String result = "[";
        String fileName = file;
        File file1 = new File(fileName);
        FileReader fr1 = new FileReader(file1);
        BufferedReader br1 = new BufferedReader(fr1);
        String line1;
        String []para = parameter.split(",");
        int i =0;
        while((line1 = br1.readLine()) != null ){
            if(i==para.length){
                i=0;
            }
            if(i<para.length){
                if(i==0 && para.length==1){
                    result+="[";
                    result+=(line1+"],");
                }else if(i==0 && para.length!=1){
                    result+="[";
                    result+=(line1+",");
                }else if(i !=para.length-1){
                    result+=(line1+",");
                }else{
                    //result=result.substring(0,result.length()-1);
                    result+=(line1+"],");
                }
            }
            i++;
        }
        result = result.substring(0,result.length()-1);
        result  += "]";
        return result;
    }

}
