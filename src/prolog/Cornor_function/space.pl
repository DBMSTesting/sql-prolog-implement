space(Count, Spaces) :-
    Count = null,
    Spaces = null.
space(Count, Spaces) :-
    Count < 0,
    Spaces = null.
% 生成指定数量的空格字符串
space(Count, Spaces) :-
    length(SpacesList, Count),  % 创建长度为Count的列表
    maplist(=(32), SpacesList),  % 将列表中的元素设置为ASCII码为32的空格字符
    string_codes(Spaces, SpacesList).  % 将列表转换为字符串