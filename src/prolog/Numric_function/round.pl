round(X,D,R) :-
    X = null,
    R = null.
round(X,D,R) :-
    D = null,
    R = null.
round(X, D, R) :-
    number(X),  % 确保输入是数值类型
    integer(D), % 确保小数位数是整数
    D >= 0,     % 确保小数位数为非负数
    Power is 10^D,
    R is round(X * Power) / Power.