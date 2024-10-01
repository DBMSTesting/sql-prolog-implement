character_length_clause(String,Result) :-
    characterlength(String,0,Result).
characterlength(String,Curlength,Length) :-
    String = null,
    Length = 0.
characterlength(String,Curlength,Length) :-
    String = true,
    Length = 1.
characterlength(String,Curlength,Length) :-
    String = false,
    Length = 1.
characterlength([],Curlength,Length) :-
    Length = Curlength.
characterlength([H|Hs],Curlength,Length) :-
    U is Curlength + 1,
    characterlength(Hs,U,Length).
characterlength(String,Curlength,Result):-
    atom_codes(String, Y),
    characterlength(Y,Curlength,Result).