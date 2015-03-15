# Introduction #

Unlike other Prolog systems, tuprolog performs the occurs check systematically. So, goals like X=f(X) fail in tuprolog, while other Prolog systems either loop or provide approximate (false) answers.

The special predicate unify\_with\_occurs\_check/2 therefore is implemented simply as =/2.

# Details #

File Var.java, line 420
File BasicLibrary.java, line 1065