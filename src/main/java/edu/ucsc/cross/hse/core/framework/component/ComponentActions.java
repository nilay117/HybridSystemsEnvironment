package edu.ucsc.cross.hse.core.framework.component;

import java.util.HashMap;

import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;

/*
 * This class contains the methods available to users that perform a variety of
 * tasks. These methods are safe to use whenever needed as they do not interfere
 * with the processor.
 */
public class ComponentActions
{

	protected Component component;

	public ComponentActions(Component component)
	{
		this.component = component;
	}

	public <T extends Component> T copy()
	{
		return copy(false, false);
	}

	public <T extends Component> T copy(boolean include_data, boolean include_hierarchy)
	{
		HashMap<Data, HashMap> tempValues = new HashMap<Data, HashMap>();
		ComponentHierarchy h = component.getHierarchy();

		if (!include_data)
		{
			for (Data data : component.getHierarchy().getComponents(Data.class, true))
			{
				tempValues.put(data, data.getActions().getStoredValues());
				DataOperator.getOperator(data).setStoredValues(new HashMap<Double, T>());
			}
		}
		if (!include_hierarchy)
		{
			component.loadHierarchy(null);
		} // environment = null;
		T copy = (T) ComponentOperator.cloner.deepClone(component);
		if (!include_hierarchy)
		{
			component.loadHierarchy(h);
		}
		if (!include_data)
		{
			for (Data data : component.getHierarchy().getComponents(Data.class, true))
			{
				// tempValues.put(data, Data.getStoredValues(data));
				DataOperator.getOperator(data).setStoredValues(tempValues.get(data));
			}
		}

		return copy;

	}

	public void setInitialized(boolean initialized)
	{
		component.getStatus().setInitialized(initialized);
	}
}
