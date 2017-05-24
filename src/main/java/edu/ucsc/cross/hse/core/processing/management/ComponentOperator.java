package edu.ucsc.cross.hse.core.processing.management;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.component.categorization.CoreDataGroup;
import edu.ucsc.cross.hse.core.component.constructors.Component;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.component.models.DynamicalModel;
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
				if (Data.isInitialized(data))
				{
					data.initialize();
					Component.setInitialized(data, true);
					allData.add(data);
				}
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

			executeAllJumps();
			getData().storeData(getEnvironment().getEnvironmentTime().getTime(),
			(true && getSettings().getData().storeAtEveryJump));
		} else
		{
			getEnvironment().performTasks(jump_occurred);
		}
	}

	public void executeAllJumps()
	{
		ArrayList<Component> jumpComponents = getEnvironment().jumpingComponents();
		storeAllPreJumpData(jumpComponents);
		for (Component component : jumpComponents)
		{
			DynamicalModel dynamics = ((DynamicalModel) component);
			dynamics.jumpMap();
			this.getEnvironment().getEnvironmentTime().incrementJumpIndex();
		}
		if (getEnvironment().jumpOccurring())
		{
			executeAllJumps();
		}
	}

	public void storeAllPreJumpData(ArrayList<Component> jump_components)
	{

		for (Component component : jump_components)
		{
			for (Data data : component.getComponents(Data.class, true))
			{
				if (CoreDataGroup.STATE_ELEMENTS.contains(data))
				{
					Data.storePreJumpValue(data);
				}
			}
		}
	}

}
