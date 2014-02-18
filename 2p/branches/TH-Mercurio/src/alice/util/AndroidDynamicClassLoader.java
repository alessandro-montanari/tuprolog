package alice.util;

import java.net.URL;

import dalvik.system.DexClassLoader;

public class AndroidDynamicClassLoader extends AbstractDynamicClassLoader
{
	private String dexPath;
	private DexClassLoader classLoader;
	
	public AndroidDynamicClassLoader()
	{
		super();
	}
	
	public AndroidDynamicClassLoader(URL[] urls)
	{
		super(urls);
	}
	
	public AndroidDynamicClassLoader(URL[] urls, ClassLoader parent)
	{
		super(urls, parent);
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

	public Class<?> findClass(String className) throws ClassNotFoundException
	{		
		setDexPath(createPathString());
		
		classLoader = new DexClassLoader(dexPath, "/data/data/alice.tuprologx.android/app_dex", null, getParent());
		
		return classLoader.loadClass(className);
	}

	
}
