left_clause(String,U,Result) :-
    String = null,
    Result = null
left_clause(String,U,Result) :-
    String = true,
    list_length("1",0,Z),
    left(Z,U,0,String,Re),
    format(string(Result),'~s', [Re]).
left_clause(String,U,Result) :-
    String = false,
    list_length("0",0,Z),
    left(Z,U,0,String,Re),
    format(string(Result),'~s', [Re]).
left_clause(String,U,Result) :-
    list_length(String,0,Z),
    left(Z,U,0,String,Re),
    format(string(Result),'~s', [Re]).
list_length([],Curlength,Length) :-
    Length = Curlength.
list_length([H|Hs],Curlength,Length) :-
    U is Curlength + 1,
    list_length(Hs,U,Length).
left(Z,U,L,String,[]) :-
    U < L.
left(Z,U,L,[H|Hs],[H|Re]) :-
    Z > U,
    U >= L,
    T is L+1,
    left(Z,U,T,Hs,Re).

