floor_clause(X,Result) :-
    X = null,
    Result = null.
floor_clause(X,Result) :-
    X = true,
    floor_clause(1,Result).
floor_clause(X,Result) :-
    X = false,
    floor_clause(0,Result).
floor_clause(X,Result) :-
    floor(X,Result).