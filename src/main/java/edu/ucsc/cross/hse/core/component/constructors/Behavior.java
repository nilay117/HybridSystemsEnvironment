package edu.ucsc.cross.hse.core.component.constructors;

import edu.ucsc.cross.hse.core.component.models.DynamicalModel;

public abstract class Behavior extends Component implements DynamicalModel
{

	/*
	 * Constructor that allows the user to name the behavior
	 */
	public Behavior(String name)
	{
		super(name, Behavior.class);
	}

	/*
	 * Determine if a jump is occurring
	 * 
	 * @param jump_priority - flag indicating if jumps have priority over flows
	 * to determine proper mapping to apply if system state is in both flow and
	 * jump map
	 * 
	 * @return true if jump is occurring, false otherwise
	 */
	public boolean jumpOccurring(boolean jump_priority)
	{
		return DynamicalModel.jumpOccurring(this, jump_priority);
	}

	/*
	 * Determine if a jump is occurring
	 * 
	 * @param jump_priority - flag indicating if jumps have priority over flows
	 * to determine proper mapping to apply if system state is in both flow and
	 * jump map
	 * 
	 * @return true if jump is occurring, false otherwise
	 */
	public boolean flowOccurring(boolean jump_priority)
	{
		return DynamicalModel.flowOccurring(this, jump_priority);
	}

}