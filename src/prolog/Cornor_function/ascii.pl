ascii_clause(String,Result) :-
    ascii(String,Result).
ascii(String,Result):-
    String = null,
    Result = null.
ascii(String,Result):-
    String = true,
    ascii("1",Result).
ascii(String,Result):-
    String = false,
    ascii("0",Result).
ascii([H|Hs],Result):-
    Result is H.
ascii(String,Result):-
    atom_codes(String, Y),
    ascii(Y,Result).