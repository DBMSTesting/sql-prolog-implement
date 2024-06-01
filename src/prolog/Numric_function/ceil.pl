ceil_clause(X,Result) :-
    X = null,
    Result = null.
ceil_clause(X,Result) :-
    X = true,
    ceil_clause(1,Result).
ceil_clause(X,Result) :-
    X = false,
    ceil_clause(0,Result).
ceil_clause(X,Result) :-
    ceil(X,Result).