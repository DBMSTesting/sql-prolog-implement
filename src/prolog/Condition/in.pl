in_clause(X,[],Flag,Z) :-
    write(Flag),
    Flag=0,
    Z=false.
in_clause(X,[],Flag,Z) :-
    Flag=1,
    Z=null.
in_clause(X,H,Flag,Z) :-
    X = null,
    Z = null.
in_clause(X,[Hp|Hs],Flag,Z) :-
    X = Hp,
    Z = true.
in_clause(X,[Hp|Hs],Flag,Z) :-
    X \= Hp,
    Hp = null,
    in_clause(X,Hs,1,Z).
in_clause(X,[Hp|Hs],Flag,Z) :-
    X \= Hp,
    in_clause(X,Hs,Flag,Z).