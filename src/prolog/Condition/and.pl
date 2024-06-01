and_clause(P1,P2,Z):-
    P1 \= null,
    P1 \= notnull,
    P1 \= 0,
    P2 \= null,
    P2 \= notnull,
    P2 \= 0,
    Z = true.
and_clause(P1,P2,Z):-
    P1 = null,
    P2 \= 0,
    Z = null.
and_clause(P1,P2,Z):-
    P1 \= 0,
    P2 = null,
    Z = null.
and_clause(P1,P2,Z):-
    P1 = notnull,
    P2 \= 0,
    Z = null.
and_clause(P1,P2,Z):-
    P1 \= 0,
    P2 = notnull,
    Z = null.
and_clause(P1,P2,Z):-
    Z = false.
and_clause(P1,P2,Z).
