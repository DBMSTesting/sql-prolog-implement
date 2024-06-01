package sqlancer.mysql.ast;

import sqlancer.IgnoreMeException;
//import sqlancer.LikeImplementationHelper;
import sqlancer.Randomly;
import sqlancer.mysql.MySQLSchema.MySQLDataType;
import sqlancer.mysql.ast.MySQLUnaryPrefixOperation.MySQLUnaryPrefixOperator;

public class MySQLBinaryComparisonOperation implements MySQLExpression {

    public enum BinaryComparisonOperator {
        EQUALS("=") {
            @Override
            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
                return leftVal.isEquals(rightVal);
            }
        },
        NOT_EQUALS("!=") {
            @Override
            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
                MySQLConstant isEquals = leftVal.isEquals(rightVal);
                if (isEquals.getType() == MySQLDataType.INT) {
                    return MySQLConstant.createIntConstant(1 - isEquals.getInt());
                }
                return isEquals;
            }
        },
        LESS("<") {

            @Override
            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
                return leftVal.isLessThan(rightVal);
            }
        },
        LESS_EQUALS("<=") {

            @Override
            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
                MySQLConstant lessThan = leftVal.isLessThan(rightVal);
                if (lessThan == null) {
                    return null;
                }
                if (lessThan.getType() == MySQLDataType.INT && lessThan.getInt() == 0) {
                    return leftVal.isEquals(rightVal);
                } else {
                    return lessThan;
                }
            }
        },
        ADD("+") {
            @Override
            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
                return null;
            }

            @Override
            public MySQLConstant applyNotNull(MySQLConstant expr1,MySQLConstant expr2) {
                if (expr1.isString() || expr2.isString()) {
                    // TODO: implement floating points
                    throw new IgnoreMeException();
                } else if (expr1.isInt() || expr2.isInt()) {
                    if (!expr1.isSigned() || !expr2.isSigned()) {
                        // TODO
                        throw new IgnoreMeException();
                    }
                    return MySQLConstant.createIntConstant(expr1.getInt()+expr2.getInt());
                } else {
                    throw new AssertionError(expr1);
                }
            }
        },
        SUBSTRACT("-") {
            @Override
            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
                return null;
            }

            @Override
            public MySQLConstant applyNotNull(MySQLConstant expr1,MySQLConstant expr2) {
                if (expr1.isString() || expr2.isString()) {
                    // TODO: implement floating points
                    throw new IgnoreMeException();
                } else if (expr1.isInt() || expr2.isInt()) {
                    if (!expr1.isSigned() || !expr2.isSigned()) {
                        // TODO
                        throw new IgnoreMeException();
                    }
                    return MySQLConstant.createIntConstant(expr1.getInt()-expr2.getInt());
                } else {
                    throw new AssertionError(expr1);
                }
            }
        },
        MUTIPLY("*") {
            @Override
            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
                return null;
            }

            @Override
            public MySQLConstant applyNotNull(MySQLConstant expr1,MySQLConstant expr2) {
                if (expr1.isString() || expr2.isString()) {
                    // TODO: implement floating points
                    throw new IgnoreMeException();
                } else if (expr1.isInt() || expr2.isInt()) {
                    if (!expr1.isSigned() || !expr2.isSigned()) {
                        // TODO
                        throw new IgnoreMeException();
                    }
                    return MySQLConstant.createIntConstant(expr1.getInt() * expr2.getInt());
                } else {
                    throw new AssertionError(expr1);
                }
            }
        },
        DIV("/") {
            @Override
            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
                return null;
            }

            @Override
            public MySQLConstant applyNotNull(MySQLConstant expr1,MySQLConstant expr2) {
                if (expr1.isString() || expr2.isString()) {
                    // TODO: implement floating points
                    throw new IgnoreMeException();
                } else if (expr1.isInt() || expr2.isInt()) {
                    if (!expr1.isSigned() || !expr2.isSigned()) {
                        // TODO
                        throw new IgnoreMeException();
                    }
                    return MySQLConstant.createIntConstant(expr1.getInt()/expr2.getInt());
                } else {
                    throw new AssertionError(expr1);
                }
            }
        },
        GREATER(">") {
            @Override
            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
                MySQLConstant equals = leftVal.isEquals(rightVal);
                if (equals.getType() == MySQLDataType.INT && equals.getInt() == 1) {
                    return MySQLConstant.createFalse();
                } else {
                    MySQLConstant applyLess = leftVal.isLessThan(rightVal);
                    if (applyLess.isNull()) {
                        return MySQLConstant.createNullConstant();
                    }
                    return MySQLUnaryPrefixOperator.NOT.applyNotNull(applyLess);
                }
            }
        },
        GREATER_EQUALS(">=") {

            @Override
            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
                MySQLConstant equals = leftVal.isEquals(rightVal);
                if (equals.getType() == MySQLDataType.INT && equals.getInt() == 1) {
                    return MySQLConstant.createTrue();
                } else {
                    MySQLConstant applyLess = leftVal.isLessThan(rightVal);
                    if (applyLess.isNull()) {
                        return MySQLConstant.createNullConstant();
                    }
                    return MySQLUnaryPrefixOperator.NOT.applyNotNull(applyLess);
                }
            }

        };
