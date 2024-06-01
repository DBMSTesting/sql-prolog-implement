rtrim(String, TrimmedString) :-
    String = null,
    TrimmedString = null.
% 删除右侧空格
rtrim(String, TrimmedString) :-
    atom_chars(String, Chars),  % 将字符串转换为字符列表
    rtrimHelper(Chars, TrimmedChars),  % 调用辅助谓词处理字符列表
    atom_chars(TrimmedString, TrimmedChars).  % 将修剪后的字符列表转换回字符串

% 辅助谓词：删除右侧空格的实现
rtrimHelper([], []).
rtrimHelper(Chars, TrimmedChars) :-
    reverse(Chars, ReversedChars),  % 反转字符列表
    ltrimHelper(ReversedChars, ReversedTrimmedChars),  % 调用左修剪辅助谓词
    reverse(ReversedTrimmedChars, TrimmedChars).  % 反转修剪后的字符列表