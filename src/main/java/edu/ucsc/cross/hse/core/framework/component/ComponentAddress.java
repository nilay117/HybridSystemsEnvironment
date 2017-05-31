package edu.ucsc.cross.hse.core.framework.component;

public class ComponentAddress
{

	private String environmentKey;

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
