acos_clause(X,Result) :-
    X = null,
    Result = null.
acos_clause(X,Result) :-
    X = true,
    acos_clause(1,Result).
acos_clause(X,Result) :-
    X = false,
    acos_clause(0,Result).
acos_clause(X,Result) :-
    acos(X,Result).