package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;
import java.util.HashMap;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.models.HybridDynamicalModel;
import edu.ucsc.cross.hse.core.procesing.io.FileParser;

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

	public boolean isData()
	{
		try
		{
			Data d = Data.class.cast(component);
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}

	/*
	 * Determines whether or not a jump is occurring in any component within the
	 * hybrid system
	 * 
	 * @return true if a jump is occurring, false otherwise
	 */
	public Boolean isJumpOccurring()
	{
		Boolean jumpOccurred = false;
		for (HybridDynamicalModel localBehavior : component.getHierarchy().getComponents(HybridDynamicalModel.class,
		true))
		{
			try
			{
				Boolean jumpOccurring = HybridDynamicalModel.jumpOccurring(localBehavior, true);
				if (jumpOccurring != null)
				{
					try
					{
						jumpOccurred = jumpOccurred || jumpOccurring;
					} catch (Exception outOfDomain)
					{
						outOfDomain.printStackTrace();
					}
				}
			} catch (Exception behaviorFail)
			{
				behaviorFail.printStackTrace();
			}
		}
		return jumpOccurred;
	}

	public void addComponentFromFile(String directory_path, String file_name)
	{
		Component newComponent = null;
		try
		{

			newComponent = (FileParser.loadComponent(directory_path, file_name));

			component.getHierarchy().addComponent(newComponent);

		} catch (Exception badComponent)
		{
			badComponent.printStackTrace();
		}
	}

	public void saveComponentToFile(String directory_path, String file_name)
	{
		Object clonedComponent = ObjectCloner.xmlClone(this.component);
		FileSystemOperator.createOutputFile(new File(directory_path, file_name),
		XMLParser.serializeObject(this.component));// clonedComponent));

	}

	public void setInitialized(Boolean initialized)
	{
		component.getStatus().setInitialized(initialized);
	}

	public void setSimulated(boolean simulated)
	{
		component.getStatus().setSimulated(simulated);
	}
}
