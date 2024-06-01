equal_clause(N1,N2,Z) :-
    N1 = notnull,
    Z = null.
equal_clause(N1,N2,Z) :-
    N2 = notnull,
    Z = null.
equal_clause(N1,N2,Z) :-
    N1 = null,
    Z = null.
equal_clause(N1,N2,Z) :-
    N2 = null,
    Z = null.
equal_clause(N1,N2,Z) :-
    N1 = N2,
    Z = true.
equal_clause(N1,N2,Z) :-
    Z = false.
equal_clause(N1,N2,Z).