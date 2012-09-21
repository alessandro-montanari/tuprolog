package alice.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class DynamicURLClassLoader extends ClassLoader{
	private ArrayList<URL> listURLs = null;
	private Hashtable<String, Class<?>> classCache = new Hashtable<String, Class<?>>();
	
	public DynamicURLClassLoader()
	{
		super(DynamicURLClassLoader.class.getClassLoader());
	}
	
	public DynamicURLClassLoader(URL[] urls)
	{
		super(DynamicURLClassLoader.class.getClassLoader());
		listURLs = new ArrayList<URL>(Arrays.asList(urls));
	}
	
	public DynamicURLClassLoader(URL[] urls, ClassLoader parent)
	{
		super(parent);
		listURLs = new ArrayList<URL>(Arrays.asList(urls));
	}
	
	public Class<?> loadClass(String className) throws ClassNotFoundException {  
        return findClass(className);  
	}
	
	public Class<?> findClass(String className) throws ClassNotFoundException {  
        Class<?> result = null;  
  
        result = (Class<?>) classCache.get(className);  
        if (result != null)  
            return result;  
        try {
			return findSystemClass(className);
		} catch (ClassNotFoundException e) {
			
		} 
        className = className.replace(".", File.separator);
        for (URL aURL : listURLs) {
        	try {
        		InputStream is = null;
        		byte[] classByte = null;
        		JarFile jar = null;
        		JarEntry jarEntry = null;
        		if(aURL.toString().indexOf("/", aURL.toString().length() - 1) != -1)
        		{
        			aURL = new URL(aURL.toString() + className + ".class");
        			is = aURL.openConnection().getInputStream();
        		}
        		if(aURL.toString().endsWith(".jar"))
        		{
        			jar = new JarFile(new File(aURL.toURI()));
            		jarEntry = jar.getJarEntry(className + ".class");
            		is = jar.getInputStream(jarEntry);
        		}
        		classByte = getClassData(is);
//                System.out.println(bytesToHex(classByte));
                try {
                	result = defineClass(className, classByte, 0, classByte.length, null);  
            		classCache.put(className, result);
            		
				} catch (SecurityException e) {
					result = super.loadClass(className);
				}
                if(aURL.toString().endsWith(".jar"))
                	jar.close();                	
                return result;  
        	} catch (Exception e) {

        	}
        }
        throw new ClassNotFoundException();
    }  
	
	private byte[] getClassData(InputStream is) throws IOException
	{
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		int nextValue= is.read();  
        while (-1 != nextValue) {  
            byteStream.write(nextValue);  
            nextValue = is.read();  
        }
        is.close();
        return byteStream.toByteArray();
        
        
	}
	private static String bytesToHex(byte[] bytes) {
	    final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public void addURLs(URL[] urls) throws MalformedURLException
	{
		for (URL url : urls) {
			if(!listURLs.contains(url))
				listURLs.add(url);
		}
	}
	
	public void removeURL(URL url) throws IllegalArgumentException
	{
		if(!listURLs.contains(url))
			throw new IllegalArgumentException("URL: " + url + "not found.");
		listURLs.remove(url);
	}
	
	public void removeAllURLs()
	{
		if(!listURLs.isEmpty())
			listURLs.clear();
	}
	
	public URL[] getURLs()
	{
		URL[] result = new URL[listURLs.size()];
		listURLs.toArray(result);
		return result;
	}
	
	public Class<?>[] getLoadedClasses()
	{
		Class<?>[] result = new Class<?>[classCache.size()];
		int i = 0;
		for (Class<?> aClass : classCache.values()) {
			result[i] = aClass;
		}
		return result;
	}
	
	public void clearCache()
	{
		classCache.clear();
	}
	
	public void removeClassCacheEntry(String className)
	{
		classCache.remove(className);
	}
	
	public void setClassCacheEntry(Class<?> cls)
	{
		if(classCache.contains(cls))
			classCache.remove(cls.getName());
		classCache.put(cls.getName(), cls);
	}
}
