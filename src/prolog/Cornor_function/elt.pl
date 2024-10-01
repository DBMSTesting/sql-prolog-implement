elt_clause([H|Hz],Z) :-
    H = null,
    Z = null.
elt_clause([H|Hz],Z) :-
    I is 1,
    elt(H,Hz,I,Z).
elt_clause([H|Hz],Z) :-
    H = true,
    elt(1,Hz,I,Z).
elt_clause([H|Hz],Z) :-
    H = false,
    elt(1,Hz,I,Z).
elt_clause([H|Hz],Z) :-
    H = false,
    Z = null.
elt_clause([H|Hz],Z) :-
    H = 0,
    Z = null.
elt(H,[],I,Z) :-
    Z = 0.
elt(H,[Hs|Hh],I,Z) :-
    H = I,
    Z = Hs.
elt(H,[Hs|Hh],I,Z) :-
    H\=I,
    P is I+1,
    elt(H,Hh,P,Z).

