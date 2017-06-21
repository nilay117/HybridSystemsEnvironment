package edu.ucsc.cross.hse.core.processing.execution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bs.commons.objects.access.FieldFinder;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentCoordinator;
import edu.ucsc.cross.hse.core.framework.component.ComponentAdministrator;
import edu.ucsc.cross.hse.core.framework.data.CoreDataGroup;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.models.HybridDynamicalModel;

/*
 * This class controls all of the components so that they are setup correctly
 * and functional.
 */
@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class ComponentDirector extends ProcessorAccess
{

	ComponentDirector(Processor processor)
	{
		super(processor);

	}

	public void performAllTasks(boolean jump_occurred)
	{
		if (jump_occurred)
		{
			getData().storeData(getEnvironmentOperator().getEnvironmentHybridTime().getTime() - .000001,
			(true && getSettings().getData().storeAtEveryJump));

			executeAllOccurringJumps();
			getData().storeData(getEnvironmentOperator().getEnvironmentHybridTime().getTime(),
			(true && getSettings().getData().storeAtEveryJump));
		} else
		{
			ComponentAdministrator.getConfigurer(getEnvironment()).performTasks(jump_occurred);
		}
	}

	private void executeAllOccurringJumps()
	{

		ArrayList<Component> jumpComponents = ComponentAdministrator.getConfigurer(getEnvironment()).jumpingComponents();
		storeRelavantPreJumpData(jumpComponents);
		getEnvironmentOperator().setJumpOccurring(true);
		for (Component component : jumpComponents)
		{
			HybridDynamicalModel dynamics = ((HybridDynamicalModel) component);
			dynamics.jumpMap();
			this.getEnvironmentOperator().getEnvironmentHybridTime().incrementJumpIndex();
		}
		getEnvironmentOperator().setJumpOccurring(false);
	}

	private void storeRelavantPreJumpData(ArrayList<Component> jump_components)
	{

		for (Component component : jump_components)
		{
			for (Data data : component.getHierarchy().getComponents(Data.class, true))
			{
				if (CoreDataGroup.STATE_ELEMENTS.contains(data))
				{
					getDataOperator(data).storePreJumpValue();

				}
			}
		}
	}

	void prepareComponents()
	{
		ComponentCoordinator.constructTree(getEnvironment().getHierarchy());
		linkEnvironment();
		initializeComponents(Data.class);
		// initializeComponents(DataSet.class);
		initializeComponents();
		linkEnvironment();
	}

	private void initializeComponents(Class<?>... components_to_initialize)
	{
		List<Class<?>> initializeList = Arrays.asList(components_to_initialize);
		for (Component component : getEnvironment().getHierarchy().getComponents(true))
		{
			boolean initialize = initializeList.size() == 0;
			for (Class<?> checkClass : initializeList)
			{
				initialize = initialize || FieldFinder.containsSuper(component, checkClass);
			}
			if (initialize)
			{
				getComponentOperator(component).protectedInitialize();
			}
		}

	}

	private void linkEnvironment()
	{
		for (Component component : getEnvironment().getHierarchy().getComponents(true))
		{
			getComponentOperator(component).setEnvironmentKey(getEnvironment().toString());
		}
	}

}
