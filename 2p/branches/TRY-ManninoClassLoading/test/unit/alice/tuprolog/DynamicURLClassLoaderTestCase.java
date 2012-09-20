package alice.tuprolog;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import alice.util.DynamicURLClassLoader;

public class DynamicURLClassLoaderTestCase {
	
	final static int PATHS_NUMBER = 2;
	String[] paths = new String[PATHS_NUMBER];
	
	@Test
	public void ConstructorTest() throws MalformedURLException, IOException, ClassNotFoundException{
		DynamicURLClassLoader loader = new DynamicURLClassLoader();
		assertNotNull(loader);
		
		setPath(true);
		URL[] urls = getURLsFromStringArray(paths);
		loader = new DynamicURLClassLoader(urls, this.getClass().getClassLoader());
		assertEquals(2, loader.getURLs().length);
	}
	
	@Test 
	public void LoadClassTest() throws MalformedURLException, IOException, ClassNotFoundException
	{
		DynamicURLClassLoader loader = null;
		setPath(true);
		URL[] urls = getURLsFromStringArray(paths);
		loader = new DynamicURLClassLoader(urls, this.getClass().getClassLoader());
		assertEquals(2, loader.getURLs().length);
		
		try {
			Class<?> cl = loader.loadClass("Counter");
			assertNotNull(cl);
			Method m = cl.getMethod("inc", new Class[]{});
			m.setAccessible(true);
			Object obj = cl.newInstance();
			m.invoke(obj, new Object[]{});
			Method m1 = cl.getMethod("getValue", new Class[]{});
			m1.setAccessible(true);
			int result = Integer.parseInt(m.invoke(obj, new Object[]{}).toString());
			assertEquals(1, result);
		} catch (Exception e) {
			System.out.println(e.getCause());
		}
		
	}
	
	private void setPath(boolean valid) throws IOException
	{
		File file = new File(".");
		// Array paths contains a valid path
		if(valid)
		{
			paths[0] = file.getCanonicalPath()
				+ File.separator + "test"
				+ File.separator + "unit" 
				+ File.separator + "TestURLClassLoader.jar";
		}
		paths[1] = file.getCanonicalPath();
	}
	
	private URL[] getURLsFromStringArray(String[] paths) throws MalformedURLException  
    {
    	URL[] urls = new URL[paths.length];
		
		for (int i = 0; i < paths.length; i++) 
		{
			File directory = new File(paths[i]);
			urls[i] = (directory.toURI().toURL());
		}
		return urls;
    }
}
