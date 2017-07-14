package edu.ucsc.cross.hse.core.processing.execution;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentWorker;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.models.HybridSystem;
import edu.ucsc.cross.hse.core.processing.data.DataHandler;

/*
 * This class controls all of the components so that they are setup correctly
 * and functional.
 */
public class ComponentDirector extends ProcessorAccess
{

	/*
	 * Constructor that links the processor and components
	 */
	ComponentDirector(CentralProcessor processor)
	{
		super(processor);
	}

	/*
	 * Perform jumps or flows depending on the state of the global system
	 * 
	 * @param jump_occurred - flag indicating that the integrator is paused and
	 * jumps can be executed
	 */
	public void performAllTasks(boolean jump_occurred)
	{
		if (jump_occurred)
		{

			while (this.getComponentOperator(getEnv()).isJumpOccurring())
			{
				getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex();

				executeAllOccurringJumps();

				getData().storeData(this.getEnvironmentOperator().getEnvironmentHybridTime().getTime(), true);

			}
		} else
		{
			ComponentWorker.getOperator(getEnv()).performTasks(jump_occurred);
		}
	}

	/*
	 * Execute jumps in all components where a jump is occurring
	 */
	private void executeAllOccurringJumps()
	{
		ArrayList<Component> jumpComponents = getEnv().component().getContent().getComponents(true);// ,
																									// classes).jumpingComponents();
		storeRelavantPreJumpData(jumpComponents);
		getEnvironmentOperator().setJumpOccurring(true);
		for (Component component : jumpComponents)
		{
			try
			{
				HybridSystem dynamics = ((HybridSystem) component);

				applyDynamics(dynamics, true, true);

			} catch (Exception e)
			{

			}
		}

		getEnvironmentOperator().setJumpOccurring(false);
	}

	/*
	 * Store all state values before the jump occurs to make sure that the
	 * correct value is accessed
	 * 
	 * @param jump_components - components where a jump is occurring
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void storeRelavantPreJumpData(ArrayList<Component> jump_components)
	{

		for (Data data : getEnv().component().getContent().getData(true))// component.component().getContent().getData(true))
		{
			if (getDataOperator(data).isDataStored())
			// if (getDataOperator(data).isState())
			{
				getDataOperator(data).storePrejumpData();

			}
		}
	}

	/*
	 * Store all state values before the jump occurs to make sure that the
	 * correct value is accessed
	 * 
	 * @param jump_components - components where a jump is occurring
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void clearPreJumpData()
	{

		for (Data data : getEnv().component().getContent().getData(true))
		{
			{
				getDataOperator(data).clearPrejumpData();

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

	/*
	 * Get the data handling processor module
	 */
	public DataHandler getDataHandler()
	{
		return this.getData();
	}

	/*
	 * Reload mapping of states that are stored
	 */
	public void reloadStoreStates()
	{
		this.getData().loadStoreStates();
	}

}
