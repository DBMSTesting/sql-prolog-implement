bit_length(String, Length) :-
    String = null,
    Length = null.
% 计算字符串的位长度
bit_length(String, Length) :-
    atom_length(String, AtomLength),
    Length is AtomLength * 8.