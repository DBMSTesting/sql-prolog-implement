degrees_clause(X,Result) :-
    X = null,
    Result = null.
degrees_clause(X,Result) :-
    X = true,
    degrees_clause(1,Result).
degrees_clause(X,Result) :-
    X = false,
    degrees_clause(0,Result).
degrees_clause(X,Result) :-
    degrees(X,Result).