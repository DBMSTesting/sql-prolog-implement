count_clause([H|Hs],O,K,Count) :-
    in([],H,K,Result),
    select_datas(Result,Hs,Z),
    count_in_list(Z,O,Count).
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
count_in_list([],T,Count):-
    Count is T.
count_in_list([H|Hs],T,Count) :-
    P is T + 1,
    count_in_list(Hs,P,Count).

