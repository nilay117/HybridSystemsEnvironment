package edu.ucsc.cross.hse.core.processing.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bs.commons.objects.access.FieldFinder;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentContent;
import edu.ucsc.cross.hse.core.framework.component.FullComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.models.HybridSystem;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.processing.data.DataHandler;

/*
 * This class controls all of the components so that they are setup correctly
 * and functional.
 */
@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class ComponentController extends ProcessingConnector
{

	HashMap<String, Data> initialData;

	ComponentController(CentralProcessor processor)
	{
		super(processor);
		initialData = new HashMap<String, Data>();
	}

	public void performAllTasks(boolean jump_occurred)
	{
		if (jump_occurred)
		{
			boolean jumpOccurred = false;
			// \
			// getData().storeData(getEnvironmentOperator().getEnvironmentHybridTime().getTime(),
			// (true && getSettings().getDataSettings().storeAtEveryJump));
			// this.getEnvironmentOperator().storeData();
			while (this.getComponentOperator(getEnv()).isJumpOccurring())
			{
				jumpOccurred = true;
				executeAllOccurringJumps();
			}
			if (jumpOccurred)
			{
				this.getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex();
			}
			// this.getComponentOperator(getEnv()).storeData();
			// getData().storeData(getEnvironmentOperator().getEnvironmentHybridTime().getTime()
			// + .000000001,
			// (true && getSettings().getDataSettings().storeAtEveryJump));
			// this.getComponents().setEnvTime(getEnvTime() + .000000001);
		} else
		{
			FullComponentOperator.getOperator(getEnv()).performTasks(jump_occurred);
		}
	}

	private void executeAllOccurringJumps()
	{

		ArrayList<Component> jumpComponents = FullComponentOperator.getOperator(getEnv()).jumpingComponents();
		storeRelavantPreJumpData(jumpComponents);
		getEnvironmentOperator().setJumpOccurring(true);
		// this.getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex();
		for (Component component : jumpComponents)
		{
			HybridSystem dynamics = ((HybridSystem) component);
			dynamics.jumpMap();
		}

		getEnvironmentOperator().setJumpOccurring(false);
	}

	private void storeRelavantPreJumpData(ArrayList<Component> jump_components)
	{

		for (Component component : jump_components)
		{
			for (State data : component.component().getContent().getObjects(State.class, true))
			{
				if (getDataOperator(data).isState())
				{
					getDataOperator(data).storePrejumpData();

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

	public DataHandler getDataHandler()
	{
		return this.getData();
	}

	public void reloadStoreStates()
	{
		this.getData().loadStoreStates();
	}

}
