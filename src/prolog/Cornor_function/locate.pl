locate(Substring, String, Position) :-
    Substring = null,
    Position = null.
locate(Substring, String, Position) :-
    String = null,
    Position = null.
locate(Substring, String, Position) :-
    sub_string(String, Position, _, _, Substring).