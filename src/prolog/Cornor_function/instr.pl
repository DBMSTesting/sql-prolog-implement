instr(String, Substring, Position) :-
    String = null,
    Position = null.
instr(String, Substring, Position) :-
    Substring = null,
    Position = null.
instr(String, Substring, Position) :-
    sub_string(String, Position, _, _, Substring).