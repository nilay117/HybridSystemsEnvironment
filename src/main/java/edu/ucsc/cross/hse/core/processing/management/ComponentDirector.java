package edu.ucsc.cross.hse.core.processing.management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bs.commons.objects.access.FieldFinder;
import edu.ucsc.cross.hse.core.component.categorization.CoreDataGroup;
import edu.ucsc.cross.hse.core.component.constructors.DataSet;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.component.foundation.Component;
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

	public void executeAllOccurringJumps()
	{

		ArrayList<Component> jumpComponents = getEnvironment().jumpingComponents();
		storeRelavantPreJumpData(jumpComponents);
		getEnvironment().setJumpOccurring(true);
		for (Component component : jumpComponents)
		{
			DynamicalModel dynamics = ((DynamicalModel) component);
			dynamics.jumpMap();
			this.getEnvironment().getEnvironmentTime().incrementJumpIndex();
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
					dataOps(data).storePreJumpValue();

				}
			}
		}
	}

	void prepareComponents()
	{
		Hierarchy.constructTree(getEnvironment().getHierarchy());
		linkEnvironment();
		initializeComponents(Data.class);
		initializeComponents(DataSet.class);
		initializeComponents();
		linkEnvironment();
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
				compOps(component).protectedInitialize();
			}
		}

	}

	public void linkEnvironment()
	{
		for (Component component : getEnvironment().getComponents(true))
		{
			compOps(component).setEnvironment(getEnvironment().toString());
		}
	}

}
