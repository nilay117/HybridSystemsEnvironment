package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.rits.cloning.Cloner;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.environment.GlobalContentAdministrator;
import edu.ucsc.cross.hse.core.framework.models.HybridDynamicalModel;

/*
 * This class contains the methods that are used by processing modules to
 * operate the component. These methods are not intended for use outside of
 * processing, which is why they are not directly accessable from the component
 * itself. Any additional methods that should not be accessible by users should
 * be defined here. Use caution when using them as they can disrupt
 * functionality of the environment.
 */
public class ComponentAdministrator extends ComponentOperator
{

	public static Cloner cloner = new Cloner();

	// public Component component;

	protected static HashMap<Component, ComponentAdministrator> components = new HashMap<Component, ComponentAdministrator>();

	public ComponentAdministrator(Component component)
	{
		super(component);
		// this.component = component;
		components.put(component, this);
		// getConfigurer(component);
		// component.hierarchy.
	}

	public String getEnvironmentKey()
	{
		return component.getContents().getEnvironmentKey();
	}

	public ComponentStatus getStatus()
	{
		return component.getStatus();
	}

	public Boolean isInitialized()
	{
		return component.getStatus().getInitialized();
	}

	public boolean isSimulated()
	{
		return component.getStatus().isSimulated();
	}

	/*
	 * Determines whether or not a jump is occurring in any component within the
	 * hybrid system
	 * 
	 * @return true if a jump is occurring, false otherwise
	 */
	public ArrayList<Component> jumpingComponents()
	{
		ArrayList<Component> jumpComponents = new ArrayList<Component>();
		// System.out.println(getEnvironment().getMatchingComponents(Component.class,
		// true));
		for (Component localBehavior : component.getContents().getObjects(Component.class, true,
		HybridDynamicalModel.class))// ,
		// DynamicalModel.class))
		{
			try
			{
				Boolean jumpOccurring = HybridDynamicalModel.jumpOccurring((HybridDynamicalModel) localBehavior, true);
				if (jumpOccurring != null)
				{
					if (jumpOccurring)
					{
						if (!jumpComponents.contains(localBehavior)) // make
																		// sure
																		// the
																		// dynamical
																		// model
																		// has
																		// not
																		// been
																		// accounted
																		// for
																		// already
						{
							jumpComponents.add(localBehavior);
						}
					}
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
		return jumpComponents;
	}

	/*
	 * Performs all sub component tasks according to the current domain (jump,
	 * flow, or neither)
	 * 
	 * @param jump_occurring
	 */
	public void performTasks(boolean jump_occurring)
	{

		for (HybridDynamicalModel localBehavior : component.getContents().getObjects(HybridDynamicalModel.class, true))
		{
			try
			{

				boolean jumpOccurred = false;
				if (HybridDynamicalModel.jumpOccurring(localBehavior, true))
				{
					jumpOccurred = HybridDynamicalModel.applyDynamics(localBehavior, true, jump_occurring);
				} else if (HybridDynamicalModel.flowOccurring(localBehavior, true))
				{
					jumpOccurred = HybridDynamicalModel.applyDynamics(localBehavior, true, jump_occurring);
				} else
				{

				}
				if (jumpOccurred)
				{
					GlobalContentAdministrator.getContentAdministrator(getEnvironmentKey()).getEnvironmentHybridTime()
					.incrementJumpIndex();
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
	}

	public void protectedInitialize()
	{
		if (!component.getStatus().getInitialized())
		{
			component.initialize();
			component.getStatus().setInitialized(true);
		}
	}

	public void resetHierarchy()
	{
		component.getContents().setup();
	}

	public void setEnvironmentKey(String environment_key)
	{
		component.getContents().setEnvironmentKey(environment_key);
	}

	public void initializeContentMappings()
	{
		initializeContentMappings(null);
	}

	public void initializeContentMappings(Boolean initialize_components)
	{
		component.getContents().constructTree();
		// ComponentOrganizer.constructTree(component.getContents());
		if (initialize_components != null)
		{
			for (Component comp : component.getContents().getComponents(true))
			{
				ComponentAdministrator.getConfigurer(comp).setInitialized(initialize_components);
			}
		}
	}

	public static ComponentAdministrator getConfigurer(Component component)
	{
		if (components.containsKey(component))
		{
			return components.get(component);

		} else
		{

			ComponentAdministrator config = new ComponentAdministrator(component);
			components.put(component, config);
			return config;

		}
	}
}
