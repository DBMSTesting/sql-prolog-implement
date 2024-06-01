package java_prolog;

import java.io.*;
import java.util.*;

//import com.sun.org.apache.xalan.internal.xslt.Process;
import ASTStructure.TreeValue;
import AggregationProcess.*;
import BitProcess.BitNotProcess;
import BitProcess.BitTwoParaProcess;
import CornerfunctionProcess.*;
import JoinProcess.JoinProcess;
import JoinProcess.LeftJoinProcess;
import JoinProcess.RightJoinProcess;
import KeyWordsProcess.*;
import LogicProcess.*;

public class ScriptPrologCommandOrLogic {
    public static String prologCommand = "/opt/local/bin/swipl";
    public static String prologMainFile = "/Users/tashou/STEST/src/prolog";
    //public static String testJsonSql= "{\"prologKeyword_1\": \"FROM\", \"prologParameter_1\": \"t1\", \"prologKeyword_2\": \"WHERE\", \"prologParameter_2\": \"(((+ (t1.a))) is true) is not null \", \"prologKeyword_3\": \"SELECT\", \"prologParameter_3\": \"t1.aASref0\", \"num\": 3}";


//    public static String runScript(String script,String content) throws IOException {
//        //String input = resultProlog(script);
//        JSONObject json = JSONObject.fromObject(script);
//        String Number=json.getString("num");
//        System.out.println("命令数量："+Number);
//        Process p;
//        String ret ="";
//        String tempResult="";
//        String parameter="";
//        String command="";
//
//        for(int i=0;i<Integer.parseInt(Number);i++){
//            try {
//                parameter=json.getString("prologParameter_"+String.valueOf(i+1));
//                //System.out.println("prologParameter_"+String.valueOf(i+1));
//                command=json.getString("prologKeyword_"+String.valueOf(i+1));
//                //System.out.println("prologKeyword_"+String.valueOf(i+1));
//                //System.out.println(prologCommand +" "+"-f"+" "+ prologMainFile+" "+"<"+" "+prologMainFile+" "+">"+" "+"output.txt");
//                switch (command){
//                    case "FROM":
//                        //System.out.println("yyyyyy"+tempResult);
//
//                        p = Runtime.getRuntime().exec(prologCommand);
//                        OutputStream out = p.getOutputStream ();
//                        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//
//                        tempResult=from_function(out,tempResult,parameter,in,content);
//
//                        tempResult=tempResult.substring(2,tempResult.length()-3);
//
//                        String []itemsArray = new String[2];
//                        tempResult = tempResult.replace(" ","");
//
//                        String []items=tempResult.split("]],\\[");
//                        List<List<String>> strings = new ArrayList<>();
//                        String strColumn="[";
//                        for(int j = 0;j<items.length;j++){
//                            itemsArray=items[j].split(",\\[",2);
//
//                            String []itemTemp=itemsArray[1].split("],\\[",2);
//                            String []column=itemTemp[0].split(",");
//                            for(int k=0;k<column.length;k++){
//                                strColumn+=(itemsArray[0]+"^"+column[k]+",");
//                            }
//                            String []item = itemTemp[1].split("],\\[");
//
//                            strings.add(new ArrayList(Arrays.asList(item)));
//                            //map.put(itemsArray[0],item);
//                        }
//                        strColumn = strColumn.substring(0,strColumn.length()-1);
//                        strColumn += "]";
//
//                        List<String> list = test(strings, strings.get(0), "", new ArrayList<>());
//
//
//                        String fromresult="["+strColumn;
//                        for(int q=0;q<list.size();q++){
//                            fromresult+=(","+"["+list.get(q)+"]");
//                        }
//                        fromresult +="]";
//                        tempResult = fromresult;
//                        System.out.println("FROM结果："+tempResult);
//                        break;
//                    case "ORDER BY":
//                        p = Runtime.getRuntime().exec(prologCommand);
//                        OutputStream out4 = p.getOutputStream ();
//                        BufferedReader in4 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//                        parameter = parameter.replace(".","^");
//                        tempResult=orderby_function(out4,tempResult,parameter,in4);
//                        System.out.println("ORDER BY结果："+tempResult);
//                        break;
//                    case "WHERE":
//                        tempResult= ConditionScripts.mainResult(tempResult,parameter);
//                        System.out.println("WHERE结果："+tempResult);
//                        break;
//                    case "HAVING":
//                        tempResult= ConditionScripts.mainResult(tempResult,parameter);
//                        System.out.println("HAVING结果："+tempResult);
//                        break;
//                    case "MAX":
//                        tempResult= AggregationScripts.mainResult(tempResult,parameter,"MAX");
//                        System.out.println("MAX结果："+tempResult);
//                        break;
//                    case "MIN":
//                        tempResult= AggregationScripts.mainResult(tempResult,parameter,"MIN");
//                        System.out.println("MIN结果："+tempResult);
//                        break;
//                    case "SUM":
//                        tempResult= AggregationScripts.mainResult(tempResult,parameter,"SUM");
//                        System.out.println("SUM结果："+tempResult);
//                        break;
//                    case "COUNT":
//                        tempResult= AggregationScripts.mainResult(tempResult,parameter,"COUNT");
//                        System.out.println("COUNT结果："+tempResult);
//                        break;
//                    case "AS":
//                        p = Runtime.getRuntime().exec(prologCommand);
//                        OutputStream out5 = p.getOutputStream ();
//                        BufferedReader in5 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//                        tempResult=as_function(out5,tempResult,parameter,in5);
//                        System.out.println("AS结果："+tempResult);
//                        break;
//                    case "SELECT":
//                        int maxFlag = 0;
//                        int minFlag = 0;
//                        int sumFlag = 0;
//                        int countFlag = 0;
//                        int distinctFlag = 0;
//                        if(parameter.equals("")){
//                            break;
//                        }
//                        if(parameter.contains("MAX")){
//                            parameter.replace("MAX(","");
//                            parameter.replace(")","");
//                            maxFlag = 1;
//                        }else if(parameter.contains("MIN")){
//                            parameter.replace("MIN(","");
//                            parameter.replace(")","");
//                            minFlag = 1;
//                        }
//                        else if(parameter.contains("SUM")){
//                            parameter.replace("SUM(","");
//                            parameter.replace(")","");
//                            sumFlag = 1;
//                        }else if(parameter.contains("COUNT")){
//                            parameter.replace("COUNT(","");
//                            parameter.replace(")","");
//                            countFlag = 1;
//                        }else if(parameter.contains("DISTINCT")){
//                            parameter.replace("DISTINCT","");
//                            distinctFlag = 1;
//                        }
//                        String temp="[";
//
//                        String astemp="[";
//                        String array[]=parameter.split(",");
//                        //System.out.println(parameter);
//                        //System.out.println(array);
//                        for(String value:array){
//                            //System.out.println(value);
//                            String valueas[] = value.split("AS");
//                            //String valuex[]=valueas[0].split("\\.");
//
//                            temp += valueas[0];
//                            temp=temp.replace(".","^");
//                            temp += ",";
//
//                            astemp += valueas[1];
//                            astemp += ",";
//                        }
//                        String str=temp.substring(0,temp.length()-1);
//                        str+="]";
//
//                        String stras=astemp.substring(0,astemp.length()-1);
//                        stras+="]";
//
//                        p = Runtime.getRuntime().exec(prologCommand);
//                        OutputStream out2 = p.getOutputStream ();
//                        BufferedReader in2 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//                        tempResult=select_function(out2,tempResult,str,in2);
//                        p = Runtime.getRuntime().exec(prologCommand);
//                        OutputStream out7 = p.getOutputStream ();
//                        BufferedReader in7 = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//                        tempResult=as_function(out7,tempResult,stras,in7);
//
//                        if(maxFlag==1){
//                            tempResult= AggregationScripts.mainResult(tempResult,stras,"MAX");
//                            System.out.println("MAX结果："+tempResult);
//                        }else if(minFlag==1){
//                            tempResult= AggregationScripts.mainResult(tempResult,stras,"MIN");
//                            System.out.println("MIN结果："+tempResult);
//                        }
//                        else if(sumFlag==1){
//                            tempResult= AggregationScripts.mainResult(tempResult,stras,"SUM");
//                            System.out.println("SUM结果："+tempResult);
//                        }
//                        else if(countFlag==1){
//                            tempResult= AggregationScripts.mainResult(tempResult,stras,"COUNT");
//                            System.out.println("COUNT结果："+tempResult);
//                        }
//
//                        System.out.println("SELECT结果："+tempResult);
//
//                        break;
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        ret=tempResult;
//        System.out.println("最终结果："+ret);
//        return ret;
//    }


