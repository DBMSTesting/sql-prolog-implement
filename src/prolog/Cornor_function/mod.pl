mod(X, Y, R) :-
    X = null,
    R = null.
mod(X, Y, R) :-
    Y = null,
    R = null.
mod(X, Y, R) :-
    R is X mod Y.
mod(X, Y, R) :-
    R is X mod Y.