missing_clause(P1,P) :-
    P1 = 0,
    P = true.
missing_clause(P1,P) :-
    P = 1,
    P = false.
missing_clause(P1,P) :-
    P = null,
    P = null.
missing_clause(P1,P) :-
    P = notnull,
    P = null.
missing_clause(P1,P) :-
    P = true.
missing_clause(P1,P).