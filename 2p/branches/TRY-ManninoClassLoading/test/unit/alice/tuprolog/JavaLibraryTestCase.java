package alice.tuprolog;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;

import alice.tuprolog.lib.InvalidObjectIdException;
import alice.tuprolog.lib.JavaLibrary;
import junit.framework.TestCase;

public class JavaLibraryTestCase extends TestCase {
	
	public void testGetPrimitives() {
		Library library = new JavaLibrary();
		Map<Integer, List<PrimitiveInfo>> primitives = library.getPrimitives();
		assertEquals(3, primitives.size());
		assertEquals(0, primitives.get(PrimitiveInfo.DIRECTIVE).size());
		assertTrue(primitives.get(PrimitiveInfo.PREDICATE).size() > 0);
		assertEquals(0, primitives.get(PrimitiveInfo.FUNCTOR).size());
	}
	
	public void testAnonymousObjectRegistration() throws InvalidTheoryException, InvalidObjectIdException {
		Prolog engine = new Prolog();		
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
	
	public void testURLClassLoader() throws PrologException
	{
		try {
			Prolog engine = new Prolog();
			File file = new File(".");
			String paths = null;
			paths = "'" + file.getCanonicalPath() + "', " +
					"'" +file.getCanonicalPath() + "\\test\\unit'";

			// Testing URLClassLoader with a paths' array
			String theory = "demo(C) :- \n" +
	                		"java_object([" + paths +"], 'Counter', [], Obj), \n" +
	                		"Obj <- inc, \n" +
	                		"Obj <- inc, \n" +
	                		"Obj <- getValue returns C.";
			engine.setTheory(new Theory(theory));
			SolveInfo info = engine.solve("demo(Value).");
			alice.tuprolog.Number result = (alice.tuprolog.Number) info.getVarValue("Value");
			assertEquals(2, result.intValue());
		
			// Testing URLClassLoader with java.lang.String class
			String theory2 = 	"demo_string(S) :- \n" +
	        					"java_object('java.lang.String', ['MyString'], Obj_str), \n" +
	        					"Obj_str <- toString returns S.";
			engine.setTheory(new Theory(theory2));
			SolveInfo info2 = engine.solve("demo_string(StringValue).");
			String result2 = info2.getVarValue("StringValue").toString().replace("'", "");
			assertEquals("MyString", result2);
			
//			String theory3 = 	"demo_call(CL) :- \n" +
//			"java_call([" + paths + "], ['Counter'], Obj_cl), \n" +
//			"Obj_cl <- toString returns CL.";
			
		} catch (Exception e) {
			System.out.println(e.getCause());
		}
		
		

		
	}
}
