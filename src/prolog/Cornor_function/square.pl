square_clause(T,Result) :-
    T = null,
    Result = null.
square_clause(T,Result) :-
    T = true,
    Result = 1.
square_clause(T,Result) :-
    square(T,Result).
square(T,Result) :-
    Result is T*T.
