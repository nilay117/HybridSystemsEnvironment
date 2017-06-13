package edu.ucsc.cross.hse.core.framework.component;

import bs.commons.objects.execution.Initializer;
import edu.ucsc.cross.hse.core.framework.annotations.CoreComponent;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystemInterface;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystemOperator;

/*
 * This class is the foundation of all components that handles the
 * compatibility. Component is the constructor that is used to build other
 * constructors such as all of the ones above. It can be used to create new
 * constructors, or a whole new custom component with the same built in
 * compatibility and functionality
 */
public abstract class Component implements Initializer
{

	@CoreComponent
	private ComponentAddress address; // address information about this
										// component
	@CoreComponent
	private ComponentStatus status; // configuration status of
									// this instance of the
									// component

	@CoreComponent
	private ComponentClassification classification; // specific properties of this
	// component

	@CoreComponent
	private ComponentHierarchy hierarchy; // component access hierarchy of this
											// component

	/*
	 * Constructor that defines the name and base class of the component
	 * 
	 * @param title - title of the component
	 * 
	 * @param base_class - base class of the component
	 */
	public Component(String title, Class<?> base_class)
	{

		setup(title, base_class);
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
	public ComponentAddress getAddress()
	{
		return address;
	}

	public GlobalSystemInterface getEnvironment()
	{
		return GlobalSystemOperator.getGlobalSystem(address.getEnvironmentKey());
	}

	public ComponentClassification getClassification()
	{
		return classification;
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
	 * Operation Access Functions
	 */
	ComponentStatus getStatus()
	{
		return status;
	}

	private void setup(String title, Class<?> base_class)
	{

		ComponentOperator.getConfigurer(this);
		status = new ComponentStatus();
		address = new ComponentAddress();
		classification = new ComponentClassification(title, base_class);
		hierarchy = new ComponentHierarchy(this);
	}

	void loadHierarchy(ComponentHierarchy hierarchy)
	{
		this.hierarchy = hierarchy;
	}
}
