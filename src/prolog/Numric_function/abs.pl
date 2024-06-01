abs_clause(X,Result) :-
    X = null,
    Result = null.
abs_clause(X,Result) :-
    X = true,
    abs_clause(1,Result).
abs_clause(X,Result) :-
    X = false,
    abs_clause(0,Result).
abs_clause(X,Result) :-
    abs(X,Result).