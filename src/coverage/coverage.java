package coverage;

import ASTStructure.TreeNode;
import ASTStructure.TreeValue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class coverage {
    public static int joinFlag = 0;
    public static List<List> keywordPatternList;
    public static List<HashMap<String,List>> semanticPatternList = new ArrayList<>();
    public static List<HashMap<String,List>> valuesList = new ArrayList<>();

    public static List<List> keywordList = new ArrayList<>();
    public static HashMap<String,List> valueList;
    public static HashMap<String,Boolean> subqueryList;
    public static HashMap<String,List> expressionList;
    public static String regex = "^-?([1-9]d*.d*|0.d*[1-9]d*|0?.0+|0)$";
    public static String regex1 = "^-?[1-9]d*$";
    public static String SCIENTIFIC="^([\\+|-]?\\d+(.{0}|.\\d+))[Ee]{1}([\\+|-]?\\d+)$";



    public static List<String> mainKeyWordsList = new ArrayList<String>(){{
        add("from");
        add("where");
        add("select");
        add("groupby");
        add("having");
        add("orderby");
        add("inner join");
        add("left join");
        add("right join");
        add("left outer join");
        add("right outer join");
        add("full join");
        add("natural join");
//        add("value");
    }};
    public static List<String> JoinList = new ArrayList<String>(){{
        add("inner join");
        add("left join");
        add("right join");
        add("left outer join");
        add("right outer join");
        add("full join");
        add("natural join");
    }};
    public static List<String> logicKeyWordsList = new ArrayList<String>(){{
        add("eq");
        add("and");
        add("or");
        add("gte");
        add("lte");
        add("gt");
        add("lt");
        add("add");
        add("sub");
        add("mul");
        add("div");
        add("in");
        add("exists");
        add("missing");
        add("xor");
        add("not");
        add("neq");
        add("concatws");
        add("elt");
        add("field");
        add("cast");
        add("&&");
        add("||");
        add("coalesce");
        add("if");
        add("ifnull");
        add("least");
        add("greatest");
        add("literal");
    }};
    public static TreeNode TreeConstruct(Object objJson){
        System.out.println("初始化树");
        //初始节点
        TreeValue query = new TreeValue();
        query.keywordResult = "query";
        query.tag = "-1";
        TreeNode tree = createTree(query);

        jsonToTree(objJson,tree);


        return tree;

    }
    public static TreeNode createTree(TreeValue value){
        TreeNode tree = new TreeNode(value);
        tree.childs = new ArrayList<>();
        return tree;
    }
    public static void jsonToTree(Object objJson,TreeNode node){
        if(objJson instanceof JSONObject){
            JSONObject jsonObject = (JSONObject)objJson;
            Iterator it = jsonObject.keySet().iterator();
            while(it.hasNext()){
                String key = it.next().toString();
                if(JoinList.contains(key)){
                    joinFlag = 1;
                }
                if(!node.val.keywordResult.equals("query") && key.equals("select")){
                    TreeValue query = new TreeValue();
                    query.keywordResult = "query";
                    query.tag = "-1";
                    TreeNode treeNode = createTree(query);
                    node.childs.add(treeNode);
                    node = treeNode;
                }
                TreeValue keyNode = new TreeValue();
                keyNode.keywordResult = key;
                keyNode.tag = "-1";
                TreeNode treeNode = createTree(keyNode);
                node.childs.add(treeNode);
                Object object = jsonObject.get(key);
                if(object instanceof JSONArray){
                    JSONArray objArray = (JSONArray)object;
                    jsonToTree(objArray,treeNode);
                }else if(object instanceof JSONObject) {//如果key中是一个json对象
                    jsonToTree((JSONObject)object,treeNode);
                }else{
                    TreeValue treeValue = new TreeValue();
                    treeValue.keywordResult = object.toString();
                    treeValue.tag = "-1";
                    TreeNode treeNode1 = createTree(treeValue);
                    treeNode.childs.add(treeNode1);
                }
            }
        }else if(objJson instanceof JSONArray){
            JSONArray objArray = (JSONArray)objJson;
            for (int i = 0; i < objArray.size(); i++) {
                //入栈
                jsonToTree(objArray.get(i),node);
            }
        }else{
            TreeValue treeValue = new TreeValue();
            treeValue.keywordResult = objJson.toString();
            treeValue.tag = "-1";
            TreeNode treeNode = createTree(treeValue);
            node.childs.add(treeNode);
        }
    }
    public static void testTree(TreeNode treeNode){
        for(TreeNode child:treeNode.childs){
            System.out.println(child.val.keywordResult);
            testTree(child);
        }
    }
    public static void caculateCov(TreeNode node){
        List<String>keywordlist = new ArrayList<>();
        valueList = new HashMap<>();
        subqueryList = new HashMap<>();
        expressionList =new HashMap<>();
        for(TreeNode child:node.childs){
            if(child.val.keywordResult.equals("select")){
                keywordlist.add("select");
                List<String> values = new ArrayList<>();
                for(TreeNode para:child.childs){
                    if(para.val.keywordResult.equals("query")){
                        subqueryList.put("select",true);
                    }else if(para.val.keywordResult.equals("value")){
                        if(para.childs.size()!=1){
                            continue;
                        }else{
                            values.add(para.childs.get(0).val.keywordResult);
                        }
                    }else if(para.val.keywordResult.contains(".") || para.val.keywordResult.equals("*")){
                        values.add(para.val.keywordResult);
                    }
                }
                if(!values.isEmpty()){
                    valueList.put("select",values);
                }
            }else if(child.val.keywordResult.equals("from")){
                keywordlist.add("from");
                for(TreeNode para:child.childs){
                    if(para.val.keywordResult.equals("query")){
                        subqueryList.put("from",true);
                    }
                }
                JoinCoverage(child);
            }else if(child.val.keywordResult.equals("where")){
                keywordlist.add("where");
                List<String> exps = new ArrayList<>();
                List<String> values = new ArrayList<>();
                WhereCoverage("where",child,exps);

                expressionList.put("where",exps);

            }else if(child.val.keywordResult.equals("order by")){
                keywordlist.add("order by");
                List<String> values = new ArrayList<>();
                for(TreeNode para:child.childs) {
                    if (para.val.keywordResult.equals("query")) {
                        subqueryList.put("order by",true);
                    } else if (para.val.keywordResult.equals("value")) {
                        if(para.childs.size()!=1){
                            continue;
                        }else{
                            values.add(para.childs.get(0).val.keywordResult);
                        }
                    } else if (para.val.keywordResult.contains(".")) {
                        values.add(para.val.keywordResult);
                    } else if(logicKeyWordsList.contains(para.val.keywordResult)){
                        List<String> exps = new ArrayList<>();
                        WhereCoverage("order by",para,exps);
                        expressionList.put("order by",exps);
                    }
                }
                if(!values.isEmpty()){
                    valueList.put("order by",values);
                }

            }else if(child.val.keywordResult.equals("group by")){
                keywordlist.add("group by");
                List<String> values = new ArrayList<>();
                for(TreeNode para:child.childs) {
                    if (para.val.keywordResult.equals("query")) {
                        subqueryList.put("group by",true);
                    } else if (para.val.keywordResult.equals("value")) {
                        if(para.childs.size()!=1){
                            continue;
                        }else{
                            values.add(para.childs.get(0).val.keywordResult);
                        }
                    } else if (para.val.keywordResult.contains(".")) {
                        values.add(para.val.keywordResult);
                    } else if(logicKeyWordsList.contains(para.val.keywordResult)){
                        List<String> exps = new ArrayList<>();
                        WhereCoverage("group by",para,exps);
                        expressionList.put("group by",exps);
                    }
                }
                if(!values.isEmpty()){
                    valueList.put("group by",values);
                }

            }
        }keywordList.add(keywordlist);
        valuesList.add(valueList);
        semanticPatternList.add(expressionList);
    }
    public static void JoinCoverage(TreeNode para){
        for(TreeNode child:para.childs){
            if(JoinList.contains(child.val.keywordResult)){

            }JoinCoverage(child);
        }
    }
    public static void WhereCoverage(String keyword,TreeNode para,List<String> list){
        for(TreeNode child:para.childs){
            if(logicKeyWordsList.contains(child.val.keywordResult)){
                list.add(child.val.keywordResult);
                List<String> values = new ArrayList<>();
                for(TreeNode ch:child.childs){
                    if(ch.val.keywordResult.equals("value")){
                        values.add(ch.childs.get(0).val.keywordResult);

                    }else if(ch.val.keywordResult.equals("query")){
                        subqueryList.put(child.val.keywordResult,true);
                    }else if(!logicKeyWordsList.contains(ch.val.keywordResult)){
                        values.add(ch.val.keywordResult);
                    }
                    if(!values.isEmpty()){
                        valueList.put(child.val.keywordResult,values);
                    }

                }
            }WhereCoverage(child.val.keywordResult,child,list);
        }
    }
    public static void valueCoverage(List<HashMap<String,List>> list) {
        HashMap<String,Integer> values  = new HashMap<>();
        for(HashMap<String,List> map:list){
            for(String str:map.keySet()){
                List<String> li = map.get(str);
                for(String s:li){
                    String pattern="";
                    if(Pattern.matches(regex,s)){
                        pattern = str+"-float";
                    }else if(s.equals("true")||s.equals("false")){
                        pattern = str+"-boolean";
                    }else if(s.equals("UNKNOWN")){

                    }else if(Pattern.matches(regex1,s)){
                        pattern = str+"-Integer";
                    }else if(Pattern.matches(SCIENTIFIC,s)){
                        pattern = str+"-Sentific";
                    }else if(s.contains(".")){
                        pattern = str+"-String";
                    }else{
                        pattern = str+"-TableOrColumn";
                    }
                    if(values.containsKey(pattern)){
                        int t = Integer.parseInt(values.get(pattern).toString());
                        t++;
                        values.remove(pattern);
                        values.put(pattern,Integer.valueOf(t));
                    }else{
                        values.put(pattern,0);
                    }
                }
            }
        }
        for(String p:values.keySet()){
            System.out.println(p);
            System.out.println(values.get(p));
        }
    }
    public static void exprCoverage(List<HashMap<String,List>> list) {
        HashMap<String,Integer> patterns = new HashMap<>();
        for(HashMap<String,List> map:list){
            if(map.containsKey("where")){
                List<String> exps = map.get("where");
                for(int i=0;i<exps.size()-1;i++){
                    for(int j=i+1;j<exps.size();j++){
                        String pattern1 = exps.get(i)+"-"+exps.get(j);
                        String pattern2 = exps.get(j)+"-"+exps.get(i);
                        if(patterns.containsKey(pattern1)){
                            int t = Integer.parseInt(patterns.get(pattern1).toString());
                            t++;
                            patterns.remove(pattern1);
                            patterns.put(pattern1,Integer.valueOf(t));
                        }else if(patterns.containsKey(pattern2)){
                            int t = Integer.parseInt(patterns.get(pattern2).toString());
                            t++;
                            patterns.remove(pattern2);
                            patterns.put(pattern2,Integer.valueOf(t));
                        }else if(!(patterns.containsKey(pattern1)&&patterns.containsKey(pattern2))){
                            patterns.put(pattern1,0);
                        }
                    }
                }
            }

        }
        for(String pattern:patterns.keySet()){
            System.out.println(pattern);
            System.out.println(patterns.get(pattern));
        }

    }
    public static void main(String[] args) throws IOException {
        TreeNode tree = new TreeNode(null);
        for(int i = 0;i<100;i++){
            String fileName = "/Users/tashou/new/mysql_tlp/database"+i+"-new.log";
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                line = line.replace("\'","");
                line = line.replace("\"","\'");
                JSONObject jsonob = JSONObject.fromObject(line);
                tree = TreeConstruct(jsonob);
                testTree(tree);
                caculateCov(tree);
            }
        }

        //System.out.println(semanticPatternList);
        //System.out.println(keywordList);
        System.out.println(valuesList);
        valueCoverage(valuesList);
        //exprCoverage(semanticPatternList);
        //exprCoverage(semanticPatternList);

    }
}
