package edu.ucsc.cross.hse.core.component.foundation;

import java.util.ArrayList;
import java.util.HashMap;

import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.execution.Initializer;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.component.data.DataOperator;
import edu.ucsc.cross.hse.core.component.system.GlobalAccessor;
import edu.ucsc.cross.hse.core.component.system.GlobalHybridSystem;
import edu.ucsc.cross.hse.core.object.accessors.Hierarchy;
import edu.ucsc.cross.hse.core.object.accessors.Properties;

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
	protected String environmentKey;
	// @CoreComponent
	// protected GlobalHybridSystem environment; // environment where the
	// component is located
	@CoreComponent
	protected Boolean initialized; // flag indicating if component has been
									// initialized or not

	protected boolean simulated; // flag indicating if contained data is
	// simulated
	// or not

	@CoreComponent
	protected Properties properties; // properties of the component

	@CoreComponent
	protected Hierarchy hierarchy; // properties of the component

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
	 * Adds a number of sub-components to this component. This is used to add
	 * components that are not explicitly defined in the main class, which
	 * allows for variations without modifying the main component code itself.
	 * This method allows any number of duplicate components to be added
	 * 
	 * @param component - component to be added
	 * 
	 * @param quantity - number of components to be added
	 */
	@SuppressWarnings("unchecked")
	public <T extends Component> void addComponent(T component, Integer... quantity)
	{
		Integer num = 1;
		if (quantity.length > 0)
		{
			num = quantity[0];
		}
		hierarchy.addComponent(component, num);
	}

	public ArrayList<Component> getComponents(boolean include_children, Class<?>... matching_classes)
	{
		return hierarchy.getComponents(include_children, matching_classes);
	}

	public <T> ArrayList<T> getComponents(Class<T> output_class, boolean include_children, Class<?>... matching_classes)
	{
		return hierarchy.getComponents(output_class, include_children, matching_classes);
	}

	public GlobalHybridSystem getEnvironment()
	{
		return GlobalAccessor.getGlobalSystem(environmentKey);
	}

	public Properties getProperties()
	{
		return properties;
	}

	public Hierarchy getHierarchy()
	{
		return hierarchy;
	}

	// protected BackgroundOperations getConfigurer()
	// {
	// return BackgroundOperations.getConfigurer(this);
	// }

	public <T extends Component> T copy()
	{
		return copy(false, false);
	}

	public <T extends Component> T copy(boolean include_data, boolean include_hierarchy)
	{
		HashMap<Data, HashMap> tempValues = new HashMap<Data, HashMap>();
		Hierarchy h = getHierarchy();

		if (!include_data)
		{
			for (Data data : hierarchy.getComponents(Data.class, true))
			{
				tempValues.put(data, data.getStoredValues());
				data.dataOps().setStoredValues(new HashMap<Double, T>());
			}
		}
		if (!include_hierarchy)
		{
			hierarchy = null;
		} // environment = null;
		T copy = (T) ComponentOperator.cloner.deepClone(this);
		if (!include_hierarchy)
		{
			hierarchy = h;
		}
		if (!include_data)
		{
			for (Data data : getComponents(Data.class, true))
			{
				// tempValues.put(data, Data.getStoredValues(data));
				data.dataOps().setStoredValues(tempValues.get(data));
			}
		}

		return copy;

	}

	private void setup(String title, Class<?> base_class)
	{
		initialized = false;
		ComponentOperator.getConfigurer(this);
		properties = new Properties(title, base_class);
		hierarchy = new Hierarchy(this);
	}

	protected ComponentOperator compOps()
	{
		return ComponentOperator.getConfigurer(this);
	}

	protected DataOperator dataOps()
	{
		if (FieldFinder.containsSuper(this, Data.class))
		{
			return DataOperator.operator((Data) this);
		} else
		{
			return null;
		}
	}
}