//        LIKE("LIKE") {
//
//            @Override
//            public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) {
//                if (leftVal.isNull() || rightVal.isNull()) {
//                    return MySQLConstant.createNullConstant();
//                }
//                String leftStr = leftVal.castAsString();
//                String rightStr = rightVal.castAsString();
//                boolean matches = LikeImplementationHelper.match(leftStr, rightStr, 0, 0, false);
//                return MySQLConstant.createBoolean(matches);
//            }
//
//        };
        // https://bugs.mysql.com/bug.php?id=95908
        /*
         * IS_EQUALS_NULL_SAFE("<=>") {
         *
         * @Override public MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal) { return
         * leftVal.isEqualsNullSafe(rightVal); }
         *
         * };
         */

        private final String textRepresentation;

        public String getTextRepresentation() {
            return textRepresentation;
        }

        BinaryComparisonOperator(String textRepresentation) {
            this.textRepresentation = textRepresentation;
        }

        public abstract MySQLConstant getExpectedValue(MySQLConstant leftVal, MySQLConstant rightVal);

        public static BinaryComparisonOperator getRandom() {
            return Randomly.fromOptions(BinaryComparisonOperator.values());
        }

        public MySQLConstant applyNotNull(MySQLConstant expr1,MySQLConstant expr2) {
            if (expr1.isString() || expr2.isString()) {
                // TODO: implement floating points
                throw new IgnoreMeException();
            } else if (expr1.isInt() || expr2.isInt()) {
                if (!expr1.isSigned() || !expr2.isSigned()) {
                    // TODO
                    throw new IgnoreMeException();
                }
                return MySQLConstant.createIntConstant(expr1.getInt()+expr2.getInt());
            } else {
                throw new AssertionError(expr1);
            }
        }
    }

    private final MySQLExpression left;
    private final MySQLExpression right;
    private final BinaryComparisonOperator op;
    private final String subquery;

    public MySQLBinaryComparisonOperation(MySQLExpression left, MySQLExpression right, BinaryComparisonOperator op,String subquery) {
        this.left = left;
        this.right = right;
        this.op = op;
        this.subquery = subquery;
    }


    public MySQLExpression getLeft() {
        return left;
    }

    public BinaryComparisonOperator getOp() {
        return op;
    }

    public MySQLExpression getRight() {
        return right;
    }

    public String getSubquery() {return subquery;}

    @Override
    public MySQLConstant getExpectedValue() {
        return op.getExpectedValue(left.getExpectedValue(), right.getExpectedValue());
    }

}
