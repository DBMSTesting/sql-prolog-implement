ucase_clause(String,Result) :-
    String = null,
    Result = null.
ucase_clause(String,Result) :-
    String = true,
    ucase("1",[],Re),
    format(string(Result),'~s', [Re]).
ucase_clause(String,Result) :-
    String = false,
    ucase("0",[],Re),
    format(string(Result),'~s', [Re]).
ucase_clause(String,Result) :-
    ucase(String,[],Re),
    format(string(Result),'~s', [Re]).
ucase([],U,U).
ucase([H|Hs],U,Re) :-
    H>96,
    H<123,
    T is H-32,
    append(U,[T],ZT),
    ucase(Hs,ZT,Re).
ucase([H|Hs],U,Re) :-
    write(U),
    append(U,[H],ZT),
    ucase(Hs,ZT,Re).