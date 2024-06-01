% 实现类似于SQL中的COALESCE函数的功能
coalesce(FirstValue, DefaultValue, Result) :-
    (FirstValue \= null -> Result = FirstValue ; Result = DefaultValue).