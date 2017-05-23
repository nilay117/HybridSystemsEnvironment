package edu.ucsc.cross.hse.core.component.models;

/*
 * This interface defines a classical hybrid dynamical model.
 */
public interface DynamicalModel
{

	/*
	 * The jump set is the set of states that define the discrete domain, which
	 * is where the system exhibits discrete discrete dynamics. This function
	 * returns true if the state is in the jump set, or false if not
	 */
	public boolean jumpSet();

	/*
	 * The flow set is the set of states that define the continuous domain,
	 * which is where the system exhibits discrete continuous dynamics. This
	 * function returns true if the state is in the flow set, or false if not
	 */
	public boolean flowSet();

	/*
	 * The flow map is a mapping that determines the continuous dynamics of the
	 * state. This function defines how to compute the derivatives of the state
	 * elements
	 */
	public void flowMap();

	/*
	 * The jump map is a mapping that determines the discrete changes that occur
	 * to the state variable. This function defines how the state values change
	 * discretely
	 */
	public void jumpMap();

	/*
	 * Determine if a jump is occurring for some dynamical model
	 * 
	 * @param dynamics - dynamical model to evaluate
	 * 
	 * @param jump_priority - flag indicating if jumps have priority over flows
	 * to determine proper mapping to apply if system state is in both flow and
	 * jump map
	 * 
	 * @return true if jump is occurring, false otherwise
	 */
	static boolean jumpOccurring(DynamicalModel dynamics, boolean jump_priority)
	{
		boolean dom = false;
		if (!jump_priority)
		{
			if (dynamics.flowSet())
			{
				dom = false;
			} else if (dynamics.jumpSet())
			{
				dom = true;
			}
		} else
		{
			if (dynamics.jumpSet())
			{
				dom = true;
			} else if (dynamics.flowSet())
			{
				dom = false;
			}
		}
		return dom;
	}

	/*
	 * Determine if a jump is occurring for some dynamical model
	 * 
	 * @param dynamics - dynamical model to evaluate
	 * 
	 * @param jump_priority - flag indicating if jumps have priority over flows
	 * to determine proper mapping to apply if system state is in both flow and
	 * jump map
	 * 
	 * @return true if jump is occurring, false otherwise
	 */
	static boolean flowOccurring(DynamicalModel dynamics, boolean jump_priority)
	{
		boolean dom = false;
		if (jump_priority)
		{
			if (dynamics.flowSet())
			{
				dom = true;
			}
		} else
		{
			if (dynamics.jumpSet())
			{
				dom = false;
			} else if (dynamics.flowSet())
			{
				dom = true;
			}
		}
		return dom;
	}

	/*
	 * Apply the defined dynamics depending on whether a jump is occurring
	 * globally or not
	 * 
	 * @param dynamics - dynamical model defining the dynamics
	 * 
	 * @param jump_priority - flag indicating if jumps have priority over flows
	 * to determine proper mapping to apply if system state is in both flow and
	 * jump map
	 * 
	 * @param jump_occurring - flag indicating if a global jump is occurring.
	 * This happens when a jump occurs for any system within the environment
	 */
	static boolean applyDynamics(DynamicalModel dynamics, boolean jump_priority, boolean global_jump_occurring)
	{
		boolean jumpOccurred = false;
		if (!jump_priority)
		{
			if (dynamics.flowSet())
			{
				if (!global_jump_occurring)
				{
					dynamics.flowMap();
				}
			} else if (dynamics.jumpSet())
			{
				if (global_jump_occurring)
				{
					dynamics.jumpMap();
					jumpOccurred = true;
				}
			}
		} else
		{
			if (dynamics.jumpSet())
			{
				if (global_jump_occurring)
				{
					dynamics.jumpMap();
					jumpOccurred = true;
				}
			} else if (dynamics.flowSet())
			{
				if (!global_jump_occurring)
				{
					dynamics.flowMap();
				}

			}
		}
		return jumpOccurred;
	}

}
