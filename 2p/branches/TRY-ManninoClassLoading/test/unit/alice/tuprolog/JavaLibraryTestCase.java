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

	public void test_java_object_4() throws PrologException, IOException
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
	}

	public void test_java_object_4_2() throws PrologException, IOException
	{
		setPath(true);
		theory = "demo_hierarchy(Gear) :- \n"
					+ "set_classpath([" + paths + "]), \n" 
					+ "java_object('Bicycle', [3, 4, 5], MyBicycle), \n"
					+ "java_object('MountainBike', [5, 6, 7, 8], MyMountainBike), \n"
					+ "MyMountainBike <- getGear returns Gear.";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo_hierarchy(Res).");
		assertEquals(false, info.isHalted());
		alice.tuprolog.Number result2 = (alice.tuprolog.Number) info.getVarValue("Res");
		assertEquals(8, result2.intValue());
	}
	
	public void test_invalid_path_java_object_4() throws PrologException, IOException
	{
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

	public void test_java_call_4() throws PrologException, IOException
	{
		//Testing java_call_4 using URLClassLoader 
		setPath(true);
		theory = "demo(Value) :- class([" + paths + "], 'TestStaticClass') <- echo('Message') returns Value.";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(StringValue).");
		assertEquals(true, info.isSuccess());
		result = info.getVarValue("StringValue").toString().replace("'", "");
		assertEquals("Message", result);
	}

	public void test_invalid_path_java_call_4() throws PrologException, IOException
	{
		//Testing java_call_4 with invalid path
		setPath(false);
		theory = "demo(Value) :- class([" + paths + "], 'TestStaticClass') <- echo('Message') returns Value.";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(StringValue).");
		assertEquals(true, info.isHalted());
	}

	public void test_java_array() throws PrologException, IOException
	{
		//Testing java_array_length using URLClassLoader 
		setPath(true);
		theory =  "demo(Size) :- java_object([" + paths + "], 'Counter', [], MyCounter), \n"
				+ "java_object([" + paths + "], 'Counter[]', [10], ArrayCounters), \n"
				+ "java_array_length(ArrayCounters, Size).";

		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Value).");
		assertEquals(true, info.isSuccess());
		alice.tuprolog.Number resultInt = (alice.tuprolog.Number) info.getVarValue("Value");
		assertEquals(10, resultInt.intValue());

		//Testing java_array_set and java_array_get
		setPath(true);
		theory =  "demo(Res) :- java_object([" + paths + "], 'Counter', [], MyCounter), \n"
				+ "java_object([" + paths + "], 'Counter[]', [10], ArrayCounters), \n"
				+ "MyCounter <- inc, \n"
				+ "java_array_set(ArrayCounters, 0, MyCounter), \n"
				+ "java_array_get(ArrayCounters, 0, C), \n"
				+ "C <- getValue returns Res.";

		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Value).");
		assertEquals(true, info.isSuccess());
		alice.tuprolog.Number resultInt2 = (alice.tuprolog.Number) info.getVarValue("Value");
		assertEquals(1, resultInt2.intValue());
	}

	public void test_set_classpath() throws PrologException, IOException
	{
		//Testing java_array_length using URLClassLoader 
		setPath(true);
		
		theory =  "demo(Size) :- set_classpath([" + paths + "]), \n "
				+ "java_object('Counter', [], MyCounter), \n"
				+ "java_object('Counter[]', [10], ArrayCounters), \n"
				+ "java_array_length(ArrayCounters, Size).";

		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Value).");
		assertEquals(true, info.isSuccess());
		alice.tuprolog.Number resultInt = (alice.tuprolog.Number) info.getVarValue("Value");
		assertEquals(10, resultInt.intValue());
	}
	
	public void test_get_classpath() throws PrologException, IOException
	{
		//Testing get_classpath using DynamicURLClassLoader with not URLs added
		theory =  "demo(P) :- get_classpath(P).";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Value).");
		assertEquals(true, info.isSuccess());
		assertEquals(true, info.getTerm("Value").isList());

		//Testing get_classpath using DynamicURLClassLoader with not URLs added
		setPath(true);

		theory =  "demo(P) :- set_classpath([" + paths + "]), get_classpath(P).";

		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Value).");
		assertEquals(true, info.isSuccess());
		assertEquals(true, info.getTerm("Value").isList());
	}
	
	public void test_register_1() throws PrologException, IOException
	{
		setPath(true);
		theory = "demo(Obj) :- \n" +
				"java_object([" + paths +"], 'Counter', [], Obj), \n" +
				"Obj <- inc, \n" +
				"Obj <- inc, \n" +
				"register(Obj).";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Res).");
		assertEquals(true, info.isSuccess());
		JavaLibrary lib = (JavaLibrary) engine.getLibrary("alice.tuprolog.lib.JavaLibrary");
		Struct id = (Struct) info.getTerm("Res");
		Object obj = lib.getRegisteredObject(id);
		assertNotNull(obj);
		
		// Test invalid object_id registration
		theory = "demo(Obj1) :- register(Obj1).";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Res).");
		assertEquals(true, info.isHalted());		
	}
	
	
	public void test_unregister_1() throws PrologException, IOException
	{
		// Test invalid object_id unregistration
		theory = "demo(Obj1) :- unregister(Obj1).";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Res).");
		assertEquals(true, info.isHalted());	
		
		setPath(true);
		theory = "demo(Obj) :- \n" +
				"java_object([" + paths +"], 'Counter', [], Obj), \n" +
				"Obj <- inc, \n" +
				"Obj <- inc, \n" +
				"register(Obj), unregister(Obj).";
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(Res).");
		assertEquals(true, info.isSuccess());
		JavaLibrary lib = (JavaLibrary) engine.getLibrary("alice.tuprolog.lib.JavaLibrary");
		Struct id = (Struct) info.getTerm("Res");
		Object obj = lib.getRegisteredObject(id);
		assertNull(obj);
	}
	
	
	
	/**
	 * @param valid: used to change a valid/invalid array of paths
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
