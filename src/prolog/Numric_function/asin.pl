asin_clause(X,Result) :-
    X = null,
    Result = null.
asin_clause(X,Result) :-
    X = true,
    asin_clause(1,Result).
asin_clause(X,Result) :-
    X = false,
    asin_clause(0,Result).
asin_clause(X,Result) :-
    asin(X,Result).