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

	/*
	 * Flag indicating if this component has been initialized or not
	 */
	private Boolean initialized;
	/*
	 * Flag indicating if this component is simulated or not
	 */
	protected boolean simulated;

	/*
	 * Constructor
	 */
	public ComponentStatus()
	{
		simulated = true;
		initialized = false;

	}

	/*
	 * Boiler plate code to get and set the initialized / simulated flags
	 */
	Boolean getInitialized()
	{
		return initialized;
	}

	/*
	 * Set if the component has been initialized
	 */
	void setInitialized(Boolean initialized)
	{
		this.initialized = initialized;
	}

	/*
	 * Check if the component is simulated
	 */
	boolean isSimulated()
	{
		return simulated;
	}

	/*
	 * Set whether or not the component is stimulated
	 */
	void setSimulated(boolean simulated)
	{

		if (!initialized)
		{
			this.simulated = simulated;
		}
	}

}
