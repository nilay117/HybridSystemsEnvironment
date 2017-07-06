package edu.ucsc.cross.hse.core.framework.component;

import bs.commons.objects.execution.Initializer;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;
import edu.ucsc.cross.hse.core.framework.environment.ContentOperator;

/*
 * This class is the foundation of all components to ensures proper
 * compatibility. Anything that is an extension of this class can be used with
 * the hybrid systems environment as standalone components or pieces of another
 * component. The hierarchical structure allows these extensions to be accessed
 * regardless of location, ie in a HashMap within a sub component. This class
 * also provides some additional functionality and access to the environment and
 * components within its hierarchy.
 */
public abstract class Component // implements Initializer
{

	ComponentStatus status; // current status of this component

	ComponentLabel labels; // specific information describing this component

	ComponentContent contents; // component access hierarchy of this
								// component

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param classification - general title of the component
	 * 
	 * @param name - specific name of the component
	 */
	public Component(String classification, String name)
	{
		FullComponentOperator.getOperator(this).setup(classification, this.getClass());
		this.labels.setName(name);

	}

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 * @param classification - general title of the component
	 */
	public Component(String classification)
	{
		FullComponentOperator.getOperator(this).setup(classification, this.getClass());

	}

	/*
	 * Constructor that defines the name of the component with this class
	 * (Component) as the base class. This constructor is used to create
	 * components that are not based off any of the core components:
	 * Behavior,Data,DataSet, and HybridSystem.
	 * 
	 */
	public Component()
	{
		FullComponentOperator.getOperator(this).setup(this.getClass().getSimpleName(), this.getClass());

	}

	/*
	 * Access to the component API for content access, component configuration,
	 * and general tasks
	 *
	 * @return component operator
	 */
	public UserComponentOperator component()
	{
		return FullComponentOperator.getOperator(this);
	}

	/*
	 * Initialize method that can be redefined and will be called each time the
	 * environment starts. This method is intentionally empty to save space and
	 * time as many components do not require it.
	 */
	protected void initialize()
	{

	}

}
