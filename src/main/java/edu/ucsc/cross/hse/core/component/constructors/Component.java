package edu.ucsc.cross.hse.core.component.constructors;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.rits.cloning.Cloner;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.execution.Initializer;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.component.data.Data;
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
	public static Cloner cloner = new Cloner();
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

	public static Component getComponentFromFile(String file_path)
	{
		Component component = null;
		try
		{

			component = (Component) XMLParser.getObject(file_path);
		} catch (Exception badComponent)
		{
			badComponent.printStackTrace();
		}
		return component;

	}

	public void saveComponentToFile(String directory_path, String file_name)
	{
		Object clonedComponent = ObjectCloner.xmlClone(this);

		FileSystemOperator.createOutputFile(new File(directory_path, file_name), XMLParser.serializeObject(this));// clonedComponent));

	}

	// public GlobalHybridSystem getEnvironment()
	// {
	// return environment;
	// }
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

	public static void setEnvironmentLink(Component component, GlobalHybridSystem environment)
	{
		// GlobalHybridSystem accessor = environment;
		// component.environment = environment;// accessor;
	}

	public static void setEnvironment(Component component, String environment_id)
	{
		// GlobalHybridSystem accessor = environment;
		component.environmentKey = environment_id;// accessor;
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

	public static <T extends Component> T duplicateComponentStates(T original)
	{
		HashMap<Data, HashMap> tempValues = new HashMap<Data, HashMap>();
		Hierarchy h = original.getHierarchy();
		GlobalHybridSystem s = original.getEnvironment();
		for (Data data : original.getComponents(Data.class, true))
		{
			tempValues.put(data, Data.getStoredValues(data));
			Data.setStoredValues(data, null);
		}
		original.hierarchy = null;
		// original.environment = null;
		T copy = (T) ObjectCloner.xmlClone(original);

		// copy.hierarchy = h;
		// copy.environment = s;
		original.hierarchy = h;
		// original.environment = s;
		for (Data data : original.getComponents(Data.class, true))
		{
			// tempValues.put(data, Data.getStoredValues(data));
			Data.setStoredValues(data, tempValues.get(data));
		}
		return copy;

	}

	public <T extends Component> T copy()
	{
		return copy(false, false);
	}

	public <T extends Component> T copy(boolean include_data, boolean include_hierarchy)
	{
		HashMap<Data, HashMap> tempValues = new HashMap<Data, HashMap>();
		Hierarchy h = getHierarchy();
		GlobalHybridSystem s = getEnvironment();

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
		T copy = (T) cloner.deepClone(this);
		if (include_data)
		{
			for (Data data : this.getComponents(Data.class, true))
			{
				// tempValues.put(data, Data.getStoredValues(data));
				Data.setStoredValues(data, tempValues.get(data));
			}
		}

		if (!include_hierarchy)
		{
			hierarchy = h;
		} // environment = null;
			// copy.hierarchy = h;
			// copy.environment = s;
			// hierarchy = h;
			// environment = s;
			// for (Data data : this.getComponents(Data.class, true))
			// {
			// // tempValues.put(data, Data.getStoredValues(data));
			// Data.setStoredValues(data, tempValues.get(data));
			// }

		return copy;

	}
}
