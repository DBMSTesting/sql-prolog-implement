not_clause(P1,Z) :-
    P1 \= 0,
    P1 \= null,
    P1 \= notnull,
    P1 \= unknown,
    Z = false.
not_clause(P1,Z) :-
    P1 = 0,
    Z = true.
not_clause(P1,Z) :-
    P1 = null,
    Z = notnull.
not_clause(P1,Z) :-
    P1 = unknown,
    Z = notnull.
not_clause(P1,Z) :-
    P1 = notnull,
    Z = null.
not_clause(P1,Z).