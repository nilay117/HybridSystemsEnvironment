package edu.ucsc.cross.hybrid.env.core.components;

import edu.ucsc.cross.hybrid.env.core.definitions.CoreComponent;
import edu.ucsc.cross.hybrid.env.core.models.DynamicalModel;

public abstract class Behavior extends Component implements DynamicalModel
{

	/*
	 * Constructor that allows the user to name the behavior
	 */
	public Behavior(String name)
	{
		super(name, CoreComponent.BEHAVIOR);
	}

	/*
	 * Constructor that assigns generic name
	 */
	public Behavior()
	{
		super("Behavior", CoreComponent.BEHAVIOR);
	}

	public Boolean jumpOccurring(boolean jump_priority)
	{
		Boolean dom = null;
		if (!jump_priority)
		{
			if (flowSet())
			{
				dom = false;
			} else if (jumpSet())
			{
				dom = true;
			}
		} else
		{
			if (jumpSet())
			{
				dom = true;
			} else if (flowSet())
			{
				dom = false;
			}
		}
		return dom;
	}

	public void updateStates(boolean jump_priority, boolean jump_occurring)
	{
		if (!jump_priority)
		{
			if (flowSet())
			{
				if (!jump_occurring)
				{
					flowMap();
				}
			} else if (jumpSet())
			{
				if (jump_occurring)
				{
					jumpMap();
				}
			}
		} else
		{
			if (jumpSet())
			{
				if (jump_occurring)
				{
					jumpMap();
				}
			} else if (flowSet())
			{
				if (!jump_occurring)
				{
					flowMap();
				}

			}
		}
	}
}