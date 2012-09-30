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
	String checkString = null;
	
//	public void testGetPrimitives() {
//		Library library = new JavaLibrary();
//		Map<Integer, List<PrimitiveInfo>> primitives = library.getPrimitives();
//		assertEquals(3, primitives.size());
//		assertEquals(0, primitives.get(PrimitiveInfo.DIRECTIVE).size());
//		assertTrue(primitives.get(PrimitiveInfo.PREDICATE).size() > 0);
//		assertEquals(0, primitives.get(PrimitiveInfo.FUNCTOR).size());
//	}
//
//	public void testAnonymousObjectRegistration() throws InvalidTheoryException, InvalidObjectIdException {	
//		JavaLibrary lib = (JavaLibrary) engine.getLibrary("alice.tuprolog.lib.JavaLibrary");
//		String theory = "demo(X) :- X <- update. \n";
//		engine.setTheory(new Theory(theory));
//		TestCounter counter = new TestCounter();
//		// check registering behaviour
//		Struct t = lib.register(counter);
//		engine.solve(new Struct("demo", t));
//		assertEquals(1, counter.getValue());
//		// check unregistering behaviour
//		lib.unregister(t);
//		SolveInfo goal = engine.solve(new Struct("demo", t));
//		assertFalse(goal.isSuccess());
//	}
//
//	public void testDynamicObjectsRetrival() throws PrologException {
//		Prolog engine = new Prolog();
//		JavaLibrary lib = (JavaLibrary) engine.getLibrary("alice.tuprolog.lib.JavaLibrary");
//		String theory = "demo(C) :- \n" +
//				"java_object('alice.tuprolog.TestCounter', [], C), \n" +
//				"C <- update, \n" +
//				"C <- update. \n";			
//		engine.setTheory(new Theory(theory));
//		SolveInfo info = engine.solve("demo(Obj).");
//		Struct id = (Struct) info.getVarValue("Obj");
//		TestCounter counter = (TestCounter) lib.getRegisteredDynamicObject(id);
//		assertEquals(2, counter.getValue());
//	}
//
//	public void test_java_object_4() throws PrologException, IOException
//	{
//		// Testing URLClassLoader with a paths' array
//		setPath(true);
//		theory = "demo(C) :- \n" +
//				"java_object([" + paths +"], 'Counter', [], Obj), \n" +
//				"Obj <- inc, \n" +
//				"Obj <- inc, \n" +
//				"Obj <- getValue returns C.";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(Value).");
//		assertEquals(true, info.isSuccess());
//		alice.tuprolog.Number result2 = (alice.tuprolog.Number) info.getVarValue("Value");
//		assertEquals(2, result2.intValue());
//
//		// Testing URLClassLoader with java.lang.String class
//		theory = 	"demo_string(S) :- \n" +
//				"java_object('java.lang.String', ['MyString'], Obj_str), \n" +
//				"Obj_str <- toString returns S.";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo_string(StringValue).");
//		assertEquals(true, info.isSuccess());
//		result = info.getVarValue("StringValue").toString().replace("'", "");
//		assertEquals("MyString", result);
//	}
//
//	public void test_java_object_4_2() throws PrologException, IOException
//	{
//		setPath(true);
//		theory = "demo_hierarchy(Gear) :- \n"
//					+ "set_classpath([" + paths + "]), \n" 
//					+ "java_object('Bicycle', [3, 4, 5], MyBicycle), \n"
//					+ "java_object('MountainBike', [5, 6, 7, 8], MyMountainBike), \n"
//					+ "MyMountainBike <- getGear returns Gear.";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo_hierarchy(Res).");
//		assertEquals(false, info.isHalted());
//		alice.tuprolog.Number result2 = (alice.tuprolog.Number) info.getVarValue("Res");
//		assertEquals(8, result2.intValue());
//	}
//	
//	public void test_invalid_path_java_object_4() throws PrologException, IOException
//	{
//		//Testing incorrect path
//		setPath(false);
//		theory = "demo(Res) :- \n" +
//				"java_object([" + paths +"], 'Counter', [], Obj_inc), \n" +
//				"Obj_inc <- inc, \n" +
//				"Obj_inc <- inc, \n" +
//				"Obj_inc <- getValue returns Res.";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(Value).");
//		assertEquals(true, info.isHalted());
//	}
//
//	public void test_java_call_3() throws PrologException, IOException
//	{
//		//Testing java_call_3 using URLClassLoader 
//		setPath(true); 
//		theory = "demo(Value) :- class([" + paths + "], 'TestStaticClass') <- echo('Message') returns Value.";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(StringValue).");
//		assertEquals(true, info.isSuccess());
//		result = info.getVarValue("StringValue").toString().replace("'", "");
//		assertEquals("Message", result);
//
//		//Testing get/set static Field 
//		setPath(true);
//		theory = "demo_2(Value) :- class([" + paths + "], 'TestStaticClass').'id' <- get(Value).";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo_2(Res).");
//		assertEquals(true, info.isSuccess());		
//		assertEquals(0, Integer.parseInt(info.getVarValue("Res").toString()));
//		
//		theory = "demo_2(Value, NewValue) :- class([" + paths + "], 'TestStaticClass').'id' <- set(Value), \n" +
//				"class([" + paths + "], 'TestStaticClass').'id' <- get(NewValue).";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo_2(5, Val).");
//		assertEquals(true, info.isSuccess());		
//		assertEquals(5, Integer.parseInt(info.getVarValue("Val").toString()));
//		
//	}
//
//	public void test_invalid_path_java_call_4() throws PrologException, IOException
//	{
//		//Testing java_call_4 with invalid path
//		setPath(false);
//		theory = "demo(Value) :- class([" + paths + "], 'TestStaticClass') <- echo('Message') returns Value.";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(StringValue).");
//		assertEquals(true, info.isHalted());
//	}
//
//	public void test_java_array() throws PrologException, IOException
//	{
//		//Testing java_array_length using URLClassLoader 
//		setPath(true);
//		theory =  "demo(Size) :- java_object([" + paths + "], 'Counter', [], MyCounter), \n"
//				+ "java_object([" + paths + "], 'Counter[]', [10], ArrayCounters), \n"
//				+ "java_array_length(ArrayCounters, Size).";
//
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(Value).");
//		assertEquals(true, info.isSuccess());
//		alice.tuprolog.Number resultInt = (alice.tuprolog.Number) info.getVarValue("Value");
//		assertEquals(10, resultInt.intValue());
//
//		//Testing java_array_set and java_array_get
//		setPath(true);
//		theory =  "demo(Res) :- java_object([" + paths + "], 'Counter', [], MyCounter), \n"
//				+ "java_object([" + paths + "], 'Counter[]', [10], ArrayCounters), \n"
//				+ "MyCounter <- inc, \n"
//				+ "java_array_set(ArrayCounters, 0, MyCounter), \n"
//				+ "java_array_get(ArrayCounters, 0, C), \n"
//				+ "C <- getValue returns Res.";
//
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(Value).");
//		assertEquals(true, info.isSuccess());
//		alice.tuprolog.Number resultInt2 = (alice.tuprolog.Number) info.getVarValue("Value");
//		assertEquals(1, resultInt2.intValue());
//	}
//
//	public void test_set_classpath() throws PrologException, IOException
//	{
//		//Testing java_array_length using URLClassLoader 
//		setPath(true);
//		
//		theory =  "demo(Size) :- set_classpath([" + paths + "]), \n "
//				+ "java_object('Counter', [], MyCounter), \n"
//				+ "java_object('Counter[]', [10], ArrayCounters), \n"
//				+ "java_array_length(ArrayCounters, Size).";
//
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(Value).");
//		assertEquals(true, info.isSuccess());
//		alice.tuprolog.Number resultInt = (alice.tuprolog.Number) info.getVarValue("Value");
//		assertEquals(10, resultInt.intValue());
//	}
//	
//	public void test_get_classpath() throws PrologException, IOException
//	{
//		//Testing get_classpath using DynamicURLClassLoader with not URLs added
//		theory =  "demo(P) :- get_classpath(P).";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(Value).");
//		assertEquals(true, info.isSuccess());
//		assertEquals(true, info.getTerm("Value").isList());
//		assertEquals("[]", info.getTerm("Value").toString());
//
//		//Testing get_classpath using DynamicURLClassLoader with not URLs added
//		setPath(true);
//
//		theory =  "demo(P) :- set_classpath([" + paths + "]), get_classpath(P).";
//
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(Value).");
//		assertEquals(true, info.isSuccess());
//		assertEquals(true, info.getTerm("Value").isList());
//		assertEquals(checkString, info.getTerm("Value").toString());
//	}
//	
//	public void test_register_1() throws PrologException, IOException
//	{
//		setPath(true);
//		theory = "demo(Obj) :- \n" +
//				"java_object([" + paths +"], 'Counter', [], Obj), \n" +
//				"Obj <- inc, \n" +
//				"Obj <- inc, \n" +
//				"register(Obj).";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(R).");
//		assertEquals(true, info.isSuccess());
//		
//		theory = "demo2(Obj, Val) :- \n"
//				+ "Obj <- inc, \n"
//				+ "Obj <- getValue returns Val.";
//		engine.addTheory(new Theory(theory));
//		String obj =  info.getTerm("R").toString();
//		SolveInfo info2 = engine.solve("demo2(" + obj + ", V).");
//		assertEquals(true, info2.isSuccess());
//		assertEquals(3, Integer.parseInt(info2.getVarValue("V").toString()));
//	
//		// Test invalid object_id registration
//		theory = "demo(Obj1) :- register(Obj1).";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(Res).");
//		assertEquals(true, info.isHalted());		
//	}
//	
//	
//	public void test_unregister_1() throws PrologException, IOException
//	{
//		// Test invalid object_id unregistration
//		theory = "demo(Obj1) :- unregister(Obj1).";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(Res).");
//		assertEquals(true, info.isHalted());	
//		
//		setPath(true);
//		theory = "demo(Obj) :- \n" +
//				"java_object([" + paths +"], 'Counter', [], Obj), \n" +
//				"Obj <- inc, \n" +
//				"Obj <- inc, \n" +
//				"register(Obj), unregister(Obj).";
//		engine.setTheory(new Theory(theory));
//		info = engine.solve("demo(Res).");
//		assertEquals(true, info.isSuccess());
//		JavaLibrary lib = (JavaLibrary) engine.getLibrary("alice.tuprolog.lib.JavaLibrary");
//		Struct id = (Struct) info.getTerm("Res");
//		Object obj = lib.getRegisteredObject(id);
//		assertNull(obj);
//	}
//	
//

	public void test_java_catch() throws PrologException, IOException
	{
		setPath(true);
//		theory =  "load(Obj) :- set_classpath([" + paths + "]), java_object('TestStaticClass', [], Obj), register(Obj).\n"
//				+ "demo(Obj, Msg) :- java_catch(Obj <- testMyException, [('java.lang.JavaException'( \n"
//						+ "Cause, Msg, StackTrace),write(Msg))], \n"
//						+ "true).";
		
//		theory = "goal :- set_classpath([" + paths + "]), java_object('TestStaticClass', [], Obj), Obj <- testMyException. \n"
//				+"demo(Msg) :- java_catch(goal, [('java.lang.JavaException'( \n"
//						+ "Cause, Msg, StackTrace),write(Msg))], \n"
//						+ "true).";
		// Test example n. 7.0
		theory = "demo(Msg) :- java_catch(java_object('Counter', ['MyCounter'], c)," +
				"[('java.lang.ClassNotFoundException'(Cause, Msg, StackTrace)," +
				"write(Msg))]," +
				"write('Finally done!')).";
		
		engine.setTheory(new Theory(theory));
		info = engine.solve("demo(M).");
		assertEquals(true, info.isSuccess());
		assertEquals("Counter", alice.util.Tools.removeApices(info.getVarValue("M").toString()));
		
		// Test example n. 7.1
		theory = "goal(X, Y) :- java_catch(java_object('Counter', ['MyCounter'], c)," +
				"[('java.lang.ClassNotFoundException'(Cause, Message, _)," +
						"X is 2+3)], Y is 2+5).";

		engine.setTheory(new Theory(theory));
		info = engine.solve("goal(X, Y).");
		assertEquals(true, info.isSuccess());
		assertEquals(5, Integer.parseInt(info.getVarValue("X").toString()));
		assertEquals(7, Integer.parseInt(info.getVarValue("Y").toString()));
		
		// Test example n. 7.2
		theory = "goal :- java_catch(java_object('Counter', ['MyCounter'], c)," +
				"[('java.lang.Exception'(Cause, Message, _), true)], true).";

		engine.setTheory(new Theory(theory));
		info = engine.solve("goal.");
		assertEquals(true, info.isHalted());
		
		// Test example 7.3
		theory = "goal :- java_catch(java_object('Counter', ['MyCounter'], c)," +
				"[('java.lang.Exception'(Cause, Message, _), false)], true).";

		engine.setTheory(new Theory(theory));
		info = engine.solve("goal.");
		assertEquals(true, info.isHalted());
		
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
			
			File aFile = new File(file.getCanonicalPath() + File.separator + "test" 
					+ File.separator + "unit" + File.separator
					+ "TestURLClassLoader.jar");
			
			checkString = "['" + new File(new File(".").getCanonicalPath()).toURI().toURL().toString().replace("%20", " ") + "','"
							+ aFile.toURI().toURL().toString().replace("%20", " ") + "']";
		}
		// Array paths does not contain a valid path
		else
		{
			paths = "'" + file.getCanonicalPath() + "'";
			checkString = "['" + new File(file.getCanonicalPath()).toURI().toURL() + "']";
		}
	}
}
