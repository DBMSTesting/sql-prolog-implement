bin(X, B) :-
    X = null,
    B = null.
bin(X, B) :-
    number(X),  % 确保输入是数字
    B is X,
    bin_helper(B).

bin_helper(B) :-
    B < 2, !.  % 递归结束条件
bin_helper(B) :-
    Quotient is B // 2,
    Remainder is B mod 2,
    bin_helper(Quotient),
    write(Remainder).