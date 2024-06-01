package sqlancer.common.ast.newast;

import sqlancer.common.ast.BinaryOperatorNode.Operator;

public class NewBinaryOperatorNode<T> implements Node<T> {

    protected final Operator op;
    protected final Node<T> left;
    protected final Node<T> right;
    protected final String subquery;

    public NewBinaryOperatorNode(Node<T> left, Node<T> right, Operator op,String subquery) {
        this.left = left;
        this.right = right;
        this.op = op;
        this.subquery = subquery;
    }

    public String getOperatorRepresentation() {
        return op.getTextRepresentation();
    }

    public Node<T> getLeft() {
        return left;
    }

    public Node<T> getRight() {
        return right;
    }

    public String getSubquery() {return subquery;}

}
