sin_clause(X,Result) :-
    X = null,
    Result = null.
sin_clause(X,Result) :-
    X = true,
    sin_clause(1,Result).
sin_clause(X,Result) :-
    X = false,
    sin_clause(0,Result).
sin_clause(X,Result) :-
    sin(X,Result).