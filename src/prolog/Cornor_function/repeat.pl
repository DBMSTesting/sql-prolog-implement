repeat_clause(String,Num,Result) :-
    String = null,
    Result = null.
repeat_clause(String,Num,Result) :-
    Num = null,
    Result = null.
repeat_clause(String,Num,Result) :-
    Num = true,
    repeat_clause(String,1,Result).
repeat_clause(String,Num,Result) :-
    Num = false,
    repeat_clause(String,0,Result).
repeat_clause(String,Num,Result) :-
    String = true,
    repeat_clause("1",Num,Result).
repeat_clause(String,Num,Result) :-
    String = false,
    repeat_clause("0",Num,Result).
repeat_clause(String,Num,Result) :-
    repeat(String,String,Num,1,Re),
    format(string(Result),'~s', [Re]).
repeat(String1,String2,Num,Z,String1) :-
    Z >= Num.
repeat(String1,String2,Num,Z,Re) :-
    Z<Num,
    append(String1,String2,StringF),
    U is Z + 1,
    repeat(StringF,String2,Num,U,Re).
