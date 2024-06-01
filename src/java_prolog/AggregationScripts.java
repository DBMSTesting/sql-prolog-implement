package java_prolog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class AggregationScripts {
    public static Process p;
    public static String prologCommand = "/opt/local/bin/swipl";
    public static String prologMainFile = "/Users/tashou/sql-prolog/src/prolog";
    public static String max_funxtion(String tempResult,String parameter,OutputStream out,BufferedReader in) throws IOException {
        String items = parameter.substring(1,parameter.length()-1);
        String []item = items.split("\\.");
        out.write(("['"+prologMainFile+"/Aggregation_operation/max.pl'].\n").getBytes());
        out.write(("max_clause("+tempResult+",["+item[1]+"],Z).\n").getBytes());
        System.out.println("max_clause("+tempResult+","+item[1]+",Z).");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            tempResult += (line+" ");
            //System.out.println(line);
        }
        String result="";
        result=getStr(tempResult);
        //System.out.println(result);
        return result;
    }
    public static String min_funxtion(String tempResult,String parameter,OutputStream out,BufferedReader in) throws IOException {
        String items = tempResult.substring(1,tempResult.length()-1);
        String []item = items.split("\\.");
        out.write(("['"+prologMainFile+"/Aggregation_operation/min.pl'].\n").getBytes());
        out.write(("min_clause("+tempResult+","+item[1]+",Z).\n").getBytes());
        //System.out.println("select_clause("+r+","+parameter+",Z).\n");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            tempResult += (line+" ");
            //System.out.println(line);
        }
        String result="";
        result=getStr(tempResult);
        //System.out.println(result);
        return result;
    }
    public static String sum_funxtion(String tempResult,String parameter,OutputStream out,BufferedReader in) throws IOException {
        String items = tempResult.substring(1,tempResult.length()-1);
        String []item = items.split("\\.");
        out.write(("['"+prologMainFile+"/Aggregation_operation/sum.pl'].\n").getBytes());
        out.write(("sum_clause("+tempResult+",0,"+item[1]+",Z).\n").getBytes());
        //System.out.println("select_clause("+r+","+parameter+",Z).\n");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            tempResult += (line+" ");
            //System.out.println(line);
        }
        String result="";
        result=getStr(tempResult);
        //System.out.println(result);
        return result;
    }
    public static String count_funxtion(String tempResult,String parameter,OutputStream out,BufferedReader in) throws IOException {
        String items = tempResult.substring(1,tempResult.length()-1);
        String []item = items.split("\\.");
        out.write(("['"+prologMainFile+"/Aggregation_operation/count.pl'].\n").getBytes());
        out.write(("count_clause("+tempResult+",0,"+item[1]+",Z).\n").getBytes());
        //System.out.println("select_clause("+r+","+parameter+",Z).\n");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            tempResult += (line+" ");
            //System.out.println(line);
        }
        String result="";
        result=getStr(tempResult);
        //System.out.println(result);
        return result;
    }
    public static String mainResult(String tempResult,String parameter,String type) throws IOException{
        String result = null;
        switch (type) {
            case "MAX":
                p = Runtime.getRuntime().exec(prologCommand);
                OutputStream out = p.getOutputStream ();
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                result = max_funxtion(tempResult,parameter,out,in);
                break;
            case "MIN":
                p = Runtime.getRuntime().exec(prologCommand);
                OutputStream out1 = p.getOutputStream ();
                BufferedReader in1 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                result = min_funxtion(tempResult,parameter,out1,in1);
                break;
            case "SUM":
                p = Runtime.getRuntime().exec(prologCommand);
                OutputStream out2 = p.getOutputStream ();
                BufferedReader in2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                result = sum_funxtion(tempResult,parameter,out2,in2);
                break;
            case "COUNT":
                p = Runtime.getRuntime().exec(prologCommand);
                OutputStream out3 = p.getOutputStream ();
                BufferedReader in3 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                result = count_funxtion(tempResult,parameter,out3,in3);
                break;
        }
        return result;
    }
    public static String getStr(String str){
        String []array=str.split("\\s+");
        int startIndex=0;
        int endIndex=0;
        for(int i=0;i<array.length;i++){
            if(array[i].equals("Z")){
                if(array[i+1].equals("=")){
                    startIndex=(i+2);
                }
            }
            if(array[i].equals("EOF:")){
                endIndex=(i-1);
                break;
            }
        }
        String newStr="";
        for(int j=startIndex;j<(endIndex+1);j++){
            newStr+=array[j];
        }
        return newStr;
    }
}
