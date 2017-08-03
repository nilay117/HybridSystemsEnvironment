package edu.ucsc.cross.hse.core.framework.component;

/*
 * Foundation of all components that provides API and ensures proper
 * compatibility. Anything that is an extension of this class can be used with
 * the hybrid systems environment as standalone components or pieces of another
 * component. The hierarchical structure allows these extensions to be accessed
 * regardless of location, ie in a HashMap within a sub component. This class
 * also provides some additional functionality and access to the environment and
 * components within its hierarchy.
 */
public abstract class Component
{

	/*
	 * Current status of this component
	 */
	ComponentStatus status;

	/*
	 * Specific information describing this component
	 */
	ComponentLabel labels;

	/*
	 * Component access hierarchy of this component
	 */
	ComponentContent contents;

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
		ComponentWorker.getOperator(this).setup(classification, this.getClass());
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
		ComponentWorker.getOperator(this).setup(classification, this.getClass());

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
		ComponentWorker.getOperator(this).setup(this.getClass().getSimpleName(), this.getClass());

	}

	/*
	 * Access to the component API for content access, component configuration,
	 * and general tasks
	 *
	 * @return component operator
	 */
	public ComponentOperator component()
	{
		return ComponentWorker.getOperator(this);
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
