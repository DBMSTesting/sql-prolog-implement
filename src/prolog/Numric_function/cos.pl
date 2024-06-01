cos_clause(X,Result) :-
    X = null,
    Result = null.
cos_clause(X,Result) :-
    X = true,
    cos_clause(1,Result).
cos_clause(X,Result) :-
    X = false,
    cos_clause(0,Result).
cos_clause(X,Result) :-
    cos(X,Result).