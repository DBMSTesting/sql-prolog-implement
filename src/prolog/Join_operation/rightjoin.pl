rightjoin([A|As], [B|Bs], [A,B|Rs]) :-
    rightjoin(As, Bs, Rs).
rightjoin([], Bs, Bs).
rightjoin(As, [], As).