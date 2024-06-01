char_length_clause(String,Result) :-
    charlength(String,0,Result).
charlength(String,Curlength,Length) :-
    String = null,
    Length = 0.
charlength(String,Curlength,Length) :-
    String = true,
    Length = 1.
charlength(String,Curlength,Length) :-
    String = false,
    Length = 1.
charlength([],Curlength,Length) :-
    Length = Curlength.
charlength([H|Hs],Curlength,Length) :-
    U is Curlength + 1,
    charlength(Hs,U,Length).
charlength(String,Curlength,Result):-
    atom_codes(String, Y),
    charlength(Y,Curlength,Result).