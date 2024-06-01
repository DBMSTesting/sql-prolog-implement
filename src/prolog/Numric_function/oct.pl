oct(X, O) :-
    X = null,
    O = null.
oct(X, O) :-
    number(X),  % 确保输入是数字
    O is X,
    oct_helper(O).

oct_helper(O) :-
    O < 8, !.  % 递归结束条件
oct_helper(O) :-
    Quotient is O // 8,
    Remainder is O mod 8,
    oct_helper(Quotient),
    write(Remainder).