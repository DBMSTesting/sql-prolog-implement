lower_clause(String,Result) :-
    String = null,
    Result = null.
lower_clause(String,Result) :-
    String = true,
    lower("1",[],Re),
    format(string(Result),'~s', [Re]).
lower_clause(String,Result) :-
    String = false,
    lower("0",[],Re),
    format(string(Result),'~s', [Re]).
lower_clause(String,Result) :-
    lower(String,[],Re),
    format(string(Result),'~s', [Re]).
lower([],U,U).
lower([H|Hs],U,Re) :-
    H>64,
    H<91,
    T is H+32,
    append(U,[T],ZT),
    lower(Hs,ZT,Re).
lower([H|Hs],U,Re) :-
    write(U),
    append(U,[H],ZT),
    lower(Hs,ZT,Re).