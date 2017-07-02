package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.environment.ContentOperator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.framework.models.HybridSystem;
import edu.ucsc.cross.hse.core.processing.data.DataHandler;
import edu.ucsc.cross.hse.core.processing.execution.ComponentAdministrator;

/*
 * This class contains the methods that are used by processing modules to
 * operate the component. These methods are not intended for use outside of
 * processing, which is why they are not directly accessable from the component
 * itself. Any additional methods that should not be accessible by users should
 * be defined here. Use caution when using them as they can disrupt
 * functionality of the environment.
 */
public class ComponentOperator extends ComponentWorker
{

	// public Component component;
	private Component configuration;

	protected static HashMap<Component, ComponentOperator> components = new HashMap<Component, ComponentOperator>();

	public ComponentOperator(Component component)
	{
		super(component);
		// this.component = component;
		components.put(component, this);
		configuration = (Component) ObjectCloner.xmlClone(component);
		// getConfigurer(component);
		// component.hierarchy.
	}

	public String getEnvironmentKey()
	{
		return component.getContents().getEnvironmentKey();
	}

	/*
	 * Accesses the current status of the component. This is for the processor
	 * and is not intended for users.
	 */
	public ComponentStatus getStatus()
	{
		return component.status;
	}

	public Boolean isInitialized()
	{
		return getStatus().getInitialized();
	}

	public boolean isSimulated()
	{
		return getStatus().isSimulated();
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
		for (Component localBehavior : component.getContents().getObjects(Component.class, true, HybridSystem.class))// ,
		// DynamicalModel.class))
		{
			try
			{
				Boolean jumpOccurring = ComponentAdministrator.jumpOccurring((HybridSystem) localBehavior, true);
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

		for (HybridSystem localBehavior : component.getContents().getObjects(HybridSystem.class, true))
		{
			try
			{

				boolean jumpOccurred = false;
				// if (ComponentAdministrator.jumpOccurring(localBehavior,
				// true))
				// {
				// jumpOccurred =
				// ComponentAdministrator.applyDynamics(localBehavior, true,
				// jump_occurring);
				// } else if
				// (ComponentAdministrator.flowOccurring(localBehavior, true))
				// {
				// jumpOccurred =
				// ComponentAdministrator.applyDynamics(localBehavior, true,
				// jump_occurring);
				// } else
				// {
				//
				// }
				jumpOccurred = ComponentAdministrator.applyDynamics(localBehavior, true, jump_occurring);

				if (jumpOccurred)
				{
					// ContentOperator.getContentAdministrator(getEnvironmentKey()).getEnvironmentHybridTime()
					// .incrementJumpIndex();
					// ComponentOperator.getOperator(((Component)
					// localBehavior)).storeData();
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
	}

	public void storeData()
	{
		for (Data data : component.getContents().getObjects(Data.class, true))
		{

			DataOperator.getOperator(data).storeValue(component.getEnvironment().getEnvironmentTime(), true);
		}
	}

	public boolean outOfAllDomains()
	{
		return outOfAllDomains(component.getEnvironment());
	}

	public boolean outOfAllDomains(Component component)
	{
		return !(ComponentOperator.getOperator(component).isJumpOccurring()
		|| ComponentOperator.getOperator(component).isFlowOccurring());
	}

	public void protectedInitialize()
	{
		if (!getStatus().getInitialized())
		{
			component.initialize();
			getStatus().setInitialized(true);
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
				ComponentOperator.getOperator(comp).setInitialized(initialize_components);
			}
		}
	}

	public void loadHierarchy(ComponentOrganizer hierarchy)
	{
		component.contents = hierarchy;
	}

	public void storeConfiguration()
	{
		configuration = (Component) ObjectCloner.xmlClone(component);
	}

	public Component getNewInstance()
	{
		return (Component) ObjectCloner.xmlClone(configuration);
	}

	public void generateAddress()
	{
		if (component.status.address == null)
		{
			String[] packageName = component.toString().split(Pattern.quote("."));
			component.status.address = packageName[packageName.length - 1];
		}
	}

	public static String generateInstanceAddress(Object obj)
	{

		String[] packageName = obj.toString().split(Pattern.quote("."));
		return packageName[packageName.length - 1];

	}

	/*
	 * Internal Operation Functions
	 */
	public void setup(String title, Class<?> base_class)
	{
		// ComponentAdministrator.getConfigurer(this);
		component.status = new ComponentStatus();
		component.labels = new ComponentLabel(title);
		component.contents = new ComponentOrganizer(component);

		// ComponentCoordinator.constructTree(hierarchy);
	}

	public static ComponentOperator getOperator(Component component)
	{
		if (components.containsKey(component))
		{
			return components.get(component);

		} else
		{

			ComponentOperator config = new ComponentOperator(component);
			components.put(component, config);
			return config;

		}
	}
}
