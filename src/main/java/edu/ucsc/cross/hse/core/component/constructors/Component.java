package edu.ucsc.cross.hse.core.component.constructors;

import java.util.ArrayList;

import bs.commons.objects.access.CoreComponent;
import bs.commons.objects.execution.Initializer;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.component.system.GlobalHybridSystem;
import edu.ucsc.cross.hse.core.object.accessors.Hierarchy;
import edu.ucsc.cross.hse.core.object.accessors.Properties;

public abstract class Component implements Initializer // implements
														// ComponentInterface
{

	@CoreComponent
	private GlobalHybridSystem environment; // environment that the
											// component is in
	@CoreComponent
	private Boolean initialized;

	@CoreComponent
	private Properties properties; // properties of the component
	@CoreComponent
	private Hierarchy components; // properties of the component

	public Hierarchy getComponents()
	{
		return components;
	}

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

	// /*
	// * Adds a single sub-component to this component. This is used to add
	// * components that are not explicitly defined in the main class, which
	// * allows for variations without modifying the main component code itself
	// *
	// * @param component - component to be added
	// */
	// public <T extends Component> void addComponent(T component)
	// {
	// addComponent(component, 1);
	// }
	//
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
	// public <T extends Component> void addComponent(T component, Integer
	// quantity)
	// {
	// components.addComponent(component, quantity);
	// }

	public ArrayList<Component> getComponents(boolean include_children)
	{
		return components.getComponents(include_children);
	}

	@SuppressWarnings("unchecked")
	public <T> ArrayList<T> getComponents(Class<T> component_class, boolean include_children)
	{
		return components.getComponents(component_class, include_children);
	}

	public GlobalHybridSystem getEnvironment()
	{
		return environment;
	}

	public Properties getProperties()
	{
		return properties;
	}

	public void saveComponentToFile(String directory_path, String file_name)
	{
		Object clonedComponent = ObjectCloner.xmlClone(this);
		Hierarchy.load(((Component) clonedComponent).getComponents());// .loadAllComponents();
		// ((Component) clonedComponent).storeSubComponents(((Component)
		// clonedComponent));
		// ((Component) clonedComponent).clearMapsIfEmpty();
		// for (Component subComponent : ((Component)
		// clonedComponent).getAllllComponents(true))
		// {
		// subComponent.clearMapsIfEmpty();
		// }
		// FileSystemOperator.createOutputFile(new File(directory_path,
		// file_name),
		// XMLParser.serializeObject(clonedComponent));

	}

	private void setup(String title, Class<?> base_class)
	{
		initialized = false;
		properties = new Properties(title, base_class);
		components = new Hierarchy(this);
	}

	public void protectedInitialize()
	{
		if (!initialized)
		{
			initialize();
			Component.setInitialized(this, true);
		}
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

	public static Boolean getInitialized(Component component)
	{
		return component.initialized;
	}

	public static Boolean isInitialized(Component component)
	{
		return component.initialized;
	}

	public static void setEnvironment(Component component, GlobalHybridSystem environment)
	{
		GlobalHybridSystem accessor = environment;
		component.environment = accessor;
	}

	public static void setInitialized(Component component, Boolean initialized)
	{
		component.initialized = initialized;
	}

}
