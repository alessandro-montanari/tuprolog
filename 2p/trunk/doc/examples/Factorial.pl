fact(0,1):- !. 
fact(N,X):- M is N-1, fact(M,Y), X is Y*N.

start(N,X,M,Y):- 	thread_create(ID1, fact(N,X)), 
					thread_create(ID2, fact(M,Y)),
					thread_read(ID1, fact(N,X)), 
					thread_read(ID2, fact(M,Y)).

