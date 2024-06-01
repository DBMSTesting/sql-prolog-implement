is_clause(P1,P2,Z) :-
    P1 = 0,
    P2 = 0,
    Z = true.
is_clause(P1,P2,Z) :-
    P1 = null,
    P2 = null,
    Z = true.
is_clause(P1,P2,Z) :-
    P1 = null,
    P2 = unknown,
    Z = true.
is_clause(P1,P2,Z) :-
    P1 = null,
    P2 = notnull,
    Z = false.
is_clause(P1,P2,Z) :-
    P1 = notnull,
    P2 = unknown,
    Z = true.
is_clause(P1,P2,Z) :-
    P1 = notnull,
    P2 = null,
    Z = true.
is_clause(P1,P2,Z) :-
    P1 = notnull,
    P2 = notnull,
    Z = true.
is_clause(P1,P2,Z) :-
    P1 \= null,
    P2 = notnull,
    Z = true.
is_clause(P1,P2,Z) :-
    P1 \= 0,
    P1 \= null,
    P2 = 1,
    Z = true.
is_clause(P1,P2,Z) :-
    Z = false.
is_clause(P1,P2,Z).