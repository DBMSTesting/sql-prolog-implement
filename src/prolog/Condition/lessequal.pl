lessequal_clause(N1,N2,Z) :-
    N1 = notnull,
    Z = null.
lessequal_clause(N1,N2,Z) :-
    N2 = notnull,
    Z = null.
lessequal_clause(N1,N2,Z) :-
    N1 = null,
    Z = null.
lessequal_clause(N1,N2,Z) :-
    N2 = null,
    Z = null.
lessequal_clause(N1,N2,Z) :-
    N1 =< N2,
    Z = true.
lessequal_clause(N1,N2,Z) :-
    Z = false.
lessequal_clause(N1,N2,Z).