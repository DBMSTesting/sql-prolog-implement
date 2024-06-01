bit_and_clause(X,Y,Result) :-
    X = null,
    Result = null.
bit_and_clause(X,Y,Result) :-
    Y = null,
    Result = null.
bit_and_clause(X,Y,Result) :-
    Result is X /\ Y.
bit_and_clause(X,Y,Result).