package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import bs.commons.objects.manipulation.ObjectCloner;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.procesing.io.FileContent;
import edu.ucsc.cross.hse.core.procesing.io.FileProcessor;

/*
 * Component configurer that allows custom components to be created dyamically.
 * Contains all methods pertaining to modifying the specified component
 */
public class ComponentConfigurer
{

	/*
	 * Component being configured
	 */
	private Component componen;

	/*
	 * Mapping of all components to their corresponding configurers
	 */
	protected static HashMap<Component, ComponentConfigurer> components = new HashMap<Component, ComponentConfigurer>();

	/*
	 * Constructor linking a component to the configurer
	 */
	public ComponentConfigurer(Component self)
	{
		componen = self;

	}

	/*
	 * Adds a single sub-component to this component. This is used to add
	 * components that are not explicitly defined in the main class, which
	 * allows for variations without modifying the main component code itself
	 *
	 * @param component - component to be added
	 */
	@SuppressWarnings("unchecked")
	public <T extends Component> ArrayList<Component> addComponent(T... components)
	{
		for (T component : components)
		{
			addComponent(component, 1);

		}
		return null;
	}

	/*
	 * Adds a single sub-component to this component. This is used to add
	 * components that are not explicitly defined in the main class, which
	 * allows for variations without modifying the main component code itself
	 *
	 * @param component - component to be added
	 */
	public <T extends Component> ArrayList<Component> addComponent(T component)
	{
		return addComponent(component, 1);
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
	public <T extends Component> ArrayList<Component> addComponent(T component, Integer quantity)
	{
		ArrayList<Component> ret = new ArrayList<Component>();

		T initialClone = (T) ObjectCloner.xmlClone(component);
		for (Integer ind = 0; ind < quantity; ind++)
		{
			T clonedComponent = (T) ObjectCloner.xmlClone(initialClone);
			componen.contents.storeComponent(clonedComponent, true);
			ret.add(clonedComponent);
			initialClone = clonedComponent;
		}
		componen.contents.addAllUndeclaredComponents(ret);

		return ret;
	}

	/*
	 * Load contents from a file
	 */
	public void loadComponentFromFile(File file)
	{
		loadComponentsFromFile(file, 1, false);
	}

	/*
	 * Load contents from a file
	 */
	public void loadComponentsFromFile(File file, Integer quantity)
	{
		loadComponentsFromFile(file, quantity, false);
	}

	/*
	 * Load contents from a file
	 */
	public void loadComponentFromFile(File file, boolean reinitialize_data)
	{
		loadComponentsFromFile(file, 1, reinitialize_data);
	}

	/*
	 * Load contents from a file
	 */
	public void loadComponentsFromFile(File file, Integer quantity, boolean reinitialize_data)
	{
		Component component = (Component) FileProcessor.loadContents(file, FileContent.COMPONENT)
		.get(FileContent.COMPONENT);
		component.component().configure().setInitialized(false, reinitialize_data);
		addComponent(component, quantity);
	}

	/*
	 * Indirect access to the operator to keep it disconnected from the
	 * component itself
	 */
	public static ComponentConfigurer getOperator(Component component)
	{
		if (components.containsKey(component))
		{
			return components.get(component);

		} else
		{

			ComponentConfigurer config = new ComponentConfigurer(component);
			components.put(component, config);
			return config;

		}
	}

	/*
	 * Indicate whetehre or not this component should be initialized
	 */
	public void setInitialized(Boolean initialized)
	{
		setInitialized(initialized, false);
	}

	/*
	 * Indicate whetehre or not this component should be initialized
	 */
	@SuppressWarnings("rawtypes")
	public void setInitialized(Boolean initialized, Boolean include_data)
	{
		ArrayList<Data> data = componen.component().getContent().getData(true);
		for (Component comp : componen.component().getContent().getComponents(true))
		{
			if (!data.contains(comp) || include_data)
			{
				comp.component().configure().setInitialized(initialized);
			}
		}
		ComponentWorker.getOperator(componen).getStatus().setInitialized(initialized);
	}

	/*
	 * Indicate whether or not this component is simulated
	 */
	public void setSimulated(boolean simulated)
	{
		ComponentWorker.getOperator(componen).getStatus().setSimulated(simulated);
	}
}
