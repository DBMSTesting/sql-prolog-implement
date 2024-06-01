exists_clause(P1,P) :-
    P1 = 0,
    P = false.
exists_clause(P1,P) :-
    P = 1,
    P = true.
exists_clause(P1,P) :-
    P = null,
    P = null.
exists_clause(P1,P) :-
    P = notnull,
    P = null.
exists_clause(P1,P) :-
    P = true.
exists_clause(P1,P).