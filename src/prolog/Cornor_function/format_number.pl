format_number(Number, Format, Formatted) :-
    Number = null,
    Formatted = null.
format_number(Number, Format, Formatted) :-
    Format = null,
    Formatted = null.
% 格式化数值
format_number(Number, Format, Formatted) :-
    format(atom(Formatted), Format, [Number]).