package edu.ucsc.cross.hybrid.env.core.components;

import edu.ucsc.cross.hybrid.env.core.structure.Component;
import edu.ucsc.cross.hybrid.env.core.structure.ComponentClassification;

public abstract class HybridSystem extends Component
{

	/*
	 * Constructor that allows the user to name the hybrid system
	 */
	public HybridSystem(String title)
	{
		super(title, HybridSystem.class, ComponentClassification.HYBRID_SYSTEM);
	}

	/*
	 * Constructor that assigns generic name
	 */
	public HybridSystem()
	{
		super("Hybrid System", HybridSystem.class, ComponentClassification.HYBRID_SYSTEM);
	}

	public void performTasks(boolean jump_occurred)
	{

		for (Component behaviorComponent : getComponents(Behavior.class, true))
		{
			Behavior localBehavior = (Behavior) behaviorComponent;
			try
			{
				localBehavior.updateStates(true, jump_occurred);
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
		if (jump_occurred && jumpOccurring())
		{
			performTasks(true);
		}
	}

	public Boolean jumpOccurring()
	{
		Boolean jumpOccurred = false;
		for (Component behaviorComponent : getComponents(Behavior.class, true))
		{
			Behavior localBehavior = (Behavior) behaviorComponent;
			try
			{
				Boolean jumpOccurring = localBehavior.jumpOccurring(true);
				if (jumpOccurring != null)
				{
					try
					{
						jumpOccurred = jumpOccurred || jumpOccurring;
					} catch (Exception outOfDomain)
					{
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
