less_clause(N1,N2,Z) :-
    N1 = notnull,
    Z = null.
less_clause(N1,N2,Z) :-
    N2 = notnull,
    Z = null.
less_clause(N1,N2,Z) :-
    N1 = null,
    Z = null.
less_clause(N1,N2,Z) :-
    N2 = null,
    Z = null.
less_clause(N1,N2,Z) :-
    N1 >= N2,
    Z = false.
less_clause(N1,N2,Z) :-
    N1 < N2,
    Z = true.
less_clause(N1,N2,Z).
