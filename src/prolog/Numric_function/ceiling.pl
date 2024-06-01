ceiling_clause(X,Result) :-
    X = null,
    Result = null.
ceiling_clause(X,Result) :-
    X = true,
    ceiling_clause(1,Result).
ceiling_clause(X,Result) :-
    X = false,
    ceiling_clause(0,Result).
ceiling_clause(X,Result) :-
    ceil(X,Result).