package edu.ucsc.cross.hse.core.framework.component;

import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataFactory;

public class ComponentStatus
{

	private Boolean initialized; // flag indicating if component has been
	// initialized or not

	protected boolean simulated; // flag indicating if contained data is
	// simulated
	// or not

	public ComponentStatus()
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
