package edu.ucsc.cross.hybrid.env.core.components;

import edu.ucsc.cross.hybrid.env.core.structure.Component;
import edu.ucsc.cross.hybrid.env.core.structure.ComponentClassification;
import edu.ucsc.cross.hybrid.env.core.structure.DynamicalModel;

public abstract class Behavior extends Component implements DynamicalModel
{

	/*
	 * Constructor that allows the user to name the behavior
	 */
	public Behavior(String name)
	{
		super(name, Behavior.class, ComponentClassification.BEHAVIOR);
	}

	/*
	 * Constructor that assigns generic name
	 */
	public Behavior()
	{
		super("Behavior", Behavior.class, ComponentClassification.BEHAVIOR);
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