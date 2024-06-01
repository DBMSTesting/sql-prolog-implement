package KeyWordsProcess;

import java.util.List;

public class ParameterProcess {
    public static String parameterProcess(List<ASTStructure.TreeNode> childs){
        String parameterTemp = "[";
        String transform = "[";
        for(ASTStructure.TreeNode child:childs){
            transform = child.val.keywordResult.replace(".","^");
            parameterTemp += transform+",";
        }
        parameterTemp = parameterTemp.substring(0,parameterTemp.length()-1) + "]";
        return parameterTemp;
    }
}
