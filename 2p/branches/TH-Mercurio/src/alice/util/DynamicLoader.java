package alice.util;

import java.net.MalformedURLException;
import java.net.URL;

public interface DynamicLoader
{	
	public void addURLs(URL[] urls) throws MalformedURLException;
	public void removeAllURLs();
	public URL[] getURLs();

	public ClassLoader getClassLoader();
}
