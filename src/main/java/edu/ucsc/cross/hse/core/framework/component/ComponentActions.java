package edu.ucsc.cross.hse.core.framework.component;

import java.util.HashMap;

import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;

public class ComponentActions
{

	public Component component;

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
				tempValues.put(data, data.actions().getStoredValues());
				DataOperator.dataOp(data).setStoredValues(new HashMap<Double, T>());
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
				DataOperator.dataOp(data).setStoredValues(tempValues.get(data));
			}
		}

		return copy;

	}

}
