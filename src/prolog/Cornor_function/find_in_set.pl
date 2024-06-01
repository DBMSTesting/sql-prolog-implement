find_in_set_clause(H,U,Result) :-
    H = null,
    Result = null.
find_in_set_clause(H,U,Result) :-
    findinset(H,U,1,Result).
findinset(H,[],Z,Result):-
    Result is 0.
findinset(H,[Ut|Us],Z,Result) :-
    H = Ut,
    Result is Z.
findinset(H,[Ut|Us],Z,Result) :-
    H = true,
    Ut = 1,
    Result is Z.
findinset(H,[Ut|Us],Z,Result) :-
    H = false,
    Ut = 0,
    Result is Z.
findinset(H,[Ut|Us],Z,Result) :-
    H \= Ut,
    T is Z + 1,
    findinset(H,Us,T,Result).