package edu.ucsc.cross.hse.core.framework.component;

public class ComponentConfiguration
{

	private Boolean initialized; // flag indicating if component has been
	// initialized or not

	private boolean simulated; // flag indicating if contained data is
	// simulated
	// or not

	public ComponentConfiguration()
	{
		simulated = true;
		initialized = false;
	}

	Boolean getInitialized()
	{
		return initialized;
	}

	void setInitialized(Boolean initialized)
	{
		this.initialized = initialized;
	}

	boolean isSimulated()
	{
		return simulated;
	}

	void setSimulated(boolean simulated)
	{

		if (!initialized)
		{
			this.simulated = simulated;
		}
	}
}
