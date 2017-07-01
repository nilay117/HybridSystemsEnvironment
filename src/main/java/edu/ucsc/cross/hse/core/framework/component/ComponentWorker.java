package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;
import java.util.HashMap;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.Obj;
import edu.ucsc.cross.hse.core.framework.models.HybridSystem;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.ComponentAdministrator;
import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;

/*
 * This class contains the methods available to users that perform a variety of
 * tasks. These methods are safe to use whenever needed as they do not interfere
 * with the processor.
 */
public class ComponentWorker
{

	protected Component component;

	public ComponentWorker(Component component)
	{
		this.component = component;
	}

	public <T extends Component> T copy()
	{
		return copy(false, true);
	}

	public <T extends Component> T copy(boolean include_data, boolean include_hierarchy)
	{
		HashMap<Obj, HashMap<HybridTime, T>> tempValues = new HashMap<Obj, HashMap<HybridTime, T>>();
		ComponentOrganizer h = component.getContents();

		if (!include_data)
		{
			for (Obj data : component.getContents().getObjects(Obj.class, true))
			{
				tempValues.put(data, data.getActions().getStoredHybridValues());
			}
		}
		if (!include_hierarchy)
		{
			ComponentOperator.getOperator(component).loadHierarchy(null);
		} // environment = null;
		T copy = (T) ObjectCloner.xmlClone(component);// ComponentOperator.cloner.deepClone(component);
		if (!include_hierarchy)
		{
			ComponentOperator.getOperator(component).loadHierarchy(h);
		}
		if (!include_data)
		{
			for (Obj data : component.getContents().getObjects(Obj.class, true))
			{
				// tempValues.put(data, Data.getStoredValues(data));
				DataOperator.getOperator(data).setStoredHybridValues(tempValues.get(data));
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
		for (HybridSystem localBehavior : component.getContents().getObjects(HybridSystem.class, true))
		{
			try
			{
				Boolean jumpOccurring = ComponentAdministrator.jumpOccurring(localBehavior, true);
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

	/*
	 * Determines whether or not a jump is occurring in any component within the
	 * hybrid system
	 * 
	 * @return true if a jump is occurring, false otherwise
	 */
	public Boolean isFlowOccurring()
	{
		Boolean jumpOccurred = false;
		for (HybridSystem localBehavior : component.getContents().getObjects(HybridSystem.class, true))
		{
			try
			{
				Boolean jumpOccurring = ComponentAdministrator.flowOccurring(localBehavior, true);
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

	/*
	 * Add a sub-component to the current component
	 */
	public void addComponentFromFile(String directory_path, String file_name)
	{
		Component newComponent = null;
		try
		{
			newComponent = (FileExchanger.loadComponent(directory_path, file_name));
			component.getContents().addComponent(newComponent);

		} catch (Exception badComponent)
		{
			badComponent.printStackTrace();
		}
	}

	/*
	 * Save the current component to a file
	 */
	public void saveToFile(String directory_path, String file_name)
	{

		FileSystemOperator.createOutputFile(new File(directory_path, file_name), XMLParser.serializeObject(component));// clonedComponent));

	}

	public void setInitialized(Boolean initialized)
	{
		ComponentOperator.getOperator(component).getStatus().setInitialized(initialized);
	}

	public void setSimulated(boolean simulated)
	{
		ComponentOperator.getOperator(component).setSimulated(simulated);
	}

	public String getAddress()
	{
		return component.status.address;
	}

	public SettingConfigurer getSettings()
	{
		return HybridEnvironment.getEnvironment(component.contents.getEnvironmentKey()).getSettings();
	}
}
