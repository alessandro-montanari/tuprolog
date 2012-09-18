package alice.tuprolog;

import java.io.File;
import java.util.List;
import java.util.Map;

import alice.tuprolog.lib.InvalidObjectIdException;
import alice.tuprolog.lib.JavaLibrary;
import junit.framework.TestCase;

public class JavaLibraryTestCase extends TestCase {
	String theory = null;
	Prolog engine = null;
	SolveInfo info = null;
	
	public void testGetPrimitives() {
		Library library = new JavaLibrary();
		Map<Integer, List<PrimitiveInfo>> primitives = library.getPrimitives();
		assertEquals(3, primitives.size());
		assertEquals(0, primitives.get(PrimitiveInfo.DIRECTIVE).size());
		assertTrue(primitives.get(PrimitiveInfo.PREDICATE).size() > 0);
		assertEquals(0, primitives.get(PrimitiveInfo.FUNCTOR).size());
	}
	
	public void testAnonymousObjectRegistration() throws InvalidTheoryException, InvalidObjectIdException {
		engine = new Prolog();		
		JavaLibrary lib = (JavaLibrary) engine.getLibrary("alice.tuprolog.lib.JavaLibrary");
		String theory = "demo(X) :- X <- update. \n";
		engine.setTheory(new Theory(theory));
		TestCounter counter = new TestCounter();
		// check registering behaviour
		Struct t = lib.register(counter);
		engine.solve(new Struct("demo", t));
		assertEquals(1, counter.getValue());
		// check unregistering behaviour
		lib.unregister(t);
		SolveInfo goal = engine.solve(new Struct("demo", t));
		assertFalse(goal.isSuccess());
	}
	
	public void testDynamicObjectsRetrival() throws PrologException {
		Prolog engine = new Prolog();
		JavaLibrary lib = (JavaLibrary) engine.getLibrary("alice.tuprolog.lib.JavaLibrary");
		String theory = "demo(C) :- \n" +
		                "java_object('alice.tuprolog.TestCounter', [], C), \n" +
		                "C <- update, \n" +
		                "C <- update. \n";			
		engine.setTheory(new Theory(theory));
		SolveInfo info = engine.solve("demo(Obj).");
		Struct id = (Struct) info.getVarValue("Obj");
		TestCounter counter = (TestCounter) lib.getRegisteredDynamicObject(id);
		assertEquals(2, counter.getValue());
	}
	
	public void testJavaObject4() throws PrologException{
		try {
			engine = new Prolog();
			File file = new File(".");
			String paths = null;
			paths = "'" + file.getCanonicalPath() + "', " +
					"'" +file.getCanonicalPath() + "\\test\\unit\\TestURLClassLoader.jar'";

			// Testing URLClassLoader with a paths' array
			theory = "demo(C) :- \n" +
	                		"java_object([" + paths +"], 'Counter', [], Obj), \n" +
	                		"Obj <- inc, \n" +
	                		"Obj <- inc, \n" +
	                		"Obj <- getValue returns C.";
			engine.setTheory(new Theory(theory));
			info = engine.solve("demo(Value).");
			alice.tuprolog.Number result = (alice.tuprolog.Number) info.getVarValue("Value");
			assertEquals(2, result.intValue());
		
			// Testing URLClassLoader with java.lang.String class
			theory = 	"demo_string(S) :- \n" +
	        					"java_object('java.lang.String', ['MyString'], Obj_str), \n" +
	        					"Obj_str <- toString returns S.";
			engine.setTheory(new Theory(theory));
			info = engine.solve("demo_string(StringValue).");
			String result2 = info.getVarValue("StringValue").toString().replace("'", "");
			assertEquals("MyString", result2);
			
			//Testing incorrect path
			paths = "'" + file.getCanonicalPath() + "'";
			theory = "demo(Res) :- \n" +
            		"java_object([" + paths +"], 'Counter', [], Obj_inc), \n" +
            		"Obj_inc <- inc, \n" +
            		"Obj_inc <- inc, \n" +
            		"Obj_inc <- getValue returns Res.";
			engine.setTheory(new Theory(theory));
			info = engine.solve("demo(Value).");
			assertEquals(true, info.isHalted());
		} catch (Exception e) {
			System.out.println(e.getCause());
		}
	}
	
	public void testJavaCall4() throws PrologException
	{
		

	}
}
