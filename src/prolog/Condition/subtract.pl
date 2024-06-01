subtract_clause(P1,P2,Z) :-
    P1 = null,
    Z = null.
subtract_clause(P1,P2,Z) :-
    P2 = null,
    Z = null.
subtract_clause(P1,P2,Z) :-
    Z is P1 - P2.
subtract_clause(P1,P2,Z).