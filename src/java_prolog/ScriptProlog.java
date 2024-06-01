package java_prolog;
import ASTStructure.TreeNode;
import ASTStructure.TreeValue;
import KeyWordsProcess.TableStructure;
import KeyWordsProcess.ValueProcess;
import KeyWordsProcess.WhereProcess;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class ScriptProlog {
    public static String json1 = "{'select'=['*'], 'from'=['t2', 't3']}";
    //public static String json1 ="{\"a\":1,\"b\":{\"b1\":2},\"c\":[1,2,3,4],\"d\":{\"d1\":{\"d1_1\":100,\"d1_2\":101},\"d2\":[\"d2_1\",\"d2_2\"]},\"e\":[{\"e1\":1},{\"e2\":2}]}";

    //public static String json1 = "{'select'=['*'], 'from'=['t2', 't3'], 'where'={'>'=['t2.a', '1']}}";
    public static String tableContent = "[[t2,[a,b,c],[1,2,3],[2,5,6],[4,2,1]],[t3,[a,d,e],[1,4,5],[2,6,7],[3,4,6]]]";
    public static int flag = 0;
    public static int joinFlag = 0;
    public static List<String> JOINLIST = new ArrayList<String>();

    public static String LeftTable;
    public static String RightTable;

    public static int onFlag = 0;


    //参考 后序遍历
    public static List<String> list;
    public static List<String> postorderTree(TreeNode node) {
        //递归
        list = new ArrayList<>();
        //递归根节点
        order(node);
        return list;

    }
    public static List<String> JoinList = new ArrayList<String>(){{
        add("inner join");
        add("left join");
        add("right join");
        add("left outer join");
        add("right outer join");
        add("full join");
        add("natural join");
    }};
    public static void order(TreeNode node)
    {
        if(node == null){
            //如果节点为空，终止递归
            return;
        }
        //遍历该根节点的所有子节点
        for(TreeNode child : node.childs){
            order(child);
        }
        //等到遍历终止时，此时节点的值为底层节点的值
        //将该节点的值直接添加到列表中即可
        list.add(node.val.keywordResult);
    }

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
    public static HashSet<TreeNode> set = new HashSet<>();
    public static void TransformOn(TreeNode tree){

        TreeNode node;
        for(int i = 1;i<tree.childs.size();i++){
            node=tree.childs.get(i);
            if(node.val.keywordResult.equals("c_on") && !set.contains(node)){
                tree.childs.remove(node);
                tree.childs.get(i-1).childs.add(node);
                set.add(node);
            }
            TransformOn(node);
        }
    }
    public static void TransformTree(TreeNode tree){
        TreeNode rightest;
        TreeNode currentNode = tree;
        TreeNode nextNode;
        for(int j = 0;j<tree.childs.size();j++){
            TreeNode node = tree.childs.get(j);
            if(JoinList.contains(node.val.keywordResult)){
                continue;
            }
            if(node.val.keywordResult.equals("from")){
                if(JoinList.contains(node.childs.get(node.childs.size()-1).val.keywordResult)){
                    rightest = node.childs.get(node.childs.size()-1);
                    tree.childs.add(rightest);
                    tree.childs.remove(node);
                    int size = node.childs.size();
                    for(int i=1;i<size;i++){
                        if(!JoinList.contains(node.childs.get(i).val.keywordResult)){
                            continue;
                        }

                        nextNode = node.childs.get(i);
                        //node.childs.remove(node.childs.get(i));
                        TreeValue query = new TreeValue();
                        query.keywordResult = "from";
                        query.tag = "-1";
                        TreeNode newFrom = createTree(query);

                        newFrom.childs.add(node.childs.get(i-1));
                        newFrom.childs.add(nextNode.childs.get(0));

                        nextNode.childs.remove(nextNode.childs.get(0));
                        nextNode.childs.add(newFrom);
                    }
                }

            }
            TransformTree(node);

        }
    }

    //将初始表读入
    public static void TestTreeOutput(TreeNode tree){

        for(TreeNode node:tree.childs){
            System.out.println(tree.val.keywordResult+"的子节点");
            System.out.println(node.val.keywordResult);
            TestTreeOutput(node);
        }
    }

    public static TreeValue  caculateQuery(TreeNode node,String content) throws IOException {
        TreeValue treeValue = new TreeValue();
        treeValue.keywordResult = content;
        treeValue.tag = "-1";
        //String result = "10    0";
        HashMap<Integer,String> keyWordsList = new HashMap<Integer,String>();
        if(node == null){
            return null;
        }else if(node.val.keywordResult.equals("query") || JoinList.contains(node.val.keywordResult)){
            keyWordsList = sortKeywords(node);
            for(int j=1;j<=keyWordsList.size();j++){
                for(TreeNode child : node.childs){
                    System.out.println("结点值第一次执行后被更改的验证："+child.val.keywordResult);
                    if(child.val.keywordResult.equals(keyWordsList.get(j))){
                        if(child.val.keywordResult.equals("where") || child.val.keywordResult.equals("having") || child.val.keywordResult.equals("c_on")){
                            if(child.val.keywordResult.equals("c_on")){
                                onFlag = 1;
                            }
                            List<String> listdata = new ArrayList<>();
                            String strColumn = "";
                            TableStructure preparation = WhereProcess.whereBeforProcess(treeValue.keywordResult,strColumn);
                            for (String s : preparation.dataList){
                                System.out.println("nitiannitiannitian:"+treeValue.keywordResult+"   "+s);
                            }

                            for (String s : preparation.dataList) {
                                TreeValue treeValue1 = new TreeValue();
                                System.out.println("hhhdatalist:");
                                System.out.println(s);
                                treeValue1.keywordResult = s;
                                treeValue1.tag = "-1";
                                treeValue1.parameter = preparation.columnList;
                                //treeValue1.parameter = strColumn;
                                treeValue1 = caculateKeyword(child, treeValue1);

                                //if (treeValue1.tag.equals("true")||treeValue1.tag.equals("1")) {
                                if (!treeValue1.tag.equals("false")&&!treeValue1.tag.equals("0")) {
                                    System.out.println("最终结果添加："+s);
                                    listdata.add(s);
                                }else if(onFlag == 1){
                                    JOINLIST.add(s);
                                }
                            }
                            if(onFlag == 1){
                                onFlag = 0;
                            }
                            System.out.println("最终结果");
                            System.out.println(listdata);
                            treeValue.keywordResult = WhereProcess.whereAfterProcess(listdata,preparation.columnList);
                            treeValue.tag = "-1";
                        }
                        else{
                            treeValue = caculateKeyword(child,treeValue);
                        }

                    }
                }
            }
            return treeValue;
        }else{
            return null;
        }
    }

    public static HashMap<Integer,String> keyWordsOrder = new HashMap<Integer,String>(){{
        put(1,"from");
        put(2,"natural join");
        put(3,"left join");
        put(4,"right join");
        put(5,"left outer join");
        put(6,"right outer join");
        put(7,"full outer join");
        put(8,"full join");

        put(9,"where");
        put(10,"on");
        put(11,"groupby");
        put(12,"having");
        put(13,"select");
        put(14,"orderby");
        put(15,"limit");
    }};
    public static List<String> logicKeyWordsList = new ArrayList<String>(){{
        add("=");
        add("AND");
        add("OR");
        add(">=");
        add("<=");
        add(">");
        add("<");
        add("+");
        add("-");
        add("*");
        add("/");
        add("in");
        add("exists");
        add("xor");
        add("not");
        add("!=");
        add("&");
        add("|");
        add("^");
        add("<<");
        add(">>");
    }};
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
        add("limit");
//        add("value");
    }};
    public static List<String> AggregationKeyWordsList = new ArrayList<String>(){{
        add("max");
        add("min");
        add("sum");
        add("count");
        add("distinct");
    }};
    public static List<String> FunctionList = new ArrayList<String>(){{
        add("concatws");
        add("elt");
        add("field");
        add("ascii");
        add("avg");
        add("bit_length");
        add("case");
        add("char");
        add("char_length");
        add("character_length");
        add("coalesce");
        add("concat");
        add("concat_ws");
        add("find_in_set");
        add("format");
        add("if");
        add("insert");
        add("instr");
        add("lcase");
        add("left");
        add("length");
        add("locate");
        add("lower");
        add("lpad");
        add("ltrim");
        add("nullif");
        add("position");
        add("power");
        add("repeat");
        add("replace");
        add("reverse");
        add("right");
        add("rpad");
        add("rtrim");
        add("sign");
        add("space");
        add("square");
        add("strcmp");
        add("substring");
        add("substring_index");
        add("ucase");
        add("upper");
    }};


    public static HashMap<Integer,String> sortKeywords(TreeNode node){
        HashMap<Integer,String> queryKeyWordsOrder = new HashMap<Integer,String>();
        List<String> keyWords = new ArrayList<>();
        for(TreeNode child: node.childs){
            keyWords.add(child.val.keywordResult);
        }
        int order = 0;
        for(int i = 1;i<=keyWordsOrder.size();i++){
            if(keyWords.contains(keyWordsOrder.get(i))){
                order++;
                queryKeyWordsOrder.put(order,keyWordsOrder.get(i));
            }
        }
        return queryKeyWordsOrder;
    }

    public static TreeValue caculateKeyword(TreeNode node,TreeValue resultTest) throws IOException {
        if(JoinList.contains(node.val.keywordResult)){
            resultTest = caculateQuery(node,tableContent);

            String listJoin = ScriptPrologCommandOrLogic.chooseJoin(node.val.keywordResult,resultTest.keywordResult);

            System.out.println("join判断");
            System.out.println(resultTest.keywordResult);
            System.out.println(resultTest.currentValue);
            System.out.println(resultTest.tag);
            System.out.println(resultTest.parameter);
            node.val.keywordResult = listJoin;
            resultTest.keywordResult=listJoin;
            return resultTest;
        }
        for(TreeNode child : node.childs) {
            if(child.val.keywordResult.equals("query") || JoinList.contains(child.val.keywordResult)){
                String parameter = resultTest.parameter;
                TreeValue resultTestTemp = caculateQuery(child,tableContent);
                resultTest.currentValue = resultTestTemp.keywordResult;
                resultTest.parameter = resultTestTemp.parameter;
                resultTest.tag = resultTestTemp.tag;
                System.out.println(child.val.keywordResult+" "+resultTest.keywordResult);
                String result = "";
                if(node.val.keywordResult.equals("from")){
                    result = resultTest.keywordResult;
                }else if(logicKeyWordsList.contains(node.val.keywordResult)){
                    String subPara = resultTest.currentValue.substring(2,resultTest.currentValue.length()-2);
                    String []subqueryResult = subPara.split("],\\[");
                    result = subqueryResult[subqueryResult.length-1];
                }
                //child.val.keywordResult = result;
                child.val.currentValue = result;
                child.val.parameter = parameter;
                resultTest.parameter=parameter;
                resultTest.currentValue=result;
            }else{
                resultTest = caculateKeyword(child, resultTest);
            }



        }

        if(logicKeyWordsList.contains(node.val.keywordResult) || FunctionList.contains(node.val.keywordResult)){

//            System.out.println("hhhhhhhhhhhhhhhh");
//            System.out.println(node.val.keywordResult);
//            System.out.println(resultTest.keywordResult);
//            System.out.println(resultTest.currentValue);
//            System.out.println(resultTest.tag);
//            System.out.println(resultTest.parameter);
            System.out.println("逻辑运算符"+node.val.keywordResult);
            System.out.println("验证："+resultTest.keywordResult);
            if(node.val.keywordResult.equals(">")){
                System.out.println(node.childs);
                System.out.println(resultTest);
            }else{
                System.out.println(resultTest);
            }
            resultTest = caculateLogic(node.val.keywordResult, node.childs,resultTest);

            resultTest.keywordResult = resultTest.currentValue;
            node.val.currentValue = resultTest.currentValue;
            node.val.tag = resultTest.tag;
            return resultTest;
        }else if(mainKeyWordsList.contains(node.val.keywordResult)){
            resultTest = caculateCommand(node.val.keywordResult, node.childs,resultTest);
            if(node.val.keywordResult.equals("where")||node.val.keywordResult.equals("on")){
                node.val.currentValue = resultTest.currentValue;
                node.val.tag = resultTest.tag;
            }else{
                node.val.parameter = resultTest.parameter;
                node.val.currentValue = resultTest.currentValue;
                node.val.tag = resultTest.tag;
                //node.val = resultTest;
            }
            return resultTest;
        }else if(AggregationKeyWordsList.contains(node.val.keywordResult)){
            resultTest = caculateCommand(node.val.keywordResult, node.childs,resultTest);
            node.val = resultTest;
            return resultTest;
        }else if(node.val.keywordResult.equals("value")){
            node.val.keywordResult = ValueProcess.valueProcess(node.childs);
            return resultTest;
        }else{
            return resultTest;
        }
    }
    public static TreeValue caculateCommand(String keyword, List<TreeNode> childs,TreeValue result) throws IOException {
        if(keyword.equals("where") || keyword.equals("having") || keyword.equals("c_on")){
            TreeValue keyWordResult = new TreeValue();
            keyWordResult.currentValue = result.currentValue;
            keyWordResult.tag = result.tag;
            return keyWordResult;
        }else{
            String listResult = ScriptPrologCommandOrLogic.chooseKeywords(keyword, childs,result.keywordResult);

            TreeValue keyWordResult = new TreeValue();
            keyWordResult.keywordResult = listResult;
            keyWordResult.tag = "-1";
            return keyWordResult;
        }

        //return listResult+"\t"+"-1";
    }
    public static TreeValue caculateLogic(String keyword, List<TreeNode> childs,TreeValue result) throws IOException {

        String tag = ScriptPrologCommandOrLogic.chooseLogic(keyword,childs,result);

        TreeValue keyWordResult = new TreeValue();
        keyWordResult.currentValue = result.keywordResult;
        keyWordResult.tag = tag;
        keyWordResult.parameter = result.parameter;

        return keyWordResult;
    }


    public static String caculateTree(String line,String content) throws IOException {
        tableContent = content;
        JSONObject jsonob = JSONObject.fromObject(line);
        //将json转化为树的形式，方便遍历和计算
        TreeNode tree = TreeConstruct(jsonob);
        if(joinFlag==1){
            TransformTree(tree);
            joinFlag=0;
        }

        String  finalResult = caculateQuery(tree,tableContent).keywordResult;
        return finalResult;
    }
    public static void main(String[] args) throws IOException {

        JSONObject jsonob = JSONObject.fromObject(json1);
        System.out.println("重要输出："+jsonob);
        TreeNode tree = TreeConstruct(jsonob);
        TransformOn(tree);
        TransformTree(tree);
        TestTreeOutput(tree);
        long currentTimeMillis = System.currentTimeMillis();
        System.out.println(currentTimeMillis);
        TreeValue  finalResult = caculateQuery(tree,tableContent);
        System.out.println("最终结果："+finalResult.keywordResult);
        long currentTimeMillis1 = System.currentTimeMillis();
        System.out.println(currentTimeMillis1);
        //1700398871712 170039887 3445 1712
        //1701003275937 1701003275749 188  0.094
    }
}

