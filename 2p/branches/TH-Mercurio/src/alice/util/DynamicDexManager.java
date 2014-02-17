package alice.util;

import java.net.URL;
import java.util.ArrayList;

import dalvik.system.DexClassLoader;

/**
 * 
 * @author Alessio Mercurio
 *
 * This class manages an instance of DexClassLoader. 
 * The DexClassloader doesn't allow you to change the classpath at runtime.
 * For this reason, whenever you change the classpath, a new instance of the classloader is created with the new classpath.
 *
 */

public class DynamicDexManager implements DynamicLoader
{
	private ArrayList<URL> listURLs = null;
	private String optimizedDirectory;
	private String libraryPath;
	private ClassLoader parent;
	private String dexPath;
	private DexClassLoader classLoader;
	
	public DynamicDexManager(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent)
	{
		
		setDexPath(dexPath);
		this.optimizedDirectory = optimizedDirectory;
		this.libraryPath = libraryPath;
		this.parent = parent;
		
		listURLs = new ArrayList<URL>();
		
		classLoader = new DexClassLoader(dexPath, optimizedDirectory, libraryPath, parent);
	}
	
	private void createClassLoader()
	{
		classLoader = new DexClassLoader( dexPath, optimizedDirectory, libraryPath, parent);
	}
	
	@Override
	public ClassLoader getClassLoader()
	{
		return classLoader;
	}
	
	private String createPathString()
	{
		String path;
		
		if(listURLs.isEmpty())
		{
			path = "";
		}
		else
		{
			path = listURLs.get(0).getPath();
			
			for (int i=1; i<listURLs.size(); i++)
			{
				path = path.concat(":" + listURLs.get(i).getPath());
			}
		}
		
		return path;
	}
	
	private void setDexPath(String dexPath)
	{
		this.dexPath = dexPath;
	}
	
	public void addURLs(URL[] urls)
	{
		if(urls == null)
			throw new IllegalArgumentException("Array URLs must not be null.");
		
		boolean OK = false;
		
		for (URL url : urls) 
		{
			if(!listURLs.contains(url))
			{
				OK = true;
				listURLs.add(url);
			}
		}
		
		if(OK)
		{
			setDexPath(createPathString());
			createClassLoader();
		}
	}

	public void removeAllURLs()
	{
		if (!listURLs.isEmpty())
			listURLs.clear();
		
		setDexPath(createPathString());
		createClassLoader();
	}

	public URL[] getURLs()
	{
		URL[] result = new URL[listURLs.size()];
		listURLs.toArray(result);
		return result;
	}

}
