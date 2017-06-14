package edu.ucsc.cross.hse.core2.framework.component;

public class ComponentAddress
{

	private String environmentKey; // key that links the component to the global environment that it is contained in.  This keeps the component size smaller when being copied or saved, and allows for multiple environments to be running simultaneously

	public ComponentAddress()
	{
		environmentKey = null;
	}

	String getEnvironmentKey()
	{
		return environmentKey;
	}

	void setEnvironmentKey(String environmentKey)
	{
		this.environmentKey = environmentKey;
	}
}
