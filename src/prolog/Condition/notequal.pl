notequal_clause(N1,N2,Z) :-
    N1 = null,
    Z = null.
notequal_clause(N1,N2,Z) :-
    N2 = null,
    Z = null.
notequal_clause(N1,N2,Z) :-
    N1 \= N2,
    Z = true.
notequal_clause(N1,N2,Z) :-
    Z = false.
notequal_clause(N1,N2,Z).