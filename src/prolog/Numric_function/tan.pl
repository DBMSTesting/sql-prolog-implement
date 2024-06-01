tan_clause(X,Result) :-
    X = null,
    Result = null.
tan_clause(X,Result) :-
    X = true,
    tan_clause(1,Result).
tan_clause(X,Result) :-
    X = false,
    tan_clause(0,Result).
tan_clause(X,Result) :-
    tan(X,Result).