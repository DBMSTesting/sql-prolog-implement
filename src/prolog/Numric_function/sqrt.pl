sqrt_clause(X,Result) :-
    X = null,
    Result = null.
sqrt_clause(X,Result) :-
    X = true,
    sqrt_clause(1,Result).
sqrt_clause(X,Result) :-
    X = false,
    sqrt_clause(0,Result).
sqrt_clause(X,Result) :-
    sqrt(X,Result).