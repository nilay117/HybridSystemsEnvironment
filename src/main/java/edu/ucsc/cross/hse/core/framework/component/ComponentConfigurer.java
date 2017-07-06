package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import bs.commons.objects.manipulation.ObjectCloner;
import edu.ucsc.cross.hse.core.procesing.io.FileContent;
import edu.ucsc.cross.hse.core.procesing.io.FileProcessor;

public class ComponentConfigurer
{

	private Component co; // wpointer to own component

	protected static HashMap<Component, ComponentConfigurer> components = new HashMap<Component, ComponentConfigurer>();

	public ComponentConfigurer(Component self)
	{
		co = self;
		// TODO Auto-generated constructor stub
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
		// T initialClone = ObjectCloner.cloner.deepClone(component);
		for (Integer ind = 0; ind < quantity; ind++)
		{
			T clonedComponent = (T) ObjectCloner.xmlClone(initialClone);
			co.contents.storeComponent(clonedComponent, true);
			ret.add(clonedComponent);
			initialClone = clonedComponent;
			// clonedComponent = (T) ObjectCloner.xmlClone(initialClone);
		}
		co.contents.addAllUndeclaredComponents(ret);

		return ret;
	}

	/*
	 * Load contents from a file
	 */
	public void loadComponentFromFile(File file)
	{
		loadComponentsFromFile(file, 1);
	}

	/*
	 * Load contents from a file
	 */
	public void loadComponentsFromFile(File file, Integer quantity)
	{
		Component component = FileProcessor.load(file, FileContent.COMPONENT, FileContent.DATA);
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
		ComponentOperator.getOperator(co).getStatus().setInitialized(initialized);
	}

	/*
	 * Indicate whether or not this component is simulated
	 */
	public void setSimulated(boolean simulated)
	{
		ComponentOperator.getOperator(co).getStatus().setSimulated(simulated);
	}
}
