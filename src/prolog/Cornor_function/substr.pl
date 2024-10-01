substr(Str, Start, End, Substr) :-
    Str = null,
    Substr = null.
substr(Str, Start, End, Substr) :-
    Start = null,
    Substr = null.
substr(Str, Start, End, Substr) :-
    End = null,
    Substr = null.
% SUBSTR函数：提取字符串中的子字符串
substr(Str, Start, End, Substr) :-
    atom_chars(Str, Chars),
    substr_helper(Chars, Start, End, SubstrChars),
    atom_chars(Substr, SubstrChars).

substr_helper(_, Start, End, []) :-
    Start > End, !.  % 递归结束条件
substr_helper(_, _, End, []) :-
    End =< 0, !.
substr_helper([Char|Rest], Start, End, [Char|Substr]) :-
    NextStart is Start - 1,
    NextEnd is End - 1,
    substr_helper(Rest, NextStart, NextEnd, Substr).