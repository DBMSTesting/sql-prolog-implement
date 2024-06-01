leftjoin_clause(H,R,Z) :-
    tell('join_result.txt'),
    leftjoin(H,R,Z),
    told.
leftjoin([A|As], [B|Bs], [A,B|Rs]) :-
    write(A),nl,
    write(B),nl,
    leftjoin(As, Bs, Rs).
leftjoin([], Bs, Bs) :-
    write(Bs),nl.
leftjoin(As, [], As):-
    write(As),nl.
