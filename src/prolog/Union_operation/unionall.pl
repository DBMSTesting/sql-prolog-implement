unionall(HR,K,U) :-
    tell('unionall_result.txt'),
    unionall_clause(HR,K,U),
    told.
unionall_clause(T,[K|Ks],Z) :-
    append(T, Ks, Z),
    write(Z).
unionall_clause(T,K,Z).