package alice.util;

public class JavaVersionInfo extends VersionInfo {

	@Override
	public String getPlatform() 
	{
		return "Java";
	}

	@Override
	public String getSpecificVersion() 
	{
		return "@JavaSpecific.version@";
	}

}
