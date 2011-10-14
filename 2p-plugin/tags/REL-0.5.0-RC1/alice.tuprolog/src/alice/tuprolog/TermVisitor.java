package alice.tuprolog;

public interface TermVisitor {
	void visit(Struct s);
	void visit(Var v);
	void visit(Number n);
}
