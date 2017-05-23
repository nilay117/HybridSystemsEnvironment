package edu.ucsc.cross.hybrid.env.core.components;

import edu.ucsc.cross.hybrid.env.core.models.DynamicalModel;

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
	 * Constructor that assigns generic name
	 */
	public Behavior()
	{
		super("Behavior", Behavior.class);
	}

	public Boolean jumpOccurring(boolean jump_priority)
	{
		return Behavior.jumpOccurring(this, jump_priority);
	}

	public void updateStates(boolean jump_priority, boolean jump_occurring)
	{
		Behavior.updateStates(this, jump_priority, jump_occurring);
	}

	public static Boolean jumpOccurring(DynamicalModel dynamics, boolean jump_priority)
	{
		Boolean dom = null;
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

	public static void updateStates(DynamicalModel dynamics, boolean jump_priority, boolean jump_occurring)
	{
		if (!jump_priority)
		{
			if (dynamics.flowSet())
			{
				if (!jump_occurring)
				{
					dynamics.flowMap();
				}
			} else if (dynamics.jumpSet())
			{
				if (jump_occurring)
				{
					dynamics.jumpMap();
				}
			}
		} else
		{
			if (dynamics.jumpSet())
			{
				if (jump_occurring)
				{
					dynamics.jumpMap();
				}
			} else if (dynamics.flowSet())
			{
				if (!jump_occurring)
				{
					dynamics.flowMap();
				}

			}
		}
	}
}