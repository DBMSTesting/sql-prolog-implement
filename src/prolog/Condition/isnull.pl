isnull_clause(P1,P) :-
    P1 = null,
    P = true.
isnull_clause(P1,P) :-
    P = false.
isnull_clause(P1,P).