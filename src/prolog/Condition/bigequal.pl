bigequal_clause(N1,N2,Z) :-
    N1 = notnull,
    Z = null.
bigequal_clause(N1,N2,Z) :-
    N2 = notnull,
    Z = null.
bigequal_clause(N1,N2,Z) :-
    N1 = null,
    Z = null.
bigequal_clause(N1,N2,Z) :-
    N2 = null,
    Z = null.
bigequal_clause(N1,N2,Z) :-
    N1 >= N2,
    Z = true.
bigequal_clause(N1,N2,Z) :-
    Z = false.
bigequal_clause(N1,N2,Z).