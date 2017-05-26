package edu.ucsc.cross.hse.core.component.constructors;

import java.util.ArrayList;
import java.util.HashMap;

import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.execution.Initializer;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.component.foundation.ComponentConfigurer;
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
	public String environmentKey;
	// @CoreComponent
	// protected GlobalHybridSystem environment; // environment where the
	// component is located
	@CoreComponent
	private Boolean initialized; // flag indicating if component has been
									// initialized or not

	@CoreComponent
	private Properties properties; // properties of the component

	@CoreComponent
	public Hierarchy hierarchy; // properties of the component

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
		setup(title, Component.class);
	}

	public ArrayList<Component> getComponents(boolean include_children)
	{
		return hierarchy.getComponents(include_children);
	}

	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> getComponents(Class<T> component_class, boolean include_children)
	{
		return hierarchy.getComponents(component_class, include_children);
	}

	@SuppressWarnings("unchecked")
	public <T> ArrayList<Component> getMatchingComponents(Class<T> component_class, boolean include_children)
	{
		return hierarchy.getMatchingComponents(component_class, include_children);
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

	public static Boolean isInitialized(Component component)
	{
		return component.initialized;
	}

	public static void setInitialized(Component component, Boolean initialized)
	{
		component.initialized = initialized;
	}

	public void protectedInitialize()
	{
		if (!initialized)
		{
			initialize();
			Component.setInitialized(this, true);
		}
	}

	private void setup(String title, Class<?> base_class)
	{
		initialized = false;
		properties = new Properties(title, base_class);
		hierarchy = new Hierarchy(this);
	}

	public <T extends Component> T copy()
	{
		return copy(false, false);
	}

	public <T extends Component> T copy(boolean include_data, boolean include_hierarchy)
	{
		HashMap<Data, HashMap> tempValues = new HashMap<Data, HashMap>();
		Hierarchy h = this.getHierarchy();

		if (!include_data)
		{
			for (Data data : getComponents(Data.class, true))
			{
				tempValues.put(data, Data.getStoredValues(data));
				Data.setStoredValues(data, new HashMap<Double, T>());
			}
		}
		if (!include_hierarchy)
		{
			hierarchy = null;
		} // environment = null;
		T copy = (T) ComponentConfigurer.cloner.deepClone(this);
		if (!include_hierarchy)
		{
			this.hierarchy = h;
		}
		if (!include_data)
		{
			for (Data data : this.getComponents(Data.class, true))
			{
				// tempValues.put(data, Data.getStoredValues(data));
				Data.setStoredValues(data, tempValues.get(data));
			}
		}

		return copy;

	}

	public ComponentConfigurer getConfigurer()
	{
		return ComponentConfigurer.getConfigurer(this);
	}

}
