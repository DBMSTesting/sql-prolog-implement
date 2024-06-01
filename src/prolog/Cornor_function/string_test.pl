replace_substring(String,To_Replace,Replace_With,Result) :-
    test(String,Z),
    format(string(Result),'~s', [Z]).
test([H|Hs],[H]).

