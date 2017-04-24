package edu.ucsc.cross.hybrid.env.core.processor;

import java.util.ArrayList;

import edu.ucsc.cross.hybrid.env.core.components.Data;
import edu.ucsc.cross.hybrid.env.core.components.DataSet;
import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;
import edu.ucsc.cross.hybrid.env.core.structure.Component;
import edu.ucsc.cross.hybrid.env.core.structure.ComponentClassification;
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
		getEnvironment().getAllComponents().clear();
		for (HybridSystem sysCom : getEnvironment().getAllSystems())
		{
			sysCom.loadAllComponents();
			for (Component component : sysCom.getAllComponents(true))
			{
				if (!getEnvironment().getAllComponents().contains(component))
				{
					getEnvironment().getAllComponents().add(component);
					//Component.setEnvironment(component, getEnvironment());
					//					if (component.getProperties().getClassification().equals(ElementClassification.DATA_SET))
					//					{
					//						Elements elements = ((Elements) component);
					//						elements.initializeElements();
					//					}
				}
			}
		}
		initializeSimulated(false);
		initializeSimulated(true);
		clearEmptyMaps();
	}

	private void initializeData()
	{
		allData.clear();
		for (Component component : getEnvironment().getAllComponents())
		{
			try
			{
				Data data = (Data) component;
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
		initializeSimulated(initialize_behavior, getEnvironment().getAllComponents());
	}

	protected void initializeSimulated(boolean initialize_behavior, ArrayList<Component> allComponents)
	{
		for (Component component : allComponents)
		{

			if (initialize_behavior)
			{
				if (component.getProperties().getClassification().equals(ComponentClassification.BEHAVIOR))
				{
					if (!component.isInitialized())
					{
						component.initialize();
						Component.setInitialized(component, true);
					}
				}
			} else
			{

				if (!component.getProperties().getClassification().equals(ComponentClassification.BEHAVIOR))
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
		for (HybridSystem componen : getEnvironment().getAllSystems())
		{
			componen.performTasks(jumpOccurring());
		}
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
		for (HybridSystem componen : getEnvironment().getAllSystems())
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
		for (HybridSystem componen : getEnvironment().getAllSystems())
		{
			jumpOccurred = jumpOccurred || componen.jumpOccurring();
		}
		return jumpOccurred;
	}

	//	protected void storePrejumpValues()
	//	{
	//		boolean jumpOccurred = false;
	//		for (Component componen : getEnvironment().get
	//		{
	//		
	//			jumpOccurred = jumpOccurred || componen.jumpOccurring();
	//		}
	//		return jumpOccurred;
	//	}

}
