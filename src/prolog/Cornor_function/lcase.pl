lcase_clause(String,Result) :-
    String = null,
    Result = null.
lcase_clause(String,Result) :-
    String = true,
    lcase("1",[],Re),
    format(string(Result),'~s', [Re]).
lcase_clause(String,Result) :-
    String = false,
    lcase("0",[],Re),
    format(string(Result),'~s', [Re]).
lcase_clause(String,Result) :-
    lcase(String,[],Re),
    format(string(Result),'~s', [Re]).
lcase([],U,U).
lcase([H|Hs],U,Re) :-
    H>64,
    H<91,
    T is H+32,
    append(U,[T],ZT),
    lcase(Hs,ZT,Re).
lcase([H|Hs],U,Re) :-
    append(U,[H],ZT),
    lcase(Hs,ZT,Re).