case(Condition, ValueIfTrue, ValueIfFalse, Result) :-
    Condition = null,
    Result = null.
% 定义 case/3 谓词来模拟 SQL 中的 CASE 函数
case(Condition, ValueIfTrue, ValueIfFalse, Result) :-
    (Condition -> Result = ValueIfTrue ; Result = ValueIfFalse).