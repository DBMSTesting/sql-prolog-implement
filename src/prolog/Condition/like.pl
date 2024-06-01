% 实现类似于SQL中的LIKE函数的功能
like(Expression, Pattern) :-
    atom_chars(Expression, ExpChars),
    atom_chars(Pattern, PatternChars),
    match(ExpChars, PatternChars).

match([], []).
match([X|T1], ['%'|T2]) :-
    match([X|T1], T2).
match([_|T1], ['_'|T2]) :-
    match(T1, T2).
match([X|T1], [X|T2]) :-
    match(T1, T2).