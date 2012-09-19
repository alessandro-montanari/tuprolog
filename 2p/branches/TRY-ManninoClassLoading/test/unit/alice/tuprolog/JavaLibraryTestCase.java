package alice.tuprolog;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import alice.tuprolog.lib.InvalidObjectIdException;
import alice.tuprolog.lib.JavaLibrary;
import junit.framework.TestCase;

public class JavaLibraryTestCase extends TestCase {
	String theory = null;
	Prolog engine = new Prolog();
	SolveInfo info = null;
	String result = null;
	String paths = null;
	
	public void testGetPrimitives() {
		Library library = new JavaLibrary();
		Map<Integer, List<PrimitiveInfo>> primitives = library.getPrimitives();
		assertEquals(3, primitives.size());
		assertEquals(0, primitives.get(PrimitiveInfo.DIRECTIVE).size());
		assertTrue(primitives.get(PrimitiveInfo.PREDICATE).size() > 0);
		assertEquals(0, primitives.get(PrimitiveInfo.FUNCTOR).size());
	}
	
	public void testAnonymousObjectRegistration() throws InvalidTheoryException, InvalidObjectIdException {	
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
	
	public void testJavaObject4() throws PrologException, IOException
	{
		// Testing URLClassLoader with a paths' array
		setPath(true);
		theory = "demo(C) :- \n" +
				"java_object([" + paths +"], 'Counter', [], Obj), \n" +
				"Obj <- inc, \n" +
				"Obj <- inc, \n" +
				"Obj <- getValue returns C.";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Value).");
		assertEquals(true, info.isSuccess());
		alice.tuprolog.Number result2 = (alice.tuprolog.Number) info.getVarValue("Value");
		assertEquals(2, result2.intValue());

		// Testing URLClassLoader with java.lang.String class
		theory = 	"demo_string(S) :- \n" +
				"java_object('java.lang.String', ['MyString'], Obj_str), \n" +
				"Obj_str <- toString returns S.";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo_string(StringValue).");
		assertEquals(true, info.isSuccess());
		result = info.getVarValue("StringValue").toString().replace("'", "");
		assertEquals("MyString", result);

		//Testing incorrect path
		setPath(false);
		theory = "demo(Res) :- \n" +
				"java_object([" + paths +"], 'Counter', [], Obj_inc), \n" +
				"Obj_inc <- inc, \n" +
				"Obj_inc <- inc, \n" +
				"Obj_inc <- getValue returns Res.";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Value).");
		assertEquals(true, info.isHalted());
	}
	
	public void testJavaCall4() throws PrologException, IOException
	{
		//Testing java_call_4 using URLClassLoader 
		setPath(true);
		theory = "demo(Value) :- class([" + paths + "], 'TestStaticClass') <- echo('Message') returns Value.";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(StringValue).");
		assertEquals(true, info.isSuccess());
		result = info.getVarValue("StringValue").toString().replace("'", "");
		assertEquals("Message", result);

		//Testing java_call_4 with invalid path
		setPath(false);
		theory = "demo(Value) :- class([" + paths + "], 'TestStaticClass') <- echo('Message') returns Value.";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(StringValue).");
		assertEquals(true, info.isHalted());
	}
	
	public void testJavaArray() throws PrologException, IOException
	{
		//Testing java_array_length using URLClassLoader 
		setPath(true);
		theory =  "demo(Res) :- java_object([" + paths + "], 'Counter', [], MyCounter), \n"
				+ "java_object([" + paths + "], 'Counter[]', [10], ArrayCounters), \n"
				+ "java_array_set(ArrayCounters, 0, MyCounter), \n"
				+ "java_array_get(ArrayCounters, 0, C), \n"
				+ "C <- getValue returns Res.";
		
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Value).");
		assertEquals(true, info.isSuccess());
		alice.tuprolog.Number resultInt = (alice.tuprolog.Number) info.getVarValue("Value");
		assertEquals(10, resultInt.intValue());
		
		// Testing java_array_set using URLClassLoader
		theory =  "demo(Res) :- java_object([" + paths + "], 'Counter', [], MyCounter), \n"
				+ "MyCounter <- inc, \n"
				+ "java_object([" + paths + "], 'Counter[]', [10], ArrayCounters), \n"
				+ "java_array_set(ArrayCounters, 0, MyCounter), \n"
				+ "java_array_get(ArrayCounters, 0, C), \n"
				+ "C <- getValue returns Res.";
		
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Value).");
		assertEquals(true, info.isSuccess());
		alice.tuprolog.Number resultInt2 = (alice.tuprolog.Number) info.getVarValue("Value");
		assertEquals(1, resultInt2.intValue());
	}
	
	/*
	 * param valid: used to change a valid/invalid array of paths
	 */
	private void setPath(boolean valid) throws IOException
	{
		File file = new File(".");
		// Array paths contains a valid path
		if(valid)
		{
			paths = "'" + file.getCanonicalPath() + "', " +
				"'" + file.getCanonicalPath() 
				+ File.separator + "test"
				+ File.separator + "unit" 
				+ File.separator + "TestURLClassLoader.jar'";
		}
		// Array paths does not contain a valid path
		else
			paths = "'" + file.getCanonicalPath() + "'";
	}
}
