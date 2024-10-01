right_clause(String,U,Result) :-
    String = null,
    Result = null
right_clause(String,U,Result) :-
    String = true,
    list_length("1",0,Z),
    right(Z,U,0,String,Re),
    format(string(Result),'~s', [Re]).
right_clause(String,U,Result) :-
    String = false,
    list_length("0",0,Z),
    right(Z,U,0,String,Re),
    format(string(Result),'~s', [Re]).
right_clause(String,U,Result) :-
    list_length(String,0,Z),
    right(Z,U,0,String,Re),
    format(string(Result),'~s', [Re]).
list_length([],Curlength,Length) :-
    Length = Curlength.
list_length([H|Hs],Curlength,Length) :-
    U is Curlength + 1,
    list_length(Hs,U,Length).
right(Z,U,L,[],[]).
right(Z,U,L,[H|Hs],Re) :-
    U >= L,
    T is L+1,
    right(Z,U,T,Hs,Re).
right(Z,U,L,[H|Hs],[H|Re]) :-
    Z > U,
    U < L,
    T is L+1,
    write(H),
    right(Z,U,T,Hs,Re).