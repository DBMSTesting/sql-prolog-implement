orderby([H|Hs],T,AD,Z):-
    AD = asc,
    orderby_clause_asc([H|Hs],T,Z).
orderby([H|Hs],T,AD,Z):-
    AD = desc,
    orderby_clause_desc([H|Hs],T,Z).
orderby_clause_asc([H|Hs],T,Z) :-
    select(H,T,Result),
    swap_location([H|Hs],Result,[Ya|Yb]),
    msort(Yb, R),
    append([Ya],R,Z).
orderby_clause_desc([H|Hs],T,Z) :-
    select(H,T,Result),
    swap_location([H|Hs],Result,[Ya|Yb]),
    msort(Yb, Rs),
    reverse(Rs,R),
    append([Ya],R,Z).
select([],T,[]).
select([K|Ks],T,[Ra|Rb]) :-
    K = T,
    Ra is 1,
    select(Ks,T,Rb).
select([K|Ks],T,[Ra|Rb]) :-
    K \= T,
    Ra is 0,
    select(Ks,T,Rb).
swap_location([],Result,[]).
swap_location([Ha|Hb],Result,[[Ya|Yb]|Yc]) :-
    swap(Ha,Result,Yb),
    swap_new(Ha,Result,Ya),
    swap_location(Hb,Result,Yc).
swap([],[],[]).
swap([Hc|Hd],[Rc|Rd],[Hc|Yd]) :-
    Rc = 0,
    swap(Hd,Rd,Yd).
swap([Hc|Hd],[Rc|Rd],Yd) :-
    Rc = 1,
    swap(Hd,Rd,Yd).
swap_new([],[],Hc).
swap_new([Hc|Hd],[Rc|Rd],Hc) :-
    Rc = 1,
    swap_new([],[],Hc).
swap_new([Hc|Hd],[Rc|Rd],Yd) :-
    Rc = 0,
    swap_new(Hd,Rd,Yd).


