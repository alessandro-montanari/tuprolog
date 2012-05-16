package alice.util;

public class VersionInfoFactory 
{
	public static VersionInfo getVersionInfo()
	{
		String vmName = System.getProperty("java.vm.name");
		if(vmName.contains("Java")) //"Java HotSpot(TM) Client VM"
			return new JavaVersionInfo();
		else if(vmName.equals("IKVM.NET"))
			return new NetVersionInfo();
		else if(vmName.equals("Dalvik"))
			return new AndroidVersionInfo();
		else
			throw new RuntimeException();
	}
}
