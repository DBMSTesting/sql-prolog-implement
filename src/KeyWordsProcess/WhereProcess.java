package KeyWordsProcess;

import java.util.List;

public class WhereProcess {
    public static TableStructure whereBeforProcess(String tempResult,String strColumn){
        tempResult=tempResult.substring(2,tempResult.length()-2);
        String []items=tempResult.split("],\\[",2);
        strColumn = "[" + items[0] + "]";
        TableStructure tableStructure = new TableStructure();
        tableStructure.columnList = strColumn;
        if(items.length>1) {
            String[] item = items[1].split("],\\[");
            tableStructure.dataList = item;

        }else{
            tableStructure.dataList = null;

        }
        return tableStructure;
    }

    public static String whereAfterProcess(List<String>listdata,String strColumn){
        String strTemp="["+strColumn;
        for (String listdatum : listdata) {
            strTemp += ("," + "[" + listdatum + "]");
        }
        strTemp +="]";
        return strTemp;
    }
    public static String whereProcess(List<ASTStructure.TreeNode> childs, String tag){
        return tag;
    }
}
