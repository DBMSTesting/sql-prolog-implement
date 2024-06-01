atan_clause(X,Result) :-
    X = null,
    Result = null.
atan_clause(X,Result) :-
    X = true,
    atan_clause(1,Result).
atan_clause(X,Result) :-
    X = false,
    atan_clause(0,Result).
atan_clause(X,Result) :-
    atan(X,Result).