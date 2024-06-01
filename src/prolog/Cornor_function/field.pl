field_clause([H|Hz],Z) :-
    H = null,
    Z = 0.
field_clause([H|Hz],Z) :-
    H = true,
    field(1,Hz,I,Z).
field_clause([H|Hz],Z) :-
    H = false,
    field(0,Hz,I,Z).
field_clause([H|Hz],Z) :-
    I is 1,
    field(H,Hz,I,Z).
field(H,[],I,Z) :-
    Z = 0.
field(H,[Hs|Hz],I,Z) :-
    H = Hs,
    Z = I.
field(H,[Hs|Hz],I,Z) :-
    H \= Hs,
    P is 1 + I,ß
    field(H,Hz,P,Z).