    public static String getUnionResult(String file) throws IOException {
        String fileName = file;
        File file1 = new File(fileName);
        FileReader fr1 = new FileReader(file1);
        BufferedReader br1 = new BufferedReader(fr1);
        String line1;
        String ret="";
        while((line1 = br1.readLine()) != null ){
            ret = line1;
        }
        System.out.println(ret);
        ret = ret.replace(", ",",");
        return ret;
    }

//    public static String orderby_function(OutputStream out,String r,String parameter,BufferedReader in) throws IOException {
//        String []order = parameter.split(" ");
//
//        if(order[order.length-1].equals("ASC")){
//            out.write(("['"+prologMainFile+"/Group_operation/orderby.pl'].\n").getBytes());
//            out.write(("orderby_clause("+r+","+order[0]+","+"asc,Z).\n").getBytes());
//        }else if(order[order.length-1].equals("DESC")){
//            out.write(("['"+prologMainFile+"/Group_operation/orderby.pl'].\n").getBytes());
//            out.write(("orderby_clause("+r+","+order[0]+","+"desc,Z).\n").getBytes());
//        }else{
//            out.write(("['"+prologMainFile+"/Group_operation/orderby.pl'].\n").getBytes());
//            out.write(("orderby_clause("+r+","+order[0]+","+"asc,Z).\n").getBytes());
//        }
//        out.flush();
//        out.close();
//        String line;
//        while((line = in.readLine()) != null){
//            r += (line+" ");
//        }
//
//        String result="";
//        result=getStr(r);
//        //System.out.println(result);
//        return result;
//    }
//    public static String as_function(OutputStream out,String r,String parameter,BufferedReader in) throws IOException {
//
//        out.write(("['"+prologMainFile+"/Query_Specification/as.pl'].\n").getBytes());
//        out.write(("as_clause("+r+","+parameter+",Z).\n").getBytes());
//
//        out.flush();
//        out.close();
//        String line;
//        while((line = in.readLine()) != null){
//            r += (line+" ");
//        }
//
//        String result="";
//        result=getStr(r);
//        //System.out.println(result);
//        return result;
//    }

