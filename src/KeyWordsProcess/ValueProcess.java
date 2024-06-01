package KeyWordsProcess;

import ASTStructure.TreeNode;

import java.util.List;

public class ValueProcess {
    public static String valueProcess(List<TreeNode> childs){
        if(childs.size()==1){
            return childs.get(0).val.keywordResult;
        }else{
            return "树构建错误";
        }
    }
}
