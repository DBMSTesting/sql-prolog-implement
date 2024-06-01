radians_clause(X,Result) :-
    X = null,
    Result = null.
radians_clause(X,Result) :-
    X = true,
    radians_clause(1,Result).
radians_clause(X,Result) :-
    X = false,
    radians_clause(0,Result).
radians_clause(X,Result) :-
    radians(X,Result).