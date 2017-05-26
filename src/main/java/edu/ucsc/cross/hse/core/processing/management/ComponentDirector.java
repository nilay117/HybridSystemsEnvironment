package edu.ucsc.cross.hse.core.processing.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bs.commons.objects.access.FieldFinder;
import edu.ucsc.cross.hse.core.component.categorization.CoreDataGroup;
import edu.ucsc.cross.hse.core.component.constructors.Component;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.component.models.DynamicalModel;
import edu.ucsc.cross.hse.core.object.accessors.Hierarchy;

/*
 * This class controls all of the components so that they are setup correctly
 * and functional.
 */
@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class ComponentDirector extends ProcessorAccess
{

	ComponentDirector(Environment processor)
	{
		super(processor);

	}

	public void performAllTasks()
	{
		performAllTasks(false);
	}

	public void performAllTasks(boolean jump_occurred)
	{
		if (jump_occurred)
		{
			getData().storeData(getEnvironment().getEnvironmentTime().getTime() - .000001,
			(true && getSettings().getData().storeAtEveryJump));

			executeAllOccurringJumps();
			getData().storeData(getEnvironment().getEnvironmentTime().getTime(),
			(true && getSettings().getData().storeAtEveryJump));
		} else
		{
			getEnvironment().performTasks(jump_occurred);
		}
	}

	public void executeAllOccurringJumpsUnprotected()
	{
		ArrayList<Component> jumpComponents = getEnvironment().jumpingComponents();
		storeRelavantPreJumpData(jumpComponents);
		for (Component component : jumpComponents)
		{
			DynamicalModel dynamics = ((DynamicalModel) component);
			dynamics.jumpMap();
			this.getEnvironment().getEnvironmentTime().incrementJumpIndex();
		}
		if (getEnvironment().jumpOccurring())
		{
			// executeAllOccurringJumps();
		}
	}

	public void executeAllOccurringJumps()
	{
		getEnvironment().setJumpOccurring(true);
		ArrayList<Component> jumpComponents = getEnvironment().jumpingComponents();
		storeRelavantPreJumpData(jumpComponents);
		for (Component component : jumpComponents)
		{
			DynamicalModel dynamics = ((DynamicalModel) component);
			dynamics.jumpMap();
			this.getEnvironment().getEnvironmentTime().incrementJumpIndex();
		}
		if (getEnvironment().jumpOccurring())
		{
			// executeAllOccurringJumps();
		}
		getEnvironment().setJumpOccurring(false);
	}

	public void storeRelavantPreJumpData(ArrayList<Component> jump_components)
	{

		for (Component component : jump_components)
		{
			for (Data data : component.getComponents(Data.class, true))
			{
				if (CoreDataGroup.STATE_ELEMENTS.contains(data))
				{
					// System.out.println(data.get().toString());
					Data.storePreJumpValue(data);
				}
			}
		}
	}

	void prepareComponents()
	{
		Hierarchy.constructTree(getEnvironment().getHierarchy());

		linkEnvironment();
		// initializeComponents();
		initializeComponents(Data.class);
		initializeComponents();
		linkEnvironment();
		// getEnvironment().sy
	}

	public void initializeComponents(Class<?>... components_to_initialize)
	{
		List<Class<?>> initializeList = Arrays.asList(components_to_initialize);
		for (Component component : getEnvironment().getComponents(true))
		{
			boolean initialize = initializeList.size() == 0;
			for (Class<?> checkClass : initializeList)
			{
				initialize = initialize || FieldFinder.containsSuper(component, checkClass);
			}
			if (initialize)
			{
				if (!Component.isInitialized(component))
				{
					component.initialize();
					Component.setInitialized(component, true);

				}
			}
		}

	}

	protected void linkEnvironment()
	{
		linkEnvironment(getEnvironment().getComponents(true));
	}

	public void linkEnvironment(ArrayList<Component> allComponents)
	{
		for (Component component : allComponents)
		{
			component.getConfigurer().setEnvironment(getEnvironment().environmentKey);
		}
	}

}
