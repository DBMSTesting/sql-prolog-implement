package java_prolog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Pattern;


public class ConditionScripts {

    public static String []LevelOpts =new String[9];
    public static Map<String,Integer> optLevel = new HashMap<>();
    public static Set<String> optSet = new HashSet<>();
    public static Pattern pattern = Pattern.compile("^[-+]?[0-9]+(\\.[0-9]+)?$");
    public static Pattern SCIENTIFIC=Pattern.compile("^([\\+|-]?\\d+(.{0}|.\\d+))[Ee]{1}([\\+|-]?\\d+)$");
    public static Pattern patternstr = Pattern.compile("r\"'(.+?)'\"");
    public static Process p;
    public static String prologCommand = "/opt/local/bin/swipl";
    public static String prologMainFile = "/Users/tashou/sql-prolog/src/prolog";
    public static int flag = 0;
    public static String solve(String tempResult, String input, String column) throws IOException {
        set_construct(optSet);
        map_construct(optLevel);
        Stack<String> optStack = new Stack<>();
        Stack<String> numStack = new Stack<>();
        String []item=input.split("\\s+");
        for(String str:item){
            System.out.print(str+" ");
        }
        //System.out.println(item);
        for(int i=0;i<item.length;){
            System.out.println(item[i]);
            if(i==0 && item[0].equals("")){
                i++;
                continue;
            }
            if(optSet.contains(item[i]) && (!item[i].equals("not") && !item[i].equals("!") && !item[i].equals("exists") && !item[i].equals("NOT"))){
//            if(item[i].equals("and") || item[i].equals("or")|| item[i].equals(">")|| item[i].equals("<")|| item[i].equals("=")|| item[i].equals("in")|| item[i].equals("(") || item[i].equals(")")){
                // 如果是操作符
                //栈为空 或 当前优先于栈顶: 入栈
                if (optStack.isEmpty() || compPriority(item[i], optStack.peek()) < 0) {
                    optStack.push(item[i]);
                    i++;
                } else if (optStack.peek().equals("(")) { // 栈顶为左括号 '('
                    if (item[i].equals(")")) { // 括号完成: 弹出'('
                        optStack.pop();
                    } else { // 括号开始 : 入栈
                        optStack.push(item[i]);
                    }
                    i++;
                } else { // 栈顶优先级更高且非 '(' : 运算
                    String top = numStack.pop();
                    if(optStack.peek().equals("not") || optStack.peek().equals("!") || optStack.peek().equals("exists") || optStack.peek().equals("NOT") ){
                        numStack.push(calc_single(optStack.pop(), top,tempResult,column));
                    }else{
                        //String top = numStack.pop();
                        if(numStack.isEmpty()){
                            if(optStack.peek().equals("+") || optStack.peek().equals("-")){
                                String x = "0";
                                numStack.push(calc(x, optStack.pop(), top,tempResult,column));
                            }
                        }else{
                            numStack.push(calc(numStack.pop(), optStack.pop(), top,tempResult,column));
                        }
                    }
                }
            }else if(item[i].equals("not") || item[i].equals("!") || item[i].equals("exists") || item[i].equals("NOT") ){
                if (optStack.isEmpty() || compPriority(item[i], optStack.peek()) > 0) {
                    //System.out.println("入站1："+item[i]);
                    optStack.push(item[i]);
                    i++;
                } else if (optStack.peek().equals("(")) { // 栈顶为左括号 '('
                    if (item[i].equals(")")) { // 括号完成: 弹出'('
                        //System.out.println("出站2："+optStack.peek());
                        optStack.pop();
                    } else { // 括号开始 : 入栈
                        //System.out.println("入站2："+item[i]);
                        optStack.push(item[i]);
                    }
                    i++;
                } else { // 栈顶优先级更高且非 '(' : 运算
                    String top = numStack.pop();
                    numStack.push(calc_single(optStack.pop(), top,tempResult,column));
                }
            }
            else{
                if(item[i].equals("NULL") || item[i].equals("UNKNOWN")){
                    numStack.push(item[i].toLowerCase());
                }else{
                    numStack.push(item[i]);
                }
                i++;
            }
        }
        while (!optStack.isEmpty()) {
            if(optStack.peek().equals("not") || optStack.peek().equals("!") || optStack.peek().equals("exists") || optStack.peek().equals("NOT")){
                String top = numStack.pop();
                numStack.push(calc_single(optStack.pop(), top,tempResult,column));
            }else{
                String top = numStack.pop();
                if(numStack.empty()){
                    int num = 0;
                    if(optStack.peek().equals("+") || optStack.peek().equals("-")){
                        numStack.push(calc("0", optStack.pop(), top,tempResult,column));
                    }
                }else{
                    numStack.push(calc(numStack.pop(), optStack.pop(), top,tempResult,column));
                }
            }
        }
        String x = numStack.peek();
        if(!pattern.matcher(x).matches()&& !(x.equals("true")) && !(x.equals("false"))&& !(x.equals("null")) && !(x.equals("notnull"))){
            x=dataChange(x,tempResult,column,0);
        }
        if(!(x.equals("true")) && !(x.equals("false"))&& !(x.equals("null"))&& !(x.equals("notnull"))){
            if((x.equals("0"))){
                x= "false";
            }else if(pattern.matcher(x).matches()){
                x="true";
            }else{
                x= "false";
            }
        }
        return x;
    }
    private static int compPriority(String c1, String c2) {
        return optLevel.get(c1)-optLevel.get(c2);
    }
    private static String calc_single(String x, String o,String tempResult,String column) throws IOException {
        switch (x) {
            case "not": case "!": case "NOT":
                return not(o,tempResult,column);
            case "exists":case "EXISTS":
                return exists(o,tempResult,column);
        }
        return "0";
    }
    private static String calc(String x, String o, String y,String tempResult,String column) throws IOException {
        //System.out.println(o);
        switch (o) {
            case "and": case "&&":case "&":case "AND":
                return and(x,y,tempResult,column);
            case "or": case "||":case "|":case "OR":
                return or(x,y,tempResult,column);
            case "xor":case "XOR":
                return xor(x,y,tempResult,column);
            case ">":
                return bigger(x,y,tempResult,column);
            case "<":
                return less(x,y,tempResult,column);
            case ">=":
                return bigequal(x,y,tempResult,column);
            case "<=":
                return lessequal(x,y,tempResult,column);
            case "=":
                return equal(x,y,tempResult,column);
            case "!=":
                return notequal(x,y,tempResult,column);
            case "in":case "IN":
                return in(x,y,tempResult,column);
            case "is":case "IS":
                return is(x,y,tempResult,column);
            case "+":case "-":case "*":case "/":
                return numericaloperation(o,x,y,tempResult,column);

        }
        return "0";
    }
    public static String dataChange(String x,String tempResult,String column,int flag){
        if(!pattern.matcher(x).matches() ){
            if(x.contains(".") && !(x.substring(0,1).equals("'") && x.substring(x.length()-1,x.length()).equals("'"))){
                x=stringToData(x,tempResult,column);
            }
            if((x.equals("true"))||(x.equals("TRUE"))){
                x = "1";
            }else if((x.equals("false"))||(x.equals("FALSE"))){
                x = "0";
            }else if((x.equals("null"))||(x.equals("NULL"))){
                x = "null";
            }else if((x.equals("notnull"))||(x.equals("NOTNULL"))){
                x = "notnull";
            }else if((x.equals("unknown"))||(x.equals("UNKNOWN"))){
                x = "unknown";
            }else if(x.substring(0,1).equals("'") && x.substring(x.length()-1,x.length()).equals("'")){
                if(pattern.matcher(x.substring(1,x.length()-1)).matches()){
                    x = x.substring(1,x.length()-1);
                }else if(x.length()>4 && x.substring(1,3).equals("\n")){
                    x = x.substring(3,x.length()-1);
                }else{
                    x = "0";
                }
            }else if(pattern.matcher(x).matches()){
                x =x;
            }else{
                x = "0";
                //flag = 1;
            }
        }return x;
    }
    public static String stringToData(String x,String tempResult,String column){
        //System.out.println(x);
        x = x.replace(".","^");
        tempResult=tempResult.replaceAll(" ","");
        column=column.replaceAll(" ","");
        column = column.substring(1,column.length()-1);
        String []column_item=column.split(",");
        String []data_item=tempResult.split(",");
        //System.out.println(item[0]);
        int index=0;
        for(int i =0;i<column_item.length;i++){
            if(column_item[i].equals(x)){
                //System.out.println("index:"+x);
                index=i;
            }
        }
        return data_item[index];
    }
    public static String numericaloperation(String o,String x,String y,String tempResult,String column) throws IOException {
        x = dataChange(x,tempResult,column,flag);
        y = dataChange(y,tempResult,column,flag);
        p = Runtime.getRuntime().exec(prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        if(o.equals("+")){
            o = "add";
        }else if(o.equals("-")){
            o = "subtract";
        }else if(o.equals("*")){
            o = "multiply";
        }else if(o.equals("/")){
            o = "divide";
        }
        out.write(("['"+prologMainFile+"/Condition/"+o+".pl'].\n").getBytes());
        out.write((o+"_clause("+x+","+y+",Z).\n").getBytes());
        //System.out.println("select_clause("+r+","+parameter+",Z).\n");
        out.flush();
        out.close();
        String line;
        while((line = in.readLine()) != null){
            tempResult += (line+" ");
        }
        String result="";
        result=getStr(tempResult);
        return result;
    }
    public static boolean isText(String str){
        if(str.substring(0,1).equals("'") && str.substring(str.length()-1,str.length()).equals("'")){
            return true;
        }else{
            return false;
        }
    }
    public static String and(String x,String y,String tempResult,String column) throws IOException {
//        System.out.println("and"+x);
//        System.out.println("and"+y);

        x = dataChange(x,tempResult,column,flag);
        y = dataChange(y,tempResult,column,flag);

        if(flag==0){
//            System.out.println("and"+x);
//            System.out.println("and"+y);
            p = Runtime.getRuntime().exec(prologCommand);
            OutputStream out = p.getOutputStream ();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            out.write(("['"+prologMainFile+"/Condition/and.pl'].\n").getBytes());
            out.write(("and_clause("+x+","+y+",Z).\n").getBytes());
            System.out.println("and_clause("+x+","+y+",Z).");
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
        else{
            flag=0;
            return "false";
        }
    }
    public static String or(String x,String y,String tempResult,String column) throws IOException {
        x = dataChange(x,tempResult,column,flag);
        y = dataChange(y,tempResult,column,flag);
        if(flag == 0){
            p = Runtime.getRuntime().exec(prologCommand);
            OutputStream out = p.getOutputStream ();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            out.write(("['"+prologMainFile+"/Condition/or.pl'].\n").getBytes());
            out.write(("or_clause("+x+","+y+",Z).\n").getBytes());
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
        }else{
            flag=0;
            return "false";
        }
    }
    public static String xor(String x,String y,String tempResult,String column) throws IOException {
        x = dataChange(x,tempResult,column,flag);
        y = dataChange(y,tempResult,column,flag);
        if(flag == 0){
            p = Runtime.getRuntime().exec(prologCommand);
            OutputStream out = p.getOutputStream ();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            out.write(("['"+prologMainFile+"/Condition/xor.pl'].\n").getBytes());
            out.write(("xor_clause("+x+","+y+",Z).\n").getBytes());
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
        }else{
            flag=0;
            return "false";
        }
    }
    public static String bigger(String x,String y,String tempResult,String column) throws IOException {
        System.out.println("bigger"+x);
        System.out.println("bigger"+y);
        if(isText(x) && isText(y)){

        }else if((isText(x)&&!isText(y)||(isText(y)&& !isText(x)))){
            x = dataChange(x,tempResult,column,flag);
            y = dataChange(y,tempResult,column,flag);
        }

        if(flag==0){
            p = Runtime.getRuntime().exec(prologCommand);
            OutputStream out = p.getOutputStream ();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            //System.out.println(x);
            //System.out.println(y);
            out.write(("['"+prologMainFile+"/Condition/bigger.pl'].\n").getBytes());
            out.write(("bigger_clause("+x+","+y+",Z).\n").getBytes());
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
        }else{
            flag=0;
            return "false";
        }
    }
    public static String less(String x,String y,String tempResult,String column) throws IOException {
        System.out.println("less"+x);
        System.out.println("less"+y);
        x = dataChange(x,tempResult,column,flag);
        y = dataChange(y,tempResult,column,flag);
        if(flag==0){
            p = Runtime.getRuntime().exec(prologCommand);
            OutputStream out = p.getOutputStream ();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            out.write(("['"+prologMainFile+"/Condition/less.pl'].\n").getBytes());
            out.write(("less_clause("+x+","+y+",Z).\n").getBytes());
            System.out.println("less_clause("+x+","+y+",Z).");
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
        }else{
            flag=0;
            return "false";
        }
        //System.out.println("2222222");

    }
    public static String bigequal(String x,String y,String tempResult,String column) throws IOException {
        System.out.println("bigequal"+x);
        System.out.println("bigequal"+y);
        x = dataChange(x,tempResult,column,flag);
        y = dataChange(y,tempResult,column,flag);
        if(flag==0){
            p = Runtime.getRuntime().exec(prologCommand);
            OutputStream out = p.getOutputStream ();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            out.write(("['"+prologMainFile+"/Condition/bigequal.pl'].\n").getBytes());
            out.write(("bigequal_clause("+x+","+y+",Z).\n").getBytes());
            System.out.println("bigequal_clause("+x+","+y+",Z).");
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
        }else{
            flag=0;
            return "false";
        }
        //System.out.println("2222222");

    }
    public static String lessequal(String x,String y,String tempResult,String column) throws IOException {
        System.out.println("lessequal"+x);
        System.out.println("lessequal"+y);
        x = dataChange(x,tempResult,column,flag);
        y = dataChange(y,tempResult,column,flag);
        if(flag==0){
            p = Runtime.getRuntime().exec(prologCommand);
            OutputStream out = p.getOutputStream ();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            out.write(("['"+prologMainFile+"/Condition/lessequal.pl'].\n").getBytes());
            out.write(("lessequal_clause("+x+","+y+",Z).\n").getBytes());
            System.out.println("lessequal_clause("+x+","+y+",Z).");
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
        }else{
            flag=0;
            return "false";
        }
        //System.out.println("2222222");

    }
    public static String equal(String x,String y,String tempResult,String column) throws IOException {
        x = dataChange(x,tempResult,column,flag);
        y = dataChange(y,tempResult,column,flag);
//        if(!pattern.matcher(x).matches()||!pattern.matcher(y).matches()){
//            x = stringToData(x);
//            y = stringToData(y);
//        }
        p = Runtime.getRuntime().exec(prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+prologMainFile+"/Condition/equal.pl'].\n").getBytes());
        out.write(("equal_clause("+x+","+y+",Z).\n").getBytes());
        System.out.println("equal_clause("+x+","+y+",Z).");
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
    public static String notequal(String x,String y,String tempResult,String column) throws IOException {
        x = dataChange(x,tempResult,column,flag);
        y = dataChange(y,tempResult,column,flag);
//        if(!pattern.matcher(x).matches()||!pattern.matcher(y).matches()){
//            x = stringToData(x);
//            y = stringToData(y);
//        }
        p = Runtime.getRuntime().exec(prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+prologMainFile+"/Condition/notequal.pl'].\n").getBytes());
        out.write(("notequal_clause("+x+","+y+",Z).\n").getBytes());
        System.out.println("notequal_clause("+x+","+y+",Z).");
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
    public static String in(String x,String y,String tempResult,String column) throws IOException {
        x = dataChange(x,tempResult,column,flag);

        String []items = y.split(",");
        String str = "[";
        for(int i=0;i<items.length;i++){
            if(items[i].contains(".") && !(items[i].substring(0,1).equals("'") && items[i].substring(items[i].length()-1,items[i].length()).equals("'"))){
                str+=(stringToData(items[i],tempResult,column)+",");
                System.out.println(x);
            }else{
                str+=(items[i]+",");
            }
            str+=(dataChange(items[i],tempResult,column,0)+",");
        }
        str=str.substring(0,str.length()-1);
        str+="]";
        p = Runtime.getRuntime().exec(prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+prologMainFile+"/Condition/in.pl'].\n").getBytes());
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
        result=getStr(tempResult);
        //System.out.println(result);
        return result;
    }
    public static String is(String x,String y,String tempResult,String column) throws IOException {
        x = dataChange(x,tempResult,column,flag);
        y = dataChange(y,tempResult,column,flag);
        if(flag == 0){
            p = Runtime.getRuntime().exec(prologCommand);
            OutputStream out = p.getOutputStream ();
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            out.write(("['"+prologMainFile+"/Condition/is.pl'].\n").getBytes());
            out.write(("is_clause("+x+","+y+",Z).\n").getBytes());
            System.out.println("is_clause("+x+","+y+",Z).");
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
        }else{
            flag = 0;
            return "false";
        }
    }
    public static String not(String x,String tempResult,String column) throws IOException {
//        if(!pattern.matcher(x).matches() && !(x.equals("true")) && !(x.equals("false"))&& !(x.equals("null"))){
//            x=stringToData(x,tempResult,column);
//        }
        x = dataChange(x,tempResult,column,flag);
        p = Runtime.getRuntime().exec(prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+prologMainFile+"/Condition/not.pl'].\n").getBytes());
        out.write(("not_clause("+x+",Z).\n").getBytes());
        System.out.println("not_clause("+x+",Z).\n");
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
    public static String exists(String x,String tempResult,String column) throws IOException {
//        if(!pattern.matcher(x).matches() && !(x.equals("true")) && !(x.equals("false"))&& !(x.equals("null"))){
//            x=stringToData(x,tempResult,column);
//        }
        x = dataChange(x,tempResult,column,flag);
        p = Runtime.getRuntime().exec(prologCommand);
        OutputStream out = p.getOutputStream ();
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        out.write(("['"+prologMainFile+"/Condition/exists.pl'].\n").getBytes());
        out.write(("exists_clause("+x+",Z).\n").getBytes());
        System.out.println("exists_clause("+x+",Z).\n");
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
    public static void set_construct(Set<String> set){
        set.add("(");
        set.add(")");
        set.add("*");
        set.add("/");
        set.add("%");
        set.add("+");
        set.add("-");
        set.add("=");
        set.add(">");
        set.add("<");
        set.add(">=");
        set.add("<=");
        set.add("!");
        set.add("!=");
        set.add("not");
        set.add("and");
        set.add("or");
        set.add("between");
        set.add("in");
        set.add("exists");
        set.add("EXISTS");
        set.add("is");
        set.add("NOT");
        set.add("AND");
        set.add("xor");
        set.add("OR");
        set.add("XOR");
        set.add("BETWEEN");
        set.add("IN");
        set.add("IS");
        set.add("!!");
        set.add("&&");
        set.add("&");
        set.add("|");
    }
    public static void map_construct(Map<String,Integer> map){
        map.put("(", 1);
        map.put("!", 2);
        map.put("*", 3);
        map.put("/", 3);
        map.put("%", 3);
        map.put("+", 4);
        map.put("-", 4);
        map.put("&", 4);
        map.put("|", 4);
        map.put("=", 5);
        map.put("!=", 5);
        map.put(">", 5);
        map.put("<", 5);
        map.put(">=", 5);
        map.put("<=", 5);
        map.put("not", 6);
        map.put("between", 7);
        map.put("is",5);
        map.put("in", 5);
        map.put("exists", 5);
        map.put("EXISTS", 5);
        map.put("and", 9);
        map.put("NOT", 6);
        map.put("BETWEEN", 7);
        map.put("IS",5);
        map.put("IN", 5);
        map.put("AND", 9);
        map.put("&&", 9);
        map.put("or", 10);
        map.put("xor", 10);
        map.put("OR", 10);
        map.put("XOR", 10);
        map.put("||", 10);
        map.put(")", 11);
    }
    public static String getSplitStr(String input) {
        String input1=input.replaceAll("\\s*","");
        //System.out.println(input1);
        String input2=input1.replaceAll("(and)", " $1 ");
        //System.out.println(input2);
        String input3=input2.replaceAll("(or)", " $1 ");
        //System.out.println(input3);
        String input5=input3.replaceAll("(>)", " $1 ");
        //System.out.println(input5);
        String input6=input5.replaceAll("(<)", " $1 ");
        //System.out.println(input6);
        String input7=input6.replaceAll("(=)", " $1 ");
        //System.out.println(input7);
        String input8=input7.replaceAll("(in)", " $1 ");
        //System.out.println(input8);
        String input9=input8.replaceAll("(\\()", " $1 ");
        //System.out.println(input9);
        String input10=input9.replaceAll("(\\))", " $1 ");
        //System.out.println(input10);
        String input17=input10.replaceAll("(\\+)", " $1 ");
        //System.out.println(input17);
        String input18=input17.replaceAll("(not)", " $1 ");
        //System.out.println(input18);
        String input19=input18.replaceAll("(is)", " $1 ");
        //System.out.println(input19);
        String input20=input19.replaceAll("(!)", " $1 ");
        //System.out.println(input20);
        String input21=input20.replaceAll("(IS)", " $1 ");
        //System.out.println(input21);
        String input22=input21.replaceAll("(AND)", " $1 ");
        //System.out.println(input22);
        String input23=input22.replaceAll("(OR)", " $1 ");
        //System.out.println(input23);
        String input24=input23.replaceAll("(NOT)", " $1 ");
        //System.out.println(input24);
        String input25=input24.replaceAll("(EX IS TS)", " EXISTS ");
        //System.out.println(input25);
        String input26=input25.replaceAll("(ex is ts)", " exists ");
        //System.out.println(input26);
        String input27=input26.replaceAll("(&)", " $1 ");
        //System.out.println(input27);
        String input28=input27.replaceAll("(&  &)", " && ");
        //System.out.println(input28);
        String input29=input28.replaceAll("(\\|)", " $1 ");
        //System.out.println(input29);
        String input30=input29.replaceAll("(\\|  \\|)", " || ");
        //System.out.println(input30);
        String input31=input30.replaceAll("(>  =)", " >= ");
        //System.out.println(input31);
        String input32=input31.replaceAll("(<  =)", " <= ");
        //System.out.println(input32);
        String input33=input32.replaceAll("(X OR)", " XOR ");
        //System.out.println(input33);
        String input34=input33.replaceAll("(x or)", " xor ");
        //System.out.println(input34);
        String input35=input34.replaceAll("(-)", " $1 ");
        //System.out.println(input35);
        String input36=input35.replaceAll("(!  =)", " != ");
        System.out.println(input36);
        return input36;
    }
    public static String getExists(String parameter){
        String []items = parameter.split("EXISTS");
        String temp = items[0];
        if(items.length>1){
            for(int i = 1;i<items.length;i++){
                String str = items[i];
                String []tempWhere = str.split("wHERE");
                if(tempWhere.length==1){
                    String []strexists = tempWhere[0].split("\\)",2);
                    temp=temp+" true "+strexists[1];
                }else{
                    temp=temp+" exists ( "+tempWhere[1];
                }
            }
        }
        return temp;
    }
    public static String getStringStr(String str){
        //System.out.println(str);
        if(str.equals("''")){
            return str;
        }
        String []items = str.split("'");
        String strTemp = "";

        if(items.length==1){
            return str;
        }else{
            for(int i =0;i<items.length;i++){
                System.out.println();
                if(i%2==1){
                    strTemp+=(" '"+items[i].replaceAll("\\s*","")+"' ");
                }else{
                    strTemp+=items[i];
                }
            }
            return strTemp;
        }
    }
    public static String mainResult(String tempResult,String parameter) throws IOException {
        String strExists = getExists(parameter);
        String str = getSplitStr(strExists);
        str = getStringStr(str);

        List<String> listdata = new ArrayList<>();
        HashSet<String> set = new HashSet<>();
        tempResult=tempResult.substring(2,tempResult.length()-2);
        String []itemsArray = new String[2];
        String []items=tempResult.split("],\\[",2);
        if(items.length>1){
            String []item = items[1].split("],\\[");
            String strColumn="["+items[0]+"]";
            for (String s : item) {
                String x = solve(s, str, strColumn);
                if (x.equals("true")) {
                    listdata.add(s);
                }
            }
            String strTemp="["+strColumn;
            for (String listdatum : listdata) {
                strTemp += ("," + "[" + listdatum + "]");
            }
            strTemp +="]";
            return strTemp;
        }else{
            return "[["+items[0]+"]]";
        }
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

    public static void main(String[] args) throws IOException {
        String str = mainResult("[[t1^c0,t1^c1,t1^c2],[null,0,0],[1,1,2]]","''");
    }
}
