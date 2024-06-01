bit_or_clause(X,Y,Result) :-
    X = null,
    Result = null.
bit_or_clause(X,Y,Result) :-
    Y = null,
    Result = null.
bit_or_clause(X,Y,Result) :-
    Result is X \/ Y.
bit_or_clause(X,Y,Result).