package ASTStructure;

import java.util.List;

public class TreeNode {
    public TreeValue val;
    public List<TreeNode> childs ;
    public TreeNode(TreeValue result) {
        this.val = result;
    }
}
