package KeyWordsProcess;

import java_prolog.ScriptProlog;
import java_prolog.ScriptPrologCommandOrLogic;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Comparison.Compare.datetime;

public class FromProcess {
    public static String fromProcess(List<ASTStructure.TreeNode> childs, String keywordResult) throws IOException {
        Process p;
        p = Runtime.getRuntime().exec(ScriptPrologCommandOrLogic.prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        if(childs.size()>1){
            ScriptProlog.LeftTable = childs.get(0).val.keywordResult;
            ScriptProlog.RightTable = childs.get(1).val.keywordResult;
        }

        String parameter = ParameterProcess.parameterProcess(childs);
        String tempResult=from_function(out,"",parameter,in,keywordResult);

        tempResult=tempResult.substring(2,tempResult.length()-3);

        String []itemsArray = new String[2];
        tempResult = tempResult.replace(" ","");

        String []items=tempResult.split("]],\\[");
        List<List<String>> strings = new ArrayList<>();
        String strColumn="[";
        for(int j = 0;j<items.length;j++){
            itemsArray=items[j].split(",\\[",2);

            String []itemTemp=itemsArray[1].split("],\\[",2);
            String []column=itemTemp[0].split(",");
            for(int k=0;k<column.length;k++){
                strColumn+=(itemsArray[0]+"^"+column[k]+",");
            }
            String []item = itemTemp[1].split("],\\[");

            strings.add(new ArrayList(Arrays.asList(item)));
            //map.put(itemsArray[0],item);
        }
        strColumn = strColumn.substring(0,strColumn.length()-1);
        strColumn += "]";

        List<String> list = test(strings, strings.get(0), "", new ArrayList<>());


        String fromresult="["+strColumn;
        for(int q=0;q<list.size();q++){
            fromresult+=(","+"["+list.get(q)+"]");
        }
        fromresult +="]";
        tempResult = fromresult;
        System.out.println("FROM结果："+tempResult);
        return tempResult;
    }
    public static List<String> test(List<List<String>> list, List<String> arr, String str, List<String> result) {
        for (int i = 0; i < list.size(); i++) {
            //取得当前的集合
            if (i == list.indexOf(arr)) {
                //迭代集合
                for (String st : arr) {
                    if(!str.equals("")){
                        st = str +","+ st;
                    }
                    if (i < list.size() - 1) {
                        test(list, list.get(i + 1), st, result);
                    } else if (i == list.size() - 1) {
                        result.add(st);
                    }
                }
            }
        }
        return result;
    }
    public static String from_function(OutputStream out,String r,String parameter,BufferedReader in,String content) throws IOException {
        content = datetime(content);
        content = content.replace(")]","]]");
        content = content.replace("), (","],[");
        content = content.replace("], (","],[");

        content = content.replace("None","none");
        out.write(("['"+ ScriptPrologCommandOrLogic.prologMainFile+"/Query_Specification/from.pl'].\n").getBytes());
        //out.write(("from_clause([[t0,[c0,c1],[3,0],[4,0],[5,0]],[t1,[c0,c1],[null,0],[1,0],[1,2]]],["+parameter+"],Z).\n").getBytes());
        out.write(("from("+content+","+parameter+",Z).\n").getBytes());
        System.out.println("from("+content+",["+parameter+"],Z).");
        //out.write(("from_clause([[t0,[c0,c1],['a',1],['b',2],['c',3],['d',4]],[t1,[c0,c1,c2],[null,0,0],[1,1,2]]],["+parameter+"],Z).\n").getBytes());
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            r += (line+" ");
        }
        System.out.println("from输出");
        System.out.println(r);
        String result="";
        result = getFromResult("/Users/tashou/sql-prolog/example.txt");
//        result=getStr(r);
        System.out.println("result:"+result);
        return result;
    }
    public static String getFromResult(String file) throws IOException {
        String result = "[";
        String fileName = file;
        File file1 = new File(fileName);
        FileReader fr1 = new FileReader(file1);
        BufferedReader br1 = new BufferedReader(fr1);
        String line1;
        while((line1 = br1.readLine()) != null){
            result += line1+",";
        }
        result = result.substring(0,result.length()-1);
        result  += "]";
        return result;
    }
}
