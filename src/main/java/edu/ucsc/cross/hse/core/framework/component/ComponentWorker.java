package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;
import java.util.HashMap;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.models.HybridSystem;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.procesing.io.FileContent;
import edu.ucsc.cross.hse.core.procesing.io.FileElement;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.procesing.io.FileProcessor;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.ComponentAdministrator;
import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;

/*
 * This class contains the methods available to users that perform a variety of
 * tasks. These methods are safe to use whenever needed as they do not interfere
 * with the processor, though most are for preparing components.
 */
public class ComponentWorker
{

	protected Component component; // component this class works for

	public ComponentWorker(Component component) // constructor assigning a
												// component
	{
		this.component = component;
	}

	/*
	 * Copy the component without including the hierarchy or stored data to save
	 * space and time
	 */
	public <T extends Component> T copy()
	{
		return copy(false, true);
	}

	/*
	 * Copy the component with the option of including the hierarchy or saved
	 * data is necessary
	 */
	public <T extends Component> T copy(boolean include_data, boolean include_hierarchy)
	{
		HashMap<Data, HashMap<HybridTime, T>> tempValues = new HashMap<Data, HashMap<HybridTime, T>>();
		ComponentOrganizer h = component.getContent();

		if (!include_data)
		{
			for (Data data : component.getContent().getObjects(Data.class, true))
			{
				tempValues.put(data, data.getActions().getStoredValues());
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
			for (Data data : component.getContent().getObjects(Data.class, true))
			{
				// tempValues.put(data, Data.getStoredValues(data));
				DataOperator.getOperator(data).loadStoredValues(tempValues.get(data));
			}
		}

		return copy;

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
		for (HybridSystem localBehavior : component.getContent().getObjects(HybridSystem.class, true))
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
		for (HybridSystem localBehavior : component.getContent().getObjects(HybridSystem.class, true))
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
	 * Save the current component to a file
	 */
	public void saveToFile(File file)
	{
		saveToFile(file, false);
	}

	/*
	 * Save the current component to a file
	 */
	public void saveToFile(File file, boolean save_data)
	{
		if (save_data)
		{
			FileProcessor.saveComponent(component, file, FileContent.COMPONENT, FileContent.DATA);
		} else
		{
			FileProcessor.saveComponent(component, file, FileContent.COMPONENT);
		}
	}

	/*
	 * Get the address of the component, which is unique for every instance
	 */
	public String getAddress()
	{
		if (component.status.address == null)
		{
			ComponentOperator.getOperator(component).generateAddress();
		}
		return component.status.address;
	}

	/*
	 * Access the settings
	 */
	public SettingConfigurer getSettings()
	{
		return HybridEnvironment.getEnvironment(component.getActions().getEnvironmentKey()).getSettings();
	}

	/*
	 * Environment key generated by environment that contains this component
	 */
	public String getEnvironmentKey()
	{
		return component.contents.environmentKey;
	}
}
