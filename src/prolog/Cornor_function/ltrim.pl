ltrim(String, TrimmedString) :-
    String = null,
    TrimmedString = null.
% 删除左侧空格
ltrim(String, TrimmedString) :-
    atom_chars(String, Chars),  % 将字符串转换为字符列表
    ltrimHelper(Chars, TrimmedChars),  % 调用辅助谓词处理字符列表
    atom_chars(TrimmedString, TrimmedChars).  % 将修剪后的字符列表转换回字符串

% 辅助谓词：删除左侧空格的实现
ltrimHelper([], []).
ltrimHelper([' ' | Rest], TrimmedChars) :-  % 如果当前字符是空格
    ltrimHelper(Rest, TrimmedChars).  % 递归调用删除剩余字符的空格
ltrimHelper(Chars, Chars).  % 当前字符不是空格，返回剩余的字符列表