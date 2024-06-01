package sqlancer.common.gen;

import java.util.ArrayList;
import java.util.List;

import sqlancer.Randomly;

public abstract class UntypedExpressionGenerator<E, C> implements ExpressionGenerator<E> {

    protected List<C> columns;
    protected boolean allowAggregates;

    public E generateExpression(String currentCov) {
        return generateExpression(0,currentCov);
    }

    public abstract E generateConstant();

    protected abstract E generateExpression(int depth,String currentCov);

    protected abstract E generateColumn();

    @SuppressWarnings("unchecked") // unsafe
    public <U extends UntypedExpressionGenerator<E, C>> U setColumns(List<C> columns) {
        this.columns = columns;
        return (U) this;
    }

    public E generateLeafNode() {
        if (Randomly.getBoolean() && !columns.isEmpty()) {
            return generateColumn();
        } else {
            return generateConstant();
        }
    }

    public List<E> generateExpressions(int nr,String currentCov) {
        List<E> expressions = new ArrayList<>();
        for (int i = 0; i < nr; i++) {
            expressions.add(generateExpression(currentCov));
        }
        return expressions;
    }

    public List<E> generateExpressions(int nr, int depth,String currentcov) {
        List<E> expressions = new ArrayList<>();
        for (int i = 0; i < nr; i++) {
            expressions.add(generateExpression(depth,currentcov));
        }
        return expressions;
    }

    // override this class to also generate ASC, DESC
    public List<E> generateOrderBys(String currentCov) {
        return generateExpressions(Randomly.smallNumber() + 1,currentCov);
    }

    // override this class to generate aggregate functions
    public E generateHavingClause(String currentCov) {
        allowAggregates = true;
        E expr = generateExpression(currentCov);
        allowAggregates = false;
        return expr;
    }

    @Override
    public E generatePredicate(String currentCov) {
        return generateExpression(currentCov);
    }

}
