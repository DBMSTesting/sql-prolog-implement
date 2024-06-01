upper_clause(String,Result) :-
    String = null,
    Result = null.
upper_clause(String,Result) :-
    String = true,
    upper("1",[],Re),
    format(string(Result),'~s', [Re]).
upper_clause(String,Result) :-
    String = false,
    upper("0",[],Re),
    format(string(Result),'~s', [Re]).
upper_clause(String,Result) :-
    upper(String,[],Re),
    format(string(Result),'~s', [Re]).
upper([],U,U).
upper([H|Hs],U,Re) :-
    H>96,
    H<123,
    T is H-32,
    append(U,[T],ZT),
    upper(Hs,ZT,Re).
upper([H|Hs],U,Re) :-
    write(U),
    append(U,[H],ZT),
    upper(Hs,ZT,Re).