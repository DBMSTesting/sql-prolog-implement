length_clause(String,Result) :-
    String = null,
    Result = null.
length_clause(String,Result) :-
    String = true,
    Result = 1.
length_clause(String,Result) :-
    String = false,
    Result = 0.
length_clause(String,Result) :-
    lengths(String,0,Result).
lengths([],Curlength,Length) :-
    Length = Curlength.
lengths([H|Hs],Curlength,Length) :-
    U is Curlength + 1,
    lengths(Hs,U,Length).