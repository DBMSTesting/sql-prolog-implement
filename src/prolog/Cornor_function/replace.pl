replace_clause(String,Y,R,Result) :-
    String = null,
    Result = null.
replace_clause(String,Y,R,Result) :-
    Y = null,
    Result = null.
replace_clause(String,Y,R,Result) :-
    R = null,
    Result = null.
replace_clause(String,Y,R,Result) :-
    replace(String,Y,R,Re),
    format(string(Result),'~s', [Re]).
replace([],Y,R,[]).
replace([H|Hs],Y,R,[H|Rs]) :-
    [H] \= Y,
    replace(Hs,Y,R,Rs).
replace([H|Hs],Y,R,[T|Rs]) :-
    [H] = Y,
    T is R,
    replace(Hs,Y,R,Rs).


