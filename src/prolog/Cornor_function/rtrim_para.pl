rtrim(String, TrimChar, TrimmedString) :-
    String = null,
    TrimmedString = null.
rtrim(String, TrimChar, TrimmedString) :-
    TrimChar = null,
    TrimmedString = null.
% 删除右侧指定字符
rtrim(String, TrimChar, TrimmedString) :-
    atom_chars(String, Chars),  % 将字符串转换为字符列表
    rtrimHelper(Chars, TrimChar, TrimmedChars),  % 调用辅助谓词处理字符列表
    atom_chars(TrimmedString, TrimmedChars).  % 将修剪后的字符列表转换回字符串

% 辅助谓词：删除右侧指定字符的实现
rtrimHelper([], _, []).
rtrimHelper([TrimChar | Rest], TrimChar, TrimmedChars) :-  % 如果当前字符是指定字符
    rtrimHelper(Rest, TrimChar, TrimmedChars).  % 递归调用删除剩余字符的指定字符
rtrimHelper(Chars, _, Chars).  % 当前字符不是指定字符，返回剩余的字符列表
