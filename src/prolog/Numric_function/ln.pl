ln_clause(X,Result) :-
    X = null,
    Result = null.
ln_clause(X,Result) :-
    X = true,
    ln_clause(1,Result).
ln_clause(X,Result) :-
    X = false,
    ln_clause(0,Result).
ln_clause(X,Result) :-
    ln(X,Result).