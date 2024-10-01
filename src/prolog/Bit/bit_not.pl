bit_not_clause(X,Result) :-
    X = null,
    Result = null.
bit_not_clause(X,Result) :-
    Result is \X.
bit_not_clause(X,Result).