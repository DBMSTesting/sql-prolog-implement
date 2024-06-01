package sqlancer.common.visitor;

import java.util.List;
import java.util.Random;

import sqlancer.Randomly;
import sqlancer.common.visitor.UnaryOperation.OperatorKind;
//import sqlancer.sqlite3.ast.SQLite3Expression;

public abstract class ToStringVisitor<T> extends NodeVisitor<T> {

    protected final StringBuilder sb = new StringBuilder();
    public String sbwhere = "";
    public String sbSelectSubQuery = "";
    public boolean funcFlag = false;
    public boolean onFlag = false;

//    public void visit(SQLite3Expression.BinaryComparisonOperation op){

//    }

    public void getDataType(){
        Random randomTypes = new Random();
        switch (randomTypes.nextInt(6)){
            case 0: case 2:
                sb.append(Randomly.getIntegerType());
                break;
            case 1:
                sb.append(Randomly.getBigint());
                break;
//            case 2:
//                sb.append(Randomly.getBytea());
//                break;
//            case 3:
//                sb.append(Randomly.getCharacterVarying(9));
//                break;
//            case 4:
//                sb.append(Randomly.geCharacter(9));
//                break;
            case 3:
                sb.append(Randomly.getSmallint());
                break;
            case 4:
                sb.append(Randomly.getReal());
                break;
//            case 7:
//                sb.append(Randomly.getText());
//                break;
//            case 8:
//                sb.append(Randomly.getDate());
//                break;
            case 5:
                sb.append(Randomly.getDoublePrecision());
                break;
//
        }
    }
    public void visit(BinaryOperation<T> op) {

//        sb.append('(');
//        sb.append('(');
//        visit(op.getLeft());
//        sb.append(')');
//        sb.append(op.getOperatorRepresentation());
//        System.out.println(op.getOperatorRepresentation());
//        sb.append('(');
//        visit(op.getRight());
//        sb.append(')');
//        sb.append(')');
        if(op.getOperatorRepresentation().equals("+") || op.getOperatorRepresentation().equals("-")|| op.getOperatorRepresentation().equals("*")|| op.getOperatorRepresentation().equals("/") || op.getOperatorRepresentation().equals("%")){
            sb.append("(");
            getDataType();
            sb.append(") ");
            sb.append(op.getOperatorRepresentation());
            sb.append(" (");
            getDataType();
            sb.append(")");
        }

        if(op.getSubquery()!=null && !onFlag){
            sb.append("(");
            visit(op.getLeft());
            sb.append(") ");
            sb.append(op.getOperatorRepresentation());
            sb.append(" (");
            sb.append(op.getSubquery());
            System.out.println(op.getSubquery());
            sb.append(")");
        }else{
            sb.append("(");
            visit(op.getLeft());
            sb.append(") ");
            sb.append(op.getOperatorRepresentation());
            sb.append(" (");
            visit(op.getRight());
            sb.append(")");
        }



    }

    public void visit(UnaryOperation<T> op) {
        if (!op.omitBracketsWhenPrinting()) {
            sb.append('(');
        }
        if (op.getOperatorKind() == OperatorKind.PREFIX) {
            sb.append(op.getOperatorRepresentation());
            sb.append(' ');
        }
        if (!op.omitBracketsWhenPrinting()) {
            sb.append('(');
        }
        visit(op.getExpression());
        if (!op.omitBracketsWhenPrinting()) {
            sb.append(')');
        }
        if (op.getOperatorKind() == OperatorKind.POSTFIX) {
            sb.append(' ');
            sb.append(op.getOperatorRepresentation());
        }
        if (!op.omitBracketsWhenPrinting()) {
            sb.append(')');
        }
    }

    @SuppressWarnings("unchecked")
    public void visit(T expr) {
        assert expr != null;
        if (expr instanceof BinaryOperation<?>) {
            visit((BinaryOperation<T>) expr);
        } else if (expr instanceof UnaryOperation<?>) {
            visit((UnaryOperation<T>) expr);
        } else {
            visitSpecific(expr);
        }
    }

    public abstract void visitSpecific(T expr);

    public void visit(List<T> expressions) {
        for (int i = 0; i < expressions.size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            if(funcFlag){
                Random randomTypes = new Random();
                switch (randomTypes.nextInt(6)){
                    case 0: case 1: case 2:
                        visit(expressions.get(i));
                        break;
                    case 3:
                        sb.append("NULL");
                        break;
                    case 4:
                        sb.append("TRUE");
                        break;
                    case 5:
                        sb.append("FALSE");
                        break;
                }
            }else{
                visit(expressions.get(i));
            }


            //visit(expressions.get(i));
        }
        funcFlag = false;
    }

    public String get() {
        String sh = sb.toString()+"|||||"+sbwhere;
        sbwhere = "";
        return sh;
    }

}
