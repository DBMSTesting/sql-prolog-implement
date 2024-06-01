char_clause(X,Result) :-
    X = null,
    Result = null.
char_clause(X,Result) :-
    format(string(Result),'~s', [[X]]).