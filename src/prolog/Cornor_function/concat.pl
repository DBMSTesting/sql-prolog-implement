concat_clause(String1,String2,Result) :-
    String1 = null,
    Result = null.
concat_clause(String1,String2,Result) :-
    String2 = null,
    Result = null.
concat_clause(String1,String2,Result) :-
    String1 = true,
    append("1",String2,Re),
    format(string(Result),'~s', [Re]).
concat_clause(String1,String2,Result) :-
    String1 = false,
    append("0",String2,Re),
    format(string(Result),'~s', [Re]).
concat_clause(String1,String2,Result) :-
    String2 = true,
    append(String1,"1",Re),
    format(string(Result),'~s', [Re]).
concat_clause(String1,String2,Result) :-
    String2 = false,
    append(String1,"0",Re),
    format(string(Result),'~s', [Re]).
concat_clause(String1,String2,Result) :-
    append(String1,String2,Re),
    format(string(Result),'~s', [Re]).
concat_clause(String1,String2,Result) :-
    atom_codes(String1, Y1),
    atom_codes(String2, Y2),
    concat_clause(Y1,Y2,Result).
