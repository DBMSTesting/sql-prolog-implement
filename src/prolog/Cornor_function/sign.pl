sign_clause(T,Result) :-
    T = null,
    Result = null.
sign_clause(T,Result) :-
    T = true,
    Result = 1.
sign_clause(T,Result) :-
    T = false,
    Result = 0.
sign_clause(T,Result) :-
    sign(T,Result).
sign(T,Result) :-
    T >0,
    Result is 1.
sign(T,Result) :-
    T =0,
    Result is 0.
sign(T,Result) :-
    T <0,
    Result is -1.
