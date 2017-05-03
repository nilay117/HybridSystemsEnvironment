package edu.ucsc.cross.hybrid.env.core.processor;

import java.util.ArrayList;

import edu.ucsc.cross.hybrid.env.core.components.Behavior;
import edu.ucsc.cross.hybrid.env.core.components.Data;
import edu.ucsc.cross.hybrid.env.core.components.DataSet;
import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;
import edu.ucsc.cross.hybrid.env.core.structure.Component;
import edu.ucsc.cross.hybrid.env.core.structure.DataCategory;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class ElementOperator extends ProcessorComponent
{

	public ArrayList<Data> getAllData()
	{
		return allData;
	}

	private ArrayList<Data> allData;

	ElementOperator(Environment processor)
	{
		super(processor);
		allData = new ArrayList<Data>();
		//this.environment = environment;
	}

	protected void initialize()
	{
		initializeSystems();
		initializeData();
	}

	protected void initializeSystems()
	{
		getEnvironment().load();
		for (Component component : getEnvironment().getComponents(true))
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
		for (Component component : getEnvironment().getComponents(true))
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
		clearEmptyMaps(getEnvironment().getAllComponents());
	}

	protected void clearEmptyMaps(ArrayList<Component> allComponents)
	{
		for (Component component : allComponents)
		{
			component.clearMapsIfEmpty();
			Component.setEnvironment(component, getEnvironment());
		}
	}

	protected void initializeSimulated(boolean initialize_behavior)
	{
		initializeSimulated(initialize_behavior, getEnvironment().getComponents(true));
	}

	protected void initializeSimulated(boolean initialize_behavior, ArrayList<Component> allComponents)
	{
		for (Component component : allComponents)
		{

			if (initialize_behavior)
			{
				//if (component.getProperties().getClassification().equals(ComponentClassification.BEHAVIOR))
				if (component.getProperties().getBaseComponentClass().equals(Behavior.class))
				{
					if (!component.isInitialized())
					{
						component.initialize();
						Component.setInitialized(component, true);
					}
				}
			} else
			{

				//if (!component.getProperties().getClassification().equals(ComponentClassification.BEHAVIOR))
				if (!component.getProperties().getBaseComponentClass().equals(Behavior.class))
				{
					if (!component.isInitialized())
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
			if (getSettings().getData().storePreJumpValue)
			{
				storePrejumpValues();
			}
		}
		for (HybridSystem componen : getEnvironment().getComponents(HybridSystem.class, true))//getEnvironment().getAllSystems())
		{
			componen.performTasks(jump_occurred);
		}

	}

	protected void storePrejumpValues()
	{
		for (Data data : allData)
		{
			if (DataCategory.STATE_ELEMENTS.containsObj(data))
			{
				Data.storePreJumpValue(data);
			}
		}
	}

	protected boolean jumpOccurring()
	{
		boolean jumpOccurred = false;
		for (HybridSystem componen : getEnvironment().getComponents(HybridSystem.class, true))
		{
			jumpOccurred = jumpOccurred || componen.jumpOccurring();
		}
		return jumpOccurred;
	}

}
