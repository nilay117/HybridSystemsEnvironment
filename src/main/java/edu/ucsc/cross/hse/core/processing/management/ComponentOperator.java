package edu.ucsc.cross.hse.core.processing.management;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.component.categorization.CoreDataGroup;
import edu.ucsc.cross.hse.core.component.constructors.Behavior;
import edu.ucsc.cross.hse.core.component.constructors.Component;
import edu.ucsc.cross.hse.core.component.constructors.HybridSystem;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.object.accessors.Hierarchy;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class ComponentOperator extends Processor
{

	public ArrayList<Data> getAllData()
	{
		return allData;
	}

	private ArrayList<Data> allData;

	ComponentOperator(Environment processor)
	{
		super(processor);
		allData = new ArrayList<Data>();
		// this.environment = environment;
	}

	void initialize()
	{
		Hierarchy.load(getEnvironment().getComponents());
		for (Component component : getEnvironment().getComponents(true))
		{
			Component.setEnvironment(component, getEnvironment());
		}
		initializeData();
		initializeComponents();
		// initializeSimulated(false);
		// initializeSimulated(true);
		clearEmptyMaps();
	}

	private void initializeData()
	{
		allData.clear();
		for (Component component : getEnvironment().getComponents(true))
		{
			try
			{
				Data data = (Data) component;
				data.initializeValue();
				data.setInitialized(data, true);
				allData.add(data);
			} catch (Exception notData)
			{

			}
		}
	}

	protected void clearEmptyMaps()
	{
		clearEmptyMaps(getEnvironment().getComponents(true));
	}

	public void clearEmptyMaps(ArrayList<Component> allComponents)
	{
		for (Component component : allComponents)
		{
			// component.clearMapsIfEmpty();
			Component.setEnvironment(component, getEnvironment());
		}
	}

	public void initializeSimulated(boolean initialize_behavior)
	{
		initializeSimulated(initialize_behavior, getEnvironment().getComponents(true));
	}

	public void initializeSimulated(boolean initialize_behavior, ArrayList<Component> allComponents)
	{
		for (Component component : allComponents)
		{

			if (initialize_behavior)
			{
				// if
				// (component.getProperties().getClassification().equals(ComponentClassification.BEHAVIOR))
				if (component.getProperties().getBaseComponentClass().equals(Behavior.class))
				{
					if (!Component.isInitialized(component))
					{
						// if (!Data.getInitialized(component))
						{
							component.initialize();
							Component.setInitialized(component, true);
						}
					}
				}
			} else
			{

				// if
				// (!component.getProperties().getClassification().equals(ComponentClassification.BEHAVIOR))
				if (!component.getProperties().getBaseComponentClass().equals(Behavior.class))
				{
					if (!Component.isInitialized(component))
					{
						// if (!Data.getInitialized(component))
						{
							component.initialize();
							Component.setInitialized(component, true);
						}
					}
				}
			}

		}

	}

	public void initializeComponents()
	{
		for (Component component : getEnvironment().getComponents().getComponents(true))
		{

			if (!Component.isInitialized(component))
			{
				// if (!Data.getInitialized(component))
				{
					component.initialize();
					Component.setInitialized(component, true);
				}
			}

		}

	}

	public void performTasks()
	{
		performTasks(false);
	}

	public void performTasks(boolean jump_occurred)
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

	public void performAllJumps()
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

	public void storePrejumpValues()
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
