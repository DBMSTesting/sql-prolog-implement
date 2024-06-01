distinct_clause(HR,K,Z) :-
    select_final(HR,K,Uz),
    filterA(Uz,Fa),
    filterB(Uz,Fb),
    sort(Fb,Zt),
    Z=[Fa|Zt].
select_final(HR,K,U) :-
    tell('select_result.txt'),
    select_clause(HR,K,U),
    told.
select_clause([H|Hs],K,U) :-
    K = *,
    U = [H|Hs].
select_clause([H|Hs],K,U) :-
    in([],H,K,Result),
    select_datas(Result,[H|Hs],U).
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
    write(B),nl,
    select_data(Ps,Bs,Cs).
select_data([P|Ps],[B|Bs],Cs) :-
    P = 0,
    select_data(Ps,Bs,Cs).
filterA([Fa|Fb],Fa).
filterB([Fa|Fb],Fb).

