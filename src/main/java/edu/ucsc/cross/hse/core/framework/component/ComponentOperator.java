package edu.ucsc.cross.hse.core.framework.component;

import java.io.File;
import java.util.HashMap;

import bs.commons.objects.manipulation.ObjectCloner;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentOperator;
import edu.ucsc.cross.hse.core.framework.environment.HybridEnvironment;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.procesing.io.FileContent;
import edu.ucsc.cross.hse.core.procesing.io.FileProcessor;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;

/*
 * This class contains the methods available to users that perform a variety of
 * tasks. These methods are safe to use whenever needed as they do not interfere
 * with the processor, though most are for preparing components.
 */
public class ComponentOperator
{

	protected Component component; // component this class works for

	public ComponentOperator(Component component) // constructor assigning a
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
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public <T extends Component> T copy(boolean include_data, boolean include_hierarchy)
	{
		HashMap<Data, HashMap<HybridTime, T>> tempValues = new HashMap<Data, HashMap<HybridTime, T>>();
		ComponentContent h = component.component().getContent();
		try
		{
			if (!include_data)
			{
				for (Data data : component.component().getContent().getObjects(Data.class, true))
				{
					tempValues.put(data, data.component().getStoredValues());
				}
			}
			if (!include_hierarchy)
			{
				ComponentWorker.getOperator(component).loadHierarchy(null);
			} // environment = null;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		T copy = (T) ObjectCloner.xmlClone(component);// ComponentOperator.cloner.deepClone(component);
		try
		{// FullComponentOperator.getOperator(component).loadHierarchy(h);
			if (!include_hierarchy)
			{
				ComponentWorker.getOperator(component).loadHierarchy(h);
			}
			if (!include_data)
			{
				for (Data data : component.component().getContent().getObjects(Data.class, true))
				{
					// tempValues.put(data, Data.getStoredValues(data));
					DataOperator.getOperator(data).loadStoredValues(tempValues.get(data));
				}
			}
		} catch (Exception e)
		{

		}
		return copy;

	}

	/*
	 * Accesses the global environment containing all other components and the
	 * time domains
	 * 
	 * @return global environment component
	 */
	public HybridEnvironment getEnvironment()
	{
		return EnvironmentOperator.getGlobalSystem(component);
	}

	/*
	 * Accesses information about this component for naming conventions
	 * 
	 * @return component information
	 */
	public ComponentLabel getLabels()
	{
		return component.labels;
	}

	/*
	 * Accesses an organized data structure for accessing objects within this
	 * components hierarchy
	 * 
	 * @return component organizer
	 */
	public ComponentContent getContent()
	{
		return component.contents;
	}

	/*
	 * Accesses an organized data structure for accessing objects within this
	 * components hierarchy
	 * 
	 * @return component organizer
	 */
	public ComponentConfigurer configure()
	{
		return ComponentConfigurer.getOperator(component);
	}

	/*
	 * Save the current component to a file
	 */
	public void save(File file)
	{
		save(file, getSettings().getDataSettings().saveDataToFileDefault,
		getSettings().getDataSettings().saveSettingsToFileDefault);
	}

	/*
	 * Save the current component to a file
	 */
	public void save(File file, boolean save_data)
	{
		save(file, save_data, getSettings().getDataSettings().saveSettingsToFileDefault);
	}

	/*
	 * Save the current component to a file
	 */
	public void save(File file, boolean save_data, boolean save_settings)
	{
		FileContent[] contents = FileContent.getContentArray(save_data, save_settings);

		FileProcessor.store(file, component, contents);

	}

	/*
	 * Access the address of the component, which is unique for every instance
	 */
	public String getAddress()
	{
		if (component.labels.address == null)
		{
			ComponentWorker.getOperator(component).generateAddress();
		}
		return component.labels.address;
	}

	/*
	 * Access the settings
	 */
	public SettingConfigurer getSettings()
	{
		return EnvironmentOperator.getOperator(component.component().getEnvironment()).getManager().getSettings();
	}

	/*
	 * Environment key generated by environment that contains this component
	 */
	public String getEnvironmentKey()
	{
		return component.contents.environmentKey;
	}
}
