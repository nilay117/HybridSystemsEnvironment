package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;

import bs.commons.objects.execution.Initializer;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContentOperator;
import edu.ucsc.cross.hse.core2.framework.component.ComponentAddress;
import edu.ucsc.cross.hse.core2.framework.environment.GlobalSystemInterface;

/*
 * This class is the foundation of all components to ensures proper
 * compatibility. Anything that is an extension of this class can be used with
 * the hybrid systems environment as standalone components or pieces of another
 * component. The hierarchical structure allows these extensions to be accessed
 * regardless of location, ie in a HashMap within a sub component. This class
 * also provides some additional functionality and access to the environment and
 * components within its hierarchy.
 */
public abstract class Component implements Initializer
{

	private ComponentStatus state; // configuration state of
									// this instance of the
									// component

	private ComponentCharacterization description; // specific information
													// describing
													// the component

	private ComponentCoordinator hierarchy; // component access hierarchy of
											// this
											// component
	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param title - title of the component
	 */

	public Component(String title, String description)
	{
		setup(title, this.getClass());
		this.description.setDescription(description);

	}

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param title - title of the component
	 */
	public Component(String title)
	{
		setup(title, this.getClass());

	}

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param title - title of the component
	 */
	public Component()
	{
		setup(this.getClass().getSimpleName(), this.getClass());

	}

	/*
	 * User Access Functions
	 */
	public EnvironmentContent getEnvironment()
	{
		return EnvironmentContentOperator.getGlobalSystem(getHierarchy().getEnvironmentKey());
	}

	public ComponentCharacterization getDescription()
	{
		return description;
	}

	public ComponentCoordinator getHierarchy()
	{
		return hierarchy;
	}

	public ComponentOperator getActions()
	{
		return ComponentAdministrator.getConfigurer(this);
	}

	/*
	 * External Operation Functions
	 */
	ComponentStatus getStatus()
	{
		return state;
	}

	void loadHierarchy(ComponentCoordinator hierarchy)
	{
		this.hierarchy = hierarchy;
	}

	/*
	 * Internal Operation Functions
	 */
	private void setup(String title, Class<?> base_class)
	{
		ComponentAdministrator.getConfigurer(this);
		state = new ComponentStatus();

		description = new ComponentCharacterization(title);
		hierarchy = new ComponentCoordinator(this);
	}

}
