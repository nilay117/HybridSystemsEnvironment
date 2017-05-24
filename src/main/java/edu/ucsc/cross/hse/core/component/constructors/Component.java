package edu.ucsc.cross.hse.core.component.constructors;

import java.io.File;
import java.util.ArrayList;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.execution.Initializer;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
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
	private GlobalHybridSystem environment; // environment where the
											// component is located
	@CoreComponent
	private Boolean initialized; // flag indicating if component has been
									// initialized or not

	@CoreComponent
	private Properties properties; // properties of the component

	@CoreComponent
	private Hierarchy hierarchy; // properties of the component

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

	public GlobalHybridSystem getEnvironment()
	{
		return environment;
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

	public static void setEnvironment(Component component, GlobalHybridSystem environment)
	{
		GlobalHybridSystem accessor = environment;
		component.environment = accessor;
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
}
