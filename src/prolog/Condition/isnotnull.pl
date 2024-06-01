isnotnull_clause(P1,Z) :-
    P1 = null,
    Z = false.
isnotnull_clause(P1,Z) :-
    Z = true.
isnotnull_clause(P1,Z).