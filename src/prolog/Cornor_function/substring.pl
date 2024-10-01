substring_clause(String,Num,Result) :-
    String = null,
    Result = null.
substring_clause(String,Num,Result) :-
    Num = null,
    Result = null.
substring_clause(String,Num,Result) :-
    Num < 0,
    Result = null.
substring_clause(String,Num,Result) :-
    Num = true,
    substring_clause(String,1,Result).
substring_clause(String,Num,Result) :-
    Num = false,
    substring_clause(String,0,Result).
substring_clause(String,Num,Result) :-
    String = false,
    substring_clause("0",Num,Result).
substring_clause(String,Num,Result) :-
    String = true,
    substring_clause("1",Num,Result).
substring_clause(String,Num,Result) :-
    substring(String,1,Num,Re),
    format(string(Result),'~s', [Re]).
substring([],Z,Num,[]).
substring([H|Hs],Z,Num,Re) :-
    Z<Num,
    T is Z+1,
    substring(Hs,T,Num,Re).
substring([H|Hs],Z,Num,[H|Rs]) :-
    Z>=Num,
    T is Z+1,
    substring(Hs,T,Num,Rs).

