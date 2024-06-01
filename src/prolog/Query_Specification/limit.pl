limit_final(H,[T],H) :-
    length(H,Length),
    S is Length-1,
    Length =< T.
limit_final(H,T,Z) :-
    I = 0,
    limit(H,I,T,Z).

limit(H,I,T,[]) :-
    I>T.
limit([Hs|Hz],I,T,[Hs|ZT]) :-
    I=<T,
    N is I+1,
    limit(Hz,N,T,ZT).


