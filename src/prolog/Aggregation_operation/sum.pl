sum_clause([H|Hs],O,K,Sum) :-
    in([],H,K,Result),
    select_datas(Result,Hs,Z),
    sum_in_list(Z,O,Sum).
select_datas(Temp,[],[]).
select_datas(Temp,[Rs|Rb],[Za|Zb]) :-
    select_data(Temp,Rs,Za),
    select_datas(Temp,Rb,Zb).
in(Temp,[],K,[]).
in(Temp,[S|Sk],K,[RA|RD]) :-
    singal_in(Temp,S,K,RA),
    in(R,Sk,K,RD).
singal_in(Temp,t,[],R).
singal_in(Temp,S,[],R) :-
    R is 0.
singal_in(Temp,S,[Ah|As],R) :-
    S = Ah,
    R is 1,
    singal_in(R,t,[],R1).
singal_in(Temp,S,[A|As],R) :-
    S \= A,
    singal_in(Temp,S,As,R).
select_data([],[],[]).
select_data([P|Ps],[B|Bs],[B|Cs]) :-
    P = 1,
    select_data(Ps,Bs,Cs).
select_data([P|Ps],[B|Bs],Cs) :-
    P = 0,
    select_data(Ps,Bs,Cs).

sum_in_list([],O,Sum) :-
    Sum is O.
sum_in_list([[H|Hb]|Hs],O,Sum) :-
    H is null,
    sum_in_list(Hs,T,Sum).
sum_in_list([[H|Hb]|Hs],O,Sum) :-
    N is O + H,
    T is N,
    sum_in_list(Hs,T,Sum).

