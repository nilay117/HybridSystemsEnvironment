package edu.ucsc.cross.hse.core.processing.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bs.commons.objects.access.FieldFinder;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOrganizer;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.models.HybridSystem;

/*
 * This class controls all of the components so that they are setup correctly
 * and functional.
 */
@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class ComponentAdministrator extends ProcessingElement
{

	ComponentAdministrator(CentralProcessor processor)
	{
		super(processor);

	}

	public void performAllTasks(boolean jump_occurred)
	{
		if (jump_occurred)
		{
			getData().storeData(getEnvironmentOperator().getEnvironmentHybridTime().getTime(),
			(true && getSettings().getDataSettings().storeAtEveryJump));

			executeAllOccurringJumps();
			getData().storeData(getEnvironmentOperator().getEnvironmentHybridTime().getTime() + .000000001,
			(true && getSettings().getDataSettings().storeAtEveryJump));
			this.getComponents().setEnvTime(getEnvTime() + .000000001);
		} else
		{
			ComponentOperator.getOperator(getEnv()).performTasks(jump_occurred);
		}
	}

	private void executeAllOccurringJumps()
	{

		ArrayList<Component> jumpComponents = ComponentOperator.getOperator(getEnv()).jumpingComponents();
		storeRelavantPreJumpData(jumpComponents);
		getEnvironmentOperator().setJumpOccurring(true);
		for (Component component : jumpComponents)
		{
			HybridSystem dynamics = ((HybridSystem) component);
			dynamics.jumpMap();
			this.getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex();
		}
		getEnvironmentOperator().setJumpOccurring(false);
	}

	private void storeRelavantPreJumpData(ArrayList<Component> jump_components)
	{

		for (Component component : jump_components)
		{
			for (Data data : component.getContents().getObjects(Data.class, true))
			{
				if (data.getActions().getDataProperties().changesContinuously())
				{
					getDataOperator(data).storePreJumpValue();

				}
			}
		}
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
	public static boolean applyDynamics(HybridSystem dynamics, boolean jump_priority, boolean global_jump_occurring)
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
	public static boolean flowOccurring(HybridSystem dynamics, boolean jump_priority)
	{
		boolean dom = false;
		if (!jump_priority)
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
	public static boolean jumpOccurring(HybridSystem dynamics, boolean jump_priority)
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

	public boolean outOfAllDomains()
	{
		return outOfAllDomains(getEnv());
	}

	public boolean outOfAllDomains(Component component)
	{
		return (!this.getComponentOperator(component).isJumpOccurring()
		&& !this.getComponentOperator(component).isFlowOccurring());
	}
}
