package edu.ucsc.cross.hse.core.framework.component;

import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.execution.Initializer;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystemInterface;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystemLibrary;

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

	// /*
	// * Adds a number of sub-components to this component. This is used to add
	// * components that are not explicitly defined in the main class, which
	// * allows for variations without modifying the main component code itself.
	// * This method allows any number of duplicate components to be added
	// *
	// * @param component - component to be added
	// *
	// * @param quantity - number of components to be added
	// */
	// @SuppressWarnings("unchecked")
	// public <T extends Component> void addComponent(T component, Integer...
	// quantity)
	// {
	// Integer num = 1;
	// if (quantity.length > 0)
	// {
	// num = quantity[0];
	// }
	// components.addComponent(component, num);
	// }

	// public ArrayList<Component> getComponents(boolean include_children,
	// Class<?>... matching_classes)
	// {
	// return components.getComponents(include_children, matching_classes);
	// }
	//
	// public <T> ArrayList<T> getComponents(Class<T> output_class, boolean
	// include_children, Class<?>... matching_classes)
	// {
	// return components.getComponents(output_class, include_children,
	// matching_classes);
	// }

	public GlobalSystemInterface getEnvironment()
	{
		return GlobalSystemLibrary.getGlobalSystem(address.getEnvironmentKey());
	}

	public ComponentClassification getClassification()
	{
		return classification;
	}

	public ComponentHierarchy getHierarchy()
	{
		return hierarchy;
	}

	// protected BackgroundOperations getConfigurer()
	// {
	// return BackgroundOperations.getConfigurer(this);
	// }

	public ComponentActions actions()
	{
		return ComponentOperator.getConfigurer(this);
	}
	// public <T extends Component> T copy()
	// {
	// return copy(false, false);
	// }
	//
	// public <T extends Component> T copy(boolean include_data, boolean
	// include_hierarchy)
	// {
	// HashMap<Data, HashMap> tempValues = new HashMap<Data, HashMap>();
	// ComponentHierarchy h = getHierarchy();
	//
	// if (!include_data)
	// {
	// for (Data data : components.getComponents(Data.class, true))
	// {
	// tempValues.put(data, data.getStoredValues());
	// DataOperator.dataOp(data).setStoredValues(new HashMap<Double, T>());
	// }
	// }
	// if (!include_hierarchy)
	// {
	// components = null;
	// } // environment = null;
	// T copy = (T) ComponentOperator.cloner.deepClone(this);
	// if (!include_hierarchy)
	// {
	// components = h;
	// }
	// if (!include_data)
	// {
	// for (Data data : getComponents(Data.class, true))
	// {
	// // tempValues.put(data, Data.getStoredValues(data));
	// DataOperator.dataOp(data).setStoredValues(tempValues.get(data));
	// }
	// }
	//
	// return copy;
	//
	// }

	private void setup(String title, Class<?> base_class)
	{

		ComponentOperator.getConfigurer(this);
		status = new ComponentStatus();
		address = new ComponentAddress();
		classification = new ComponentClassification(title, base_class);
		hierarchy = new ComponentHierarchy(this);
	}

	ComponentStatus configuration()
	{
		return status;
	}

	public ComponentAddress address()
	{
		return address;
	}

	void loadHierarchy(ComponentHierarchy hierarchy)
	{
		this.hierarchy = hierarchy;
	}
}
