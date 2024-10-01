strcmp_clause(String1,String2,Result) :-
    String1 = null,
    Result = null.
strcmp_clause(String1,String2,Result) :-
    String2 = null,
    Result = null.
strcmp_clause(String1,String2,Result) :-
    String1 = true,
    strcmp_clause("1",String2,Result).
strcmp_clause(String1,String2,Result) :-
    String1 = false,
    strcmp_clause("0",String2,Result).
strcmp_clause(String1,String2,Result) :-
    String2 = true,
    strcmp_clause(String1,"1",Result).
strcmp_clause(String1,String2,Result) :-
    String2 = false,
    strcmp_clause(String1,"0",Result).
strcmp_clause(String1,String2,Result) :-
    list_length(String1,0,Z),
    list_length(String2,0,T),
    strcmp(Z,T,Result).
list_length([],Curlength,Length) :-
    Length = Curlength.
list_length([H|Hs],Curlength,Length) :-
    U is Curlength + 1,
    list_length(Hs,U,Length).
strcmp(Z,T,Result) :-
    Z>T,
    Result is 1.
strcmp(Z,T,Result) :-
    Z<T,
    Result is -1.
strcmp(Z,T,Result) :-
    Z=T,
    Result is 0.