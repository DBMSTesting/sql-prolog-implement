log_clause(Base,X,Result) :-
    X = null,
    Result = null.
log_clause(Base,X,Result) :-
    Base = null,
    Result = null.
log_clause(Base,X,Result) :-
    X = true,
    log_clause(Base,1,Result).
log_clause(Base,X,Result) :-
    X = false,
    log_clause(Base,0,Result).
log_clause(Base,X,Result) :-
    Base = true,
    log_clause(1,X,Result).
log_clause(Base,X,Result) :-
    Base = false,
    log_clause(0,X,Result).
log_clause(X,Result) :-
    log(X,Result).