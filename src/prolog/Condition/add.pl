add_clause(P1,P2,Z) :-
    P1 = null,
    Z = null.
add_clause(P1,P2,Z) :-
    P2 = null,
    Z = null.
add_clause(P1,P2,Z) :-
    Z is P1 + P2.
add_clause(P1,P2,Z).