    public static String union_function(String tempResult, String result, String union_type, OutputStream out,BufferedReader in) throws IOException {
        if(union_type.equals("unionall")){
            System.out.println("unionall_clause("+tempResult+","+result+",Z).");
            out.write(("['"+prologMainFile+"/Union_operation/unionall.pl'].\n").getBytes());
            out.write(("unionall("+tempResult+","+result+",Z).\n").getBytes());
        }else if(union_type.equals("intersectall")){
            out.write(("['"+prologMainFile+"/Union_operation/intersectall.pl'].\n").getBytes());
            out.write(("intersectall_clause("+tempResult+","+result+",Z).\n").getBytes());
        }else if(union_type.equals("union")){
            out.write(("['"+prologMainFile+"/Union_operation/union.pl'].\n").getBytes());
            out.write(("union_clause("+tempResult+","+result+",Z).\n").getBytes());
        }else if(union_type.equals("intersect")){
            out.write(("['"+prologMainFile+"/Union_operation/intersect.pl'].\n").getBytes());
            out.write(("intersect_clause("+tempResult+","+result+",Z).\n").getBytes());
        }
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            tempResult += (line+" ");
            //System.out.println(line);
        }
        String result1="";
        //result1=getStr(tempResult);
        result1=getUnionResult("/Users/tashou/sql-prolog/unionall_result.txt");
        //System.out.println("333333333"+result1);
        return result1;
    }
