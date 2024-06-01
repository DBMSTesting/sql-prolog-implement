xor_clause(P1,P2,Z) :-
    P1 = 1,
    P2 = 1,
    Z = false.
xor_clause(P1,P2,Z) :-
    P1 = 1,
    P2 = 0,
    Z = true.
xor_clause(P1,P2,Z) :-
    P1 = 0,
    P2 = 1,
    Z = true.
xor_clause(P1,P2,Z) :-
    P1 = 0,
    P2 = 0,
    Z = false.
xor_clause(P1,P2,Z) :-
    P1 = null,
    Z = null.
xor_clause(P1,P2,Z) :-
    P2 = null,
    Z = null.
xor_clause(P1,P2,Z) :-
    P1 = P2
    Z = false.
xor_clause(P1,P2,Z) :-
    P1 \= P2
    Z = true.
xor_clause(P1,P2,Z).