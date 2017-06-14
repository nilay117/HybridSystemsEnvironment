package edu.ucsc.cross.hse.core.framework.component;

import bs.commons.objects.execution.Initializer;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystem;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystemInterface;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystemOperator;
import edu.ucsc.cross.hse.core2.framework.component.ComponentAddress;

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

	private ProtectedComponentData status; // configuration status of
									// this instance of the
									// component

	private ComponentDescription description; // specific information describing
												// the component

	private ComponentHierarchy hierarchy; // component access hierarchy of this
											// component

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
	public GlobalSystem getEnvironment()
	{
		return GlobalSystemOperator.getGlobalSystem(getHierarchy().getEnvironmentKey());
	}

	public ComponentDescription getDescription()
	{
		return description;
	}

	public ComponentHierarchy getHierarchy()
	{
		return hierarchy;
	}

	public ComponentActions getActions()
	{
		return ComponentOperator.getConfigurer(this);
	}

	/*
	 * External Operation Functions
	 */
	ProtectedComponentData getStatus()
	{
		return status;
	}

	void loadHierarchy(ComponentHierarchy hierarchy)
	{
		this.hierarchy = hierarchy;
	}

	/*
	 * Internal Operation Functions
	 */
	private void setup(String title, Class<?> base_class)
	{
		ComponentOperator.getConfigurer(this);
		status = new ProtectedComponentData();

		description = new ComponentDescription(title);
		hierarchy = new ComponentHierarchy(this);
	}

}
