where([],[]).
select([H|List],[H|Filtered]) :-
   check_condition(H),
   select(List,Filtered).                % 符合条件的数据被保存在筛选数组
select([H|List],Filtered) :-
 select(List,Filtered).                % 不符合条件的数据不会被筛选
check_condition([L|[R|N]]) :-           % 筛选条件，可自定义
   R > 5.
