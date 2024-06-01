rpad_clause(String,Num,Addtion,Y) :-
    String = null,
    Y = null.
rpad_clause(String,Num,Addtion,Y) :-
    Num = null,
    Y = null.
rpad_clause(String,Num,Addtion,Y) :-
    Addtion = null,
    Y = null.
rpad_clause(String,Num,Addtion,Y) :-
    Num < 0,
    Y = null.
rpad_clause(String,Num,Addtion,Y) :-
    list_length(String,0,Z),
    list_length(Addtion,0,T),
    rpad(String,Num,Addtion,Z,T,Result),
    format(string(Y),'~s', [Result]).
list_length([],Curlength,Length) :-
    Length = Curlength.
list_length([H|Hs],Curlength,Length) :-
    U is Curlength + 1,
    list_length(Hs,U,Length).
rpad(String,Num,Addtion,Z,T,Re):-
    Z < Num,
    U is Z+T,
    write(U),
    append(String,Addtion,ZT),
    write(ZT),
    rpad(ZT,Num,Addtion,U,T,Re).
rpad(String,Num,Addtion,Z,T,String).

