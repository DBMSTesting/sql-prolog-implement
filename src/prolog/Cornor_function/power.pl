power_clause(T,Num,Result) :-
    T = null,
    Result = null.
power_clause(T,Num,Result) :-
    Num = null,
    Result = null.
power_clause(T,Num,Result) :-
    T = true,
    power_clause(1,Num,Result).
power_clause(T,Num,Result) :-
    Num = true,
    power_clause(T,1,Result).
power_clause(T,Num,Result) :-
    Num = false,
    power_clause(T,0,Result).
power_clause(T,Num,Result) :-
    Num = 0,
    Result is 1.
power_clause(T,Num,Result) :-
    power(T,T,Num,1,Result).

power(T1,T2,Num,Z,T1) :-
    Z>=Num.
power(T1,T2,Num,Z,Result) :-
    Z<Num,
    U is Z+1,
    T3 is T1 * T2,
    power(T3,T2,Num,U,Result).
