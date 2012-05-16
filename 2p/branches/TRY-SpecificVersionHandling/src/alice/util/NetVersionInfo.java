package alice.util;

public class NetVersionInfo extends VersionInfo {

	@Override
	public String getPlatform()
	{
		return ".NET";
	}

	@Override
	public String getSpecificVersion() 
	{
		return "@NETSpecific.version@";
	}

}
