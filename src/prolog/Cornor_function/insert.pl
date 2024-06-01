insert_string(Source, Insert, Index, Result) :-
    Source = null,
    Result = null.
insert_string(Source, Insert, Index, Result) :-
    Insert = null,
    Result = null.
insert_string(Source, Insert, Index, Result) :-
    Index = null,
    Result = null.
insert_string(Source, Insert, Index, Result) :-
    Index < 0,
    Result = null.
% 在字符串中插入子字符串
insert_string(Source, Insert, Index, Result) :-
    sub_string(Source, 0, Index, _, Substring1),
    sub_string(Source, Index, _, 0, Substring2),
    string_concat(Substring1, Insert, Temp),
    string_concat(Temp, Substring2, Result).