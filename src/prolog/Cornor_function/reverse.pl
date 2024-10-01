reverse_clause(String,Result) :-
    String = null,
    Result = null.
reverse_clause(String,Result) :-
    String = true,
    reverse_clause("1",Result).
reverse_clause(String,Result) :-
    String = false,
    reverse_clause("0",Result).
reverse_clause(String,Result) :-
    reverse(String,Re),
    format(string(Result),'~s', [Re]).
