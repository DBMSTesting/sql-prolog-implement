bit_shift_left_clause(X,Y,Result) :-
    X = null,
    Result = null.
bit_shift_left_clause(X,Y,Result) :-
    Y = null,
    Result = null.
bit_shift_left_clause(X,Y,Result) :-
    Result is X << Y.
bit_shift_left_clause(X,Y,Result).