//    public static String getStr(String str){
//
//        String []array=str.split("\\s+");
//
//        int startIndex=0;
//        int endIndex=0;
//        for(int i=0;i<array.length;i++){
//
//            if(array[i].equals("Z")){
//                if(array[i+1].equals("=")){
//                    startIndex=(i+2);
//
//                }
//            }
//            if(array[i].equals("EOF:")){
//                endIndex=(i-1);
//
//                break;
//            }
//        }
//        String newStr="";
//
//        for(int j=startIndex;j<(endIndex+1);j++){
//            newStr+=array[j];
//        }
//
//        return newStr;
//    }
//    public static String datetime(String str){
//        String strTemp = "";
//        for(int i=0;i<str.length();i++){
//            if(i + 8<str.length()){
//                if(str.substring(i,i+8).equals("datetime")){
//                    for(int j=i;j<str.length();j++){
//                        if(str.substring(j,j+1).equals(")")){
//                            String newStr = (str.substring(i,j+1).replace(", ","^"));
//                            newStr = (newStr.replace(".","^"));
////                            newStr = (newStr.replace("(","{"));
////                            newStr = (newStr.replace(")","}"));
//                            strTemp+=newStr;
//                            i = j;
//                            break;
//                        }
//                    }
//                }else{
//                    strTemp += str.substring(i,i+1);
//                }
//            }else{
//                strTemp += str.substring(i,str.length());
//                break;
//            }
//        }
//        return strTemp;
//    }
//    public static String mysqlPrologResult(String testJsonSql,String content) throws IOException {
//        Process p;
//        JSONObject json = JSONObject.fromObject(testJsonSql);
//        String result1 = "";
//        if(json.containsKey("union_num")){
//            if(json.containsKey("query_1")){
//                result1 = runScript(json.getString("query_1"),content);
//            }
//            String tempResult = result1;
//            String result2 = "";
//            for(int i = 0 ; i < Integer.parseInt(json.getString("union_num")); i++){
//                result2 = runScript(json.getString("query_"+(i+2)),content);
//                //System.out.println("22222222"+result2);
//                p = Runtime.getRuntime().exec(prologCommand);
//                OutputStream out = p.getOutputStream ();
//                BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//                tempResult = union_function(tempResult,result2,json.getString("union_"+(i+1)),out,in);
//            }
//            //System.out.println("11111"+tempResult);
//            return tempResult;
//        }else{
//            //runScript("{\"prologKeyword_1\": \"FROM\", \"prologParameter_1\":\"t1\",\"prologKeyword_2\": \"WHERE\",\"prologParameter_2\":\"t1.b < 9 and 1+1>0 and 1 in (1,2,3)and (t1.a>0 or (t1.c<9)) and t1.c is not null\",\"prologKeyword_3\": \"SELECT\", \"prologParameter_3\":\"\",\"prologKeyword_4\": \"MAX\", \"prologParameter_4\":\"(t1^a)\",\"num\":\"4\"}");
//            //runScript("{\"prologKeyword_1\": \"FROM\", \"prologParameter_1\": \"t1\", \"prologKeyword_2\": \"WHERE\", \"prologParameter_2\": \"(((+ (t1.a))) is true) is not null \", \"prologKeyword_3\": \"SELECT\", \"prologParameter_3\": \"t1^aASref0\", \"num\": 3}");
//
//            String result = runScript(testJsonSql,content);
//            return result;
//        }
//    }

    public static String chooseKeywords(String keyword,List<ASTStructure.TreeNode> childs,String keywordResult) throws IOException {
        switch (keyword){
            case "from":
                //return "from process";
                return FromProcess.fromProcess(childs,keywordResult);
            case "select":
                //return "select process";
                return SelectProcess.selectProcess(childs,keywordResult);
            case "orderby":
                //return "orderby process";
                return OrderbyProcess.orderbyProcess(childs,keywordResult);
            case "limit":
                //return "orderby process";
                return LimitProcess.limitProcess(childs,keywordResult);
            case "max":
                //return "max process";
                return MaxProcess.maxProcess(childs,keywordResult);
            case "min":
                //return "min process";
                return MinProcess.minProcess(childs,keywordResult);
            case "sum":
                //return "sum process";
                return SumProcess.sumProcess(childs,keywordResult);
            case "count":
                //return "count process";
                return CountProcess.countProcess(childs,keywordResult);
            case "distinct":
                //return "count process";
                return DistinctProcess.distinctProcess(childs,keywordResult);
            case "value":
                //return "value process";
                return ValueProcess.valueProcess(childs);
            case "left join": case "left outer join":
                return LeftJoinProcess.leftjoinProcess(keywordResult);
            case "right join": case "right outer join":
                return RightJoinProcess.rightjoinProcess(keywordResult);
            case "join": case "inner join":
                return JoinProcess.joinProcess(keywordResult);
        }
        return "关键字超出范围";
    }
    public static String chooseJoin(String keyword,String keywordResult) throws IOException {
        switch (keyword){
            case "left join": case "left outer join":
                return LeftJoinProcess.leftjoinProcess(keywordResult);
            case "right join": case "right outer join":
                return RightJoinProcess.rightjoinProcess(keywordResult);
            case "join": case "inner join":
                return JoinProcess.joinProcess(keywordResult);
        }
        return "关键字超出范围";
    }
    public static List<String> LogicOneParameter = new ArrayList<String>(){{
        add("upper");
        add("lcase");
        add("ucase");
        add("lower");
        add("ascii");
        add("char_length");
        add("character_length");
        add("length");
        add("reverse");
        add("char");
        add("avg");
        add("sign");
        add("square");
        add("bit_length");
        add("ltrim");
        add("rtrim");
        add("space");
    }};
    public static List<String> LogicTwoParameter = new ArrayList<String>(){{
        add("left");
        add("right");
        add("strcmp");
        add("repeat");
        add("position");
        add("substring");
        add("power");
        add("coalesce");
        add("format");
        add("instr");
        add("locate");
        add("ltrim_para");
        add("nullif");
        add("rtrim_para");
    }};
    public static List<String> LogicThreeParameter = new ArrayList<String>(){{
        add("lpad");
        add("rpad");
        add("replace");
        add("substring_index");
        add("case");
        add("if");
        add("insert");
    }};
    public static List<String> LogicListParameter = new ArrayList<String>(){{
        add("concat");
        add("concat_ws");
        add("elt");
    }};
    public static List<String> LogicListSpcParameter = new ArrayList<String>(){{
        add("find_in_set");
        add("field");
    }};
    public static String chooseLogic(String keyword, List<ASTStructure.TreeNode> childs, TreeValue treeValue) throws IOException {
        switch (keyword){
            case "where" : case "having": case "c_on":
                //return "where process";
                return WhereProcess.whereProcess(childs,treeValue.tag);
            case "=":
                //return "eq process";
                //System.out.println("equal process");
                return EqualProcess.equalProcess(childs,treeValue);
            case "<=":
                //return "lte process";
                return LessOrEqualProcess.lessOrEqualProcess(childs,treeValue);
            case ">=":
                //return "gte process";
                return GreaterOrEqualProcess.greaterOrEqualProcess(childs,treeValue);
            case ">":
                //return "gt process";
                return GreaterProcess.greaterProcess(childs,treeValue);
            case "<":
                //return "lt process";
                return LessProcess.lessProcess(childs,treeValue);
            case "AND":
                //return "and process";
                return AndProcess.andProcess(childs,treeValue);
            case "OR":
                //return "or process";
                return OrProcess.orProcess(childs,treeValue);
            case "concat-ws":
                //return "in process";
                return ConcatwsFunction.concatwsProcess(childs,treeValue);
            case "elt":
                //return "in process";
                return EltProcess.eltProcess(childs,treeValue);
            case "field":
                //return "in process";
                return FieldProcess.fieldProcess(childs,treeValue);
            case "in":
                //return "in process";
                return InProcess.inProcess(childs,treeValue);
            case "exists":
                //return "exists process";
                return ExistsProcess.existsProcess(childs,treeValue);
            case "!=":
                //return "neq process";
                return NotEqualProcess.notEqualProcess(childs,treeValue);
            case "not":
                //return "not process";
                return NotProcess.notProcess(childs,treeValue);
            case "&": case "|": case "^": case "<<": case ">>":
                return BitTwoParaProcess.bitTwoProcess(keyword,childs,treeValue);
            case "+": case "-": case "*": case "/":
                return CaculateProcess.caculateProcess(keyword,childs,treeValue);
            case "!":
                return BitNotProcess.bitNotProcess(keyword,childs,treeValue);
        }
        if(LogicOneParameter.contains(keyword) && childs.size()==1){
            return FunctionOneParProcess.functionProcess(keyword,childs,treeValue);
        }else if(LogicThreeParameter.contains(keyword) && childs.size()==3){
            return FunctionThreeParProcess.functionProcess(keyword,childs,treeValue);
        }else if(LogicTwoParameter.contains(keyword) && childs.size()==2){
            return FunctionTwoParProcess.functionProcess(keyword,childs,treeValue);
        }else if(LogicListParameter.contains(keyword)){
            return FunctionListProcess.functionProcess(keyword,childs,treeValue);
        }else if(keyword.equals("ltrim") && childs.size()==3){
            return FunctionThreeParProcess.functionProcess("ltrim_para",childs,treeValue);
        }else if(keyword.equals("rtrim") && childs.size()==3){
            return FunctionThreeParProcess.functionProcess("rtrim_para",childs,treeValue);
        }

        return "操作符超出范围";
    }

}
