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
		super(title, ComponentClassification.HYBRID_SYSTEM);
	}

	/*
	 * Constructor that assigns generic name
	 */
	public HybridSystem()
	{
		super("Hybrid System", ComponentClassification.HYBRID_SYSTEM);
	}

	public void performTasks(boolean jump_occurred)
	{
		for (Component behaviorComponent : getComponents(ComponentClassification.BEHAVIOR, true))
		{
			Behavior localBehavior = (Behavior) behaviorComponent;
			try
			{

				localBehavior.updateStates(true, jump_occurred);

			} catch (Exception e)
			{
				e.printStackTrace();
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
		for (Component behaviorComponent : getComponents(ComponentClassification.BEHAVIOR, true))
		{
			Behavior localBehavior = (Behavior) behaviorComponent;
			try
			{
				Boolean jumpOccurring = localBehavior.jumpOccurring(true);
				if (jumpOccurring != null)
				{
					jumpOccurred = jumpOccurred || localBehavior.jumpOccurring(jumpOccurring);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return jumpOccurred;
	}

}
