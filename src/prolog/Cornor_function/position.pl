position_clause(String1,String2,Result) :-
    String1 = null,
    Result = null.
position_clause(String1,String2,Result) :-
    String2 = null,
    Result = null.
position_clause(String1,String2,Result) :-
    position(String1,String2,1,Result).
position(String1,[],Z,Result) :-
    Result is 0.
position(String1,[H|Hs],Z,Result) :-
    String1 = [H],
    Result is Z.
position(String1,[H|Hs],Z,Result):-
    String1 \= [H],
    T is Z+1,
    position(String1,Hs,T,Result).
