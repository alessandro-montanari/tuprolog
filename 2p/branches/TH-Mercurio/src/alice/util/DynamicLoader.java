package alice.util;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 
 * @author Alessio Mercurio
 *
 * This class is used to manage DynamicURLClassloader (for Java platform) and DexClassLoader (for Android platform).
 * 
 */

public class DynamicLoader
{
	private DynamicURLClassLoader dynamicURLClassLoader;
	
	private DynamicDexClassLoader dynamicDexClassLoader;
	

	public DynamicLoader(ClassLoader parent)
	{
		if (System.getProperty("java.vm.name").equals("Dalvik"))
		{
			dynamicDexClassLoader = new DynamicDexClassLoader("", "/data/data/alice.tuprologx.android/app_dex", null, parent);
		} 
		else
		{
			dynamicURLClassLoader = new DynamicURLClassLoader(new URL[] {}, parent);
		}
	}

	public ClassLoader getClassLoader()
	{
		if (System.getProperty("java.vm.name").equals("Dalvik"))
		{
			return dynamicDexClassLoader.getClassLoader();
		} 
		else
		{
			return dynamicURLClassLoader;
		}
	}

	public void addURLs(URL[] urls) throws MalformedURLException
	{
		if (System.getProperty("java.vm.name").equals("Dalvik"))
		{
			dynamicDexClassLoader.addURLs(urls);
		} 
		else
		{
			dynamicURLClassLoader.addURLs(urls);
		}
	}

	public void removeAllURLs()
	{
		if (System.getProperty("java.vm.name").equals("Dalvik"))
		{
			dynamicDexClassLoader.removeAllURLs();
		} 
		else
		{
			dynamicURLClassLoader.removeAllURLs();
		}
	}

	public URL[] getURLs()
	{
		if (System.getProperty("java.vm.name").equals("Dalvik"))
		{
			return dynamicDexClassLoader.getURLs();
		} 
		else
		{
			return dynamicURLClassLoader.getURLs();
		}
	}
}
