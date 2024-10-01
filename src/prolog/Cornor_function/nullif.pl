nullif(Expression1, Expression2, Result) :-
    Expression1 = null,
    Result = null.
nullif(Expression1, Expression2, Result) :-
    Expression2 = null,
    Result = null.
nullif(Expression1, Expression2, Result) :-
    (Expression1 = Expression2 -> Result = null ; Result = Expression1).