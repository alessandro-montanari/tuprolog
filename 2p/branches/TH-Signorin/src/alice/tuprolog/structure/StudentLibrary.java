package alice.tuprolog.structure;

//franz.beslmeisl at googlemail.com

import alice.tuprolog.Library;
import alice.tuprolog.Term;

//import in a prolog session by
//:-load_library('StudentLibrary').
public class StudentLibrary extends Library{

	private static final long serialVersionUID = 1L;

public boolean termframe_1(Term term){
new TermFrame(term);
return true;
}
}
