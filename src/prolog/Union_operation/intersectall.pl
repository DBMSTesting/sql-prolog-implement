intersect([], _, []):-!.
intersect([X|S1], S2, [X|S]):-
   is_member(X, S2), !,
   intersect(S1, S2, S).
intersect([_|S1], S2, S):-
   intersect(S1, S2, S).
