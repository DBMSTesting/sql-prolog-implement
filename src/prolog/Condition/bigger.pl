bigger_clause(N1,N2,Z) :-
    N1 = notnull,
    Z = null.
bigger_clause(N1,N2,Z) :-
    N2 = notnull,
    Z = null.
bigger_clause(N1,N2,Z) :-
    N1 = null,
    Z = null.
bigger_clause(N1,N2,Z) :-
    N2 = null,
    Z = null.
bigger_clause(N1,N2,Z) :-
    N1 > N2,
    Z = true.
bigger_clause(N1,N2,Z) :-
    Z = false.
bigger_clause(N1,N2,Z).