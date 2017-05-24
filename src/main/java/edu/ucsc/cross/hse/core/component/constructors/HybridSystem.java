package edu.ucsc.cross.hse.core.component.constructors;

import edu.ucsc.cross.hse.core.component.models.DynamicalModel;

public abstract class HybridSystem extends Component
{

	/*
	 * Constructor that assigns generic name "Hybrid System"
	 */
	public HybridSystem()
	{
		super("Hybrid System", HybridSystem.class);
	}

	/*
	 * Constructor that allows the user to name the hybrid system
	 * 
	 * @param title - title of the hybrid system, ie Solid State Drive. This is
	 * different from the name of the device, ie Toshiba Q Series or Micron
	 * eMMC, since there can be many different names associated with a system
	 * model
	 */
	public HybridSystem(String title)
	{
		super(title, HybridSystem.class);
	}

	/*
	 * Performs all sub component tasks according to the current domain (jump,
	 * flow, or neither)
	 * 
	 * @param jump_occurring
	 */
	public void performTasks(boolean jump_occurring)
	{

		for (DynamicalModel localBehavior : getComponents(DynamicalModel.class, true))
		{
			try
			{
				boolean jumpOccurred = DynamicalModel.applyDynamics(localBehavior, true, jump_occurring);
				if (jumpOccurred)
				{
					this.getEnvironment().getEnvironmentTime().incrementJumpIndex();
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
	}

	/*
	 * Determines whether or not a jump is occurring in any component within the
	 * hybrid system
	 * 
	 * @return true if a jump is occurring, false otherwise
	 */
	public Boolean jumpOccurring()
	{
		Boolean jumpOccurred = false;
		for (DynamicalModel localBehavior : getComponents(DynamicalModel.class, true))
		{
			try
			{
				Boolean jumpOccurring = DynamicalModel.jumpOccurring(localBehavior, true);
				if (jumpOccurring != null)
				{
					try
					{
						jumpOccurred = jumpOccurred || jumpOccurring;
					} catch (Exception outOfDomain)
					{
						outOfDomain.printStackTrace();
					}
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
		return jumpOccurred;
	}

}
