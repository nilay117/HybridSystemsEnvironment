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

	private Component configuration; // wpointer to own component

	protected static HashMap<Component, ComponentOperator> components = new HashMap<Component, ComponentOperator>();

	// relative compoents

	public ComponentOperator(Component component)
	{
		super(component);
		// this.component = component;
		components.put(component, this);
		configuration = (Component) ObjectCloner.xmlClone(component);
		// getConfigurer(component);
		// component.hierarchy.
	}

	/*
	 * Accesses the current status of the component. This is for the processor
	 * and is not intended for users.
	 */
	public ComponentStatus getStatus()
	{
		return component.status;
	}

	/*
	 * Has the component been initialized before
	 */
	public Boolean isInitialized()
	{
		return getStatus().getInitialized();
	}

	/*
	 * Is the component simulated or is it external
	 */
	public boolean isSimulated()
	{
		return getStatus().isSimulated();
	}

	/*
	 * Determines whether or not a jump is occurring in any component within the
	 * hybrid system, and which components they are occurring in
	 * 
	 * @return list of all components with jumps occurring
	 */
	public ArrayList<Component> jumpingComponents()
	{
		ArrayList<Component> jumpComponents = new ArrayList<Component>(); // prepare
																			// list
																			// of
																			// jumping
																			// components

		for (Component localBehavior : component.getContent().getObjects(Component.class, true, HybridSystem.class)) // check
																														// through
																														// all
																														// components
																														// where
																														// a
																														// jump
																														// could
																														// occur

		{
			try
			{
				Boolean jumpOccurring = ComponentAdministrator.jumpOccurring((HybridSystem) localBehavior, true);
				if (jumpOccurring != null)
				{
					if (jumpOccurring) // add component to list if jump is
										// occcuring
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

		for (HybridSystem localBehavior : component.getContent().getObjects(HybridSystem.class, true))
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

	/*
	 * Attempt to store all data point of the component and any descndents
	 */
	public void storeData()
	{
		for (Data data : component.getContent().getData(true))
		{
			DataOperator.getOperator(data).storeValue(component.getEnvironment().getEnvironmentTime(), true);
		}
	}

	/*
	 * Is the component out of all domains, more specifically has its solution
	 * ended as it can no longer flow or jump
	 */
	public boolean outOfAllDomains()
	{
		return outOfAllDomains(component.getEnvironment());
	}

	/*
	 * Is some specified component out of all domains, more specifically has its
	 * solution ended as it can no longer flow or jump
	 */
	public boolean outOfAllDomains(Component component)
	{
		return !(ComponentOperator.getOperator(component).isJumpOccurring()
		|| ComponentOperator.getOperator(component).isFlowOccurring());
	}

	/*
	 * An initialize call that insures the component does not get initialized
	 * twice
	 */
	public void protectedInitialize()
	{
		if (!getStatus().getInitialized())
		{
			component.initialize();
			getStatus().setInitialized(true);
		}
	}

	/*
	 * Clears the hierarchy of the component in case it needs to be reset or
	 * moved to another system
	 */
	public void resetHierarchy()
	{
		component.getContent().setup();
	}

	/*
	 * Constructs the hierarchy of this component, allowing it to access related
	 * components
	 */
	public void initializeContentMappings()
	{
		initializeContentMappings(null);
	}

	/*
	 * Implementation of content mapping
	 */
	public void initializeContentMappings(Boolean initialize_components)
	{
		component.getContent().constructTree();
		// ComponentOrganizer.constructTree(component.getContents());
		if (initialize_components != null)
		{
			for (Component comp : component.getContent().getComponents(true))
			{
				comp.getConfiguration().setInitialized(initialize_components);
			}
		}
	}

	/*
	 * Load a hierarchy from an external source
	 */
	public void loadHierarchy(ComponentOrganizer hierarchy)
	{
		component.contents = hierarchy;
	}

	/*
	 * Store the state of the component before the experiment starts allowing
	 * multiple simulations with the same conditions
	 */
	public void storeConfiguration()
	{
		configuration = (Component) ObjectCloner.xmlClone(component);
	}

	/*
	 * Generate a new instance of the component based off the initially stored
	 * values
	 */
	public Component getNewInstance()
	{
		return (Component) ObjectCloner.xmlClone(configuration);
	}

	/*
	 * Generate a unique address for the component
	 */
	public void generateAddress()
	{
		if (component.status.address == null)
		{
			String[] packageName = component.toString().split(Pattern.quote("."));
			component.status.address = packageName[packageName.length - 1];
		}
	}

	/*
	 * Generate a unique address for any object
	 */
	public static String generateInstanceAddress(Object obj)
	{

		String[] packageName = obj.toString().split(Pattern.quote("."));
		return packageName[packageName.length - 1];

	}

	/*
	 * Initial setup of the component
	 */
	public void setup(String title, Class<?> base_class)
	{
		// ComponentAdministrator.getConfigurer(this);
		component.status = new ComponentStatus();
		component.labels = new ComponentLabel(title);
		component.contents = new ComponentOrganizer(component);

		// ComponentCoordinator.constructTree(hierarchy);
	}

	/*
	 * Set the environment generated by the environment that contains this
	 * component
	 */
	public void setEnvironmentKey(String environmentKey)
	{
		component.contents.environmentKey = environmentKey;
	}

	/*
	 * Indirect access to the operator to keep it disconnected from the
	 * component itself
	 */
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
