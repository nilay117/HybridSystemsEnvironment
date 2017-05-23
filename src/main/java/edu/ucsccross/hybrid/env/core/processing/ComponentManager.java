package edu.ucsc.cross.hybrid.env.core.processing;

import java.util.ArrayList;

import edu.ucsc.cross.hybrid.env.core.constructors.Behavior;
import edu.ucsc.cross.hybrid.env.core.constructors.Component;
import edu.ucsc.cross.hybrid.env.core.constructors.Data;
import edu.ucsc.cross.hybrid.env.core.constructors.DataSet;
import edu.ucsc.cross.hybrid.env.core.constructors.HybridSystem;
import edu.ucsc.cross.hybrid.env.core.definitions.CoreDataGroup;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class ComponentManager extends Processor
{

	public ArrayList<Data> getAllData()
	{
		return allData;
	}

	private ArrayList<Data> allData;

	ComponentManager(Environment processor)
	{
		super(processor);
		allData = new ArrayList<Data>();
		// this.environment = environment;
	}

	protected void initialize()
	{
		initializeSystems();
		initializeData();
	}

	protected void initializeSystems()
	{
		getEnvironment().load();
		for (Component component : getEnvironment().getAllComponents(true))
		{
			Component.setEnvironment(component, getEnvironment());
		}
		initializeSimulated(false);
		initializeSimulated(true);
		clearEmptyMaps();
	}

	private void initializeData()
	{
		allData.clear();
		for (Component component : getEnvironment().getAllComponents(true))
		{
			try
			{
				Data data = (Data) component;
				data.initializeValue();
				allData.add(data);
			} catch (Exception notData)
			{

			}
		}
	}

	protected void clearEmptyMaps()
	{
		clearEmptyMaps(getEnvironment().getAllComponents(true));
	}

	protected void clearEmptyMaps(ArrayList<Component> allComponents)
	{
		for (Component component : allComponents)
		{
			// component.clearMapsIfEmpty();
			Component.setEnvironment(component, getEnvironment());
		}
	}

	protected void initializeSimulated(boolean initialize_behavior)
	{
		initializeSimulated(initialize_behavior, getEnvironment().getAllComponents(true));
	}

	protected void initializeSimulated(boolean initialize_behavior, ArrayList<Component> allComponents)
	{
		for (Component component : allComponents)
		{

			if (initialize_behavior)
			{
				// if
				// (component.getProperties().getClassification().equals(ComponentClassification.BEHAVIOR))
				if (component.getProperties().getBaseComponentClass().equals(Behavior.class))
				{
					if (!Data.getInitialized(component))
					{
						component.initialize();
						Component.setInitialized(component, true);
					}
				}
			} else
			{

				// if
				// (!component.getProperties().getClassification().equals(ComponentClassification.BEHAVIOR))
				if (!component.getProperties().getBaseComponentClass().equals(Behavior.class))
				{
					if (!Data.getInitialized(component))
					{
						component.initialize();
						Component.setInitialized(component, true);
					}
				}
			}
			try
			{
				DataSet elements = ((DataSet) component);
				elements.initializeElements();
			} catch (Exception nonElements)
			{

			}
		}

	}

	protected void performTasks()
	{
		performTasks(false);
	}

	protected void performTasks(boolean jump_occurred)
	{
		if (jump_occurred)
		{
			getData().storeData(getEnvironment().getEnvironmentTime().getTime() - .000001,
			(true && getSettings().getData().storeAtEveryJump));
			if (getSettings().getData().storePreJumpValue)
			{
				storePrejumpValues();
			}
			performAllJumps();
			getData().storeData(getEnvironment().getEnvironmentTime().getTime(),
			(true && getSettings().getData().storeAtEveryJump));
		} else
		{
			getEnvironment().performTasks(jump_occurred);
			// for (HybridSystem componen :
			// getEnvironment().getComponents(HybridSystem.class,
			// true))//getEnvironment().getAllSystems())
			// {
			// componen.performTasks(jump_occurred);
			// }
		}
	}

	protected void performAllJumps()
	{
		while (getEnvironment().jumpOccurring())
		// while (getComponents().jumpOccurring())
		{
			getEnvironment().performTasks(true);
			for (HybridSystem componen : getEnvironment().getComponents(HybridSystem.class, true))// getEnvironment().getAllSystems())
			{
				componen.performTasks(true);
			}
		}

	}

	protected void storePrejumpValues()
	{
		for (Data data : allData)
		{
			if (CoreDataGroup.STATE_ELEMENTS.contains(data))
			{
				Data.storePreJumpValue(data);
			}
		}
	}

}
