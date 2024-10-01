bit_length(Binary, Length) :-
    Binary = null,
    Length = null.
% 计算二进制数据的位长度
bit_length(Binary, Length) :-
    length(Binary, BinaryLength),
    Length is BinaryLength * 8.