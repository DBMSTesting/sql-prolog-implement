ltrim(String, TrimChar, TrimmedString) :-
    String = null,
    TrimmedString = null.
ltrim(String, TrimChar, TrimmedString) :-
    TrimChar = null,
    TrimmedString = null.
% 删除左侧指定字符
ltrim(String, TrimChar, TrimmedString) :-
    atom_chars(String, Chars),  % 将字符串转换为字符列表
    ltrimHelper(Chars, TrimChar, TrimmedChars),  % 调用辅助谓词处理字符列表
    atom_chars(TrimmedString, TrimmedChars).  % 将修剪后的字符列表转换回字符串

% 辅助谓词：删除左侧指定字符的实现
ltrimHelper([], _, []).
ltrimHelper([TrimChar | Rest], TrimChar, TrimmedChars) :-  % 如果当前字符是指定字符
    ltrimHelper(Rest, TrimChar, TrimmedChars).  % 递归调用删除剩余字符的指定字符
ltrimHelper(Chars, _, Chars).  % 当前字符不是指定字符，返回剩余的字符列表