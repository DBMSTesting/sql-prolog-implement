substring_index_clause(String,Index,Num,Result) :-
    String = null,
    Result = null.
substring_index_clause(String,Index,Num,Result) :-
    Index = null,
    Result = null.
substring_index_clause(String,Index,Num,Result) :-
    Num = null,
    Result = null.
substring_index_clause(String,Index,Num,Result) :-
    Num < 0,
    Result = null.
substring_index_clause(String,Index,Num,Result) :-
    substringindex(String,Index,1,Num,Re),
    format(string(Result),'~s', [Re]).
substringindex([],Index,Z,Num,[]).
substringindex([H|Hs],Index,Z,Num,Re) :-
    Z<Num,
    T is Z+1,
    substringindex(Hs,Index,T,Num,Re).
substringindex([H|Hs],Index,Z,Num,[H|Rs]) :-
    Z>=Num,
    [H] \= Index,
    T is Z+1,
    write(H),
    substringindex(Hs,Index,T,Num,Rs).
substringindex([H|Hs],Index,Z,Num,Rs) :-
    Z>=Num,
    [H] = Index,
    substringindex([],Index,Z,Num,Rs).
