union_clause(T,[K|Ks],Z) :-
    append(T, K, S),
    unique(S, Z).
union_clause(T,K,Z).