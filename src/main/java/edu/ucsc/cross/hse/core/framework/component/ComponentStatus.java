package edu.ucsc.cross.hse.core.framework.component;

/*
 * This class contains information regarding the status of the component that is
 * needed in processing modules, but not in regular use. Any fields that should
 * not be directly accessable from the component should be located here. Access
 * to the fields can then be implemented in a safe way as either actions or
 * operations depending on the intended use.
 */
public class ComponentStatus
{

	private Boolean initialized; // flag indicating if component has been
	// initialized or not

	protected boolean simulated; // flag indicating if contained data is
	// simulated
	// or not

	protected String address; // address of component

	/*
	 * Constructor
	 */
	public ComponentStatus()
	{
		simulated = true;
		initialized = false;
		address = null;// this.toString();
	}

	/*
	 * Boiler plate code to get and set the initialized / simulated flags
	 */
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
