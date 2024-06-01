package JoinProcess;

import DataProcess.GetStr;
import java_prolog.ScriptProlog;
import java_prolog.ScriptPrologCommandOrLogic;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class LeftJoinProcess {
    public static List<String> JOINLIST = new ArrayList<String>();
    public static String leftjoinProcess(String keywordResult) throws IOException {
        String []items = keywordResult.split("],\\[");
        String strColumn = items[0].substring(2);
        String []datas = strColumn.split(",");
        HashSet<Integer> columnSet = new HashSet<>();
        HashSet<String> dataSet = new HashSet<>();
        String dataAll="[[";
        for(int i=0;i< datas.length;i++){
            String []table = datas[i].split("\\^");
            if(table[0].equals(ScriptProlog.LeftTable)){
                columnSet.add(i);
            }
        }
//        for(int j=1;j<items.length;j++){
//            String strTrue="";
//            String []data = items[j].split(",");
//            for(int t=0;t<data.length;t++){
//                if(columnSet.contains(t)){
//                    strTrue+=(data[t]+",");
//                }else{
//                    strTrue+=("\\X,");
//                }
//            }strTrue=strTrue.substring(0,strTrue.length()-1);
//            dataSet.add(strTrue);
//        }

        //处理LeftList
        for(int x=0;x< ScriptProlog.JOINLIST.size();x++){
            String strFalse = "";
            String []datafalse = ScriptProlog.JOINLIST.get(x).split(",");
            for(int y = 0;y<datafalse.length;y++){
                if(columnSet.contains(y)){
                    strFalse+=(datafalse[y]+",");
                }else{
                    strFalse+=("\\X,");
                }
            }strFalse=strFalse.substring(0,strFalse.length()-1);
//            if(!dataSet.contains(strFalse)){
//                dataAll+=(strFalse+"],[");
//            }
            dataAll+=(strFalse+"],[");
        }
        dataAll=dataAll.substring(0,dataAll.length()-2);
        dataAll+="]";

        System.out.println(dataAll);


        Process p;
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        String tempResult = leftjoin_function(out,keywordResult,dataAll,in);


        return tempResult;
        //return "";
    }
    public static String leftjoin_function(OutputStream out,String r,String parameter,BufferedReader in) throws IOException{
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Join_operation/leftjoin.pl'].\n").getBytes());
        out.write(("leftjoin_clause("+r+","+parameter+",Z).\n").getBytes());
        System.out.println("leftjoin_clause("+r+","+parameter+",Z).\n");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            r += (line+" ");
            //System.out.println(line);
        }
        String result="";
        result = GetStr.getStr(r);
        result=getJoinResult("/Users/tashou/join_result.txt",parameter);
        return result;
    }

    public static String getJoinResult(String file,String parameter) throws IOException {
        String result = "[";
        String fileName = file;
        File file1 = new File(fileName);
        FileReader fr1 = new FileReader(file1);
        BufferedReader br1 = new BufferedReader(fr1);
        String line1;
        String []para = parameter.split(",");
        int i =0;
        while((line1 = br1.readLine()) != null ){
            line1=line1.replace("[[","[");
            line1=line1.replace("]]","]");
            result = result+line1+",";
            i++;
        }
        result = result.substring(0,result.length()-1);
        result  += "]";
        return result;
    }

    public static void testList(){
        JOINLIST.add("1,2,3,5,6");
        JOINLIST.add("2,2,3,5,6");
    }

    public static void main(String[] args) throws IOException {
        testList();
        String data = LeftJoinProcess.leftjoinProcess("[[t1^a,t2^b,t1^c,t1^d,t2^e],[1,2,3,4,5],[2,3,4,5,6]]");
    }
}
