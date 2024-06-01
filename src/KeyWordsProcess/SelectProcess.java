package KeyWordsProcess;

import java_prolog.ScriptProlog;
import java_prolog.ScriptPrologCommandOrLogic;

import java.io.*;
import java.util.List;
import java.util.regex.Pattern;

public class SelectProcess {
    public static Pattern pattern = Pattern.compile("^[-+]?[0-9]+(\\.[0-9]+)?$");

    public static String selectProcess(List<ASTStructure.TreeNode> childs, String keywordResult) throws IOException {
        if(ScriptProlog.flag==1){
            ScriptProlog.flag=0;
            return keywordResult;
        }else{
            Process p;
            p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
            OutputStream out = p.getOutputStream ();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            String parameter = ParameterProcess.parameterProcess(childs);
            String tempResult = select_function(out,keywordResult,parameter,in);

            return tempResult;
        }

    }
    public static String select_function(OutputStream out,String r,String parameter,BufferedReader in) throws IOException {
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Query_Specification/select.pl'].\n").getBytes());
        String tempPara = parameter.substring(1,parameter.length()-1);
        if(parameter.equals("[*]")){
            if(r.equals("[]")){
                parameter = "[]";
            }else{
                String []items = r.substring(1,r.length()-1).split("]",2);
                String []parameter_temp = items[0].split(",");
                parameter = "";
                for(String str:parameter_temp){
                    if(str.contains("rowid")){
                        continue;
                    }else{
                        parameter += str+",";
                    }
                }
                parameter = parameter.substring(0,parameter.length()-1)+"]" ;
                //System.out.println("hhhh"+parameter);
            }

        }else if(pattern.matcher(tempPara).matches()){
            String []items = r.substring(2,r.length()-2).split("],\\[");
            String tempR = "[[";
            for(String item:items){
                tempR = tempR + item+","+tempPara+"],[";
            }
            tempR = tempR.substring(0,tempR.length()-2) + "]";
            r = tempR;
        }
        out.write(("select_final("+r+","+parameter+",Z).\n").getBytes());
        System.out.println("select_final("+r+","+parameter+",Z).\n");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            r += (line+" ");
            //System.out.println(line);
        }
        String result="";
        result=getSelectResult("/Users/tashou/STEST/select_result.txt",parameter);

        System.out.println(result);
        return result;
    }
    public static String getSelectResult(String file,String parameter) throws IOException {
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
        if(result.length()!=1){
            result = result.substring(0,result.length()-1);
        }

        result  += "]";
        return result;
    }
}
