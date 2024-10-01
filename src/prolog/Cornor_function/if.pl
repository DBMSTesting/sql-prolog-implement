if(Condition, ValueIfTrue, ValueIfFalse, Result) :-
    Condition = null,
    Result = null.
% 实现类似于MySQL中的IF函数的功能
if(Condition, ValueIfTrue, ValueIfFalse, Result) :-
    (Condition -> Result = ValueIfTrue ; Result = ValueIfFalse).