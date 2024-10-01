bit_xor_clause(X,Y,Result) :-
    X = null,
    Result = null.
bit_xor_clause(X,Y,Result) :-
    Y = null,
    Result = null.
bit_xor_clause(X,Y,Result) :-
    Result is X xor Y.
bit_xor_clause(X,Y,Result).