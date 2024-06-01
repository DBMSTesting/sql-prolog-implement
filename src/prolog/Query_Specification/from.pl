from_clause([],T,[]).
from(HR,TR,Z) :-
    tell('example.txt'),
    from_clause(HR,TR,Z),
    told.
from(HR,TR,Z).
from_clause([[H|F]|Hs],[Ta|Tb],[[H|F]|Zs]) :-
    in(H,[Ta|Tb],P),
    P = 1,
    write([H|F]),nl,
    from_clause(Hs,[Ta|Tb],Zs).
from_clause([[H|F]|Hs],[Ta|Tb],Zs) :-
    in(H,[Ta|Tb],P),
    P = 0,
    from_clause(Hs,[Ta|Tb],Zs).
in([],[],P).
in(H,[],P) :-
    P = 0.
in(H,[Ta|Tb],P) :-
    H = Ta,
    P is 1,
    in([],[],P).
in(H,[Ta|Tb],P) :-
    H \= Ta,
    in(H,Tb,P).
