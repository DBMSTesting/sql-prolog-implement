or_clause(P1,P2,Z):-
    P1 \= null,
    P1 \= notnull,
    P1 \= 0,
    Z = true.
or_clause(P1,P2,Z):-
    P2 \= null,
    P2 \= notnull,
    P2 \= 0,
    Z = true.
or_clause(P1,P2,Z):-
    P1 = null,
    Z = null.
or_clause(P1,P2,Z):-
    P1 = notnull,
    Z = null.
or_clause(P1,P2,Z):-
    P2 = null,
    Z = null.
or_clause(P1,P2,Z):-
    P2 = notnull,
    Z = null.
or_clause(P1,P2,Z):-
    Z = false.
or_clause(P1,P2,Z).