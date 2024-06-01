sortfirst([H|Hs], T, S) :-
    select(H,T,R),
    maplist(get_first, Hs, A),
    msort(A, B),
    maplist(set_first, Hs, B, S).
select([],T,[]).
select([K|Ks],T,[Ra|Rb]) :-
    K=T,
    Ra is 1,
    select(Ks,T,Rb).
select([K|Ks],T,[Ra|Rb]) :-
    Ra is 0,
    select(Ks,T,Rb).

get_first([E|_], E).
set_first([_|R], E, [E|R]).