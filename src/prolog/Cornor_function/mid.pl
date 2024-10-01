% MID函数：提取字符串中的子字符串
mid(Str, Start, Length, Substr) :-
    atom_chars(Str, Chars),
    mid_helper(Chars, Start, Length, SubstrChars),
    atom_chars(Substr, SubstrChars).

mid_helper(_, Start, Length, []) :-
    Length =< 0, !.  % 递归结束条件
mid_helper([Char|Rest], Start, Length, [Char|Substr]) :-
    Start > 0,  % 忽略开始前的字符
    NextStart is Start - 1,
    NextLength is Length - 1,
    mid_helper(Rest, NextStart, NextLength, Substr).
mid_helper(_, _, Length, []) :-
    Length =< 0.