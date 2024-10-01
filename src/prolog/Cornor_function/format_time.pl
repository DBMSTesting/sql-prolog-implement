format_datetime(DateTime, Format, Formatted) :-
    DateTime = null,
    Formatted = null.
format_datetime(DateTime, Format, Formatted) :-
    Format = null,
    Formatted = null.
% 格式化日期/时间值
format_datetime(DateTime, Format, Formatted) :-
    format_time(atom(Formatted), Format, DateTime).