package edu.ucsc.cross.hse.core.processing.execution;

import java.util.HashMap;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOrganizer;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.processing.data.DataAccessor;
import edu.ucsc.cross.hse.core.processing.data.DataHandler;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;

public class HybridEnvironment
{

	private static HashMap<String, HybridEnvironment> environments = new HashMap<String, HybridEnvironment>();

	private EnvironmentContent content; // all elements that make up the
	// environment
	// itself such as data, components, systems
	// etc
	protected CentralProcessor processor; // environment processor that handles events,
	// computations, maintenance, etc
	private SettingConfigurer settings; // collection of settings that
										// configure the performance of the
										// environment

	/*
	 * Generic environment constructor that initializes an empty system with a
	 * default name and settings
	 */
	public HybridEnvironment()
	{
		content = new EnvironmentContent();
		initializeComponents(false);
	}

	/*
	 * Named environment constructor that initializes an empty system with a
	 * specified name and default settings
	 */
	public HybridEnvironment(String name)
	{
		content = new EnvironmentContent(name);
		initializeComponents(false);
	}

	/*
	 * Predefined environment constructor that loads a specified environment and
	 * default settings
	 */
	public HybridEnvironment(EnvironmentContent environment)
	{
		content = environment;
		initializeComponents(true);
	}

	public void start()
	{
		processor.start();
	}

	public void start(Double duration)
	{
		settings.getExecutionSettings().simDuration = duration;
		processor.start();
	}

	public void start(Time duration)
	{
		settings.getExecutionSettings().simDuration = duration.seconds();
		processor.start();
	}

	public void stop()
	{
		stop(true);
	}

	public void stop(boolean terminate)
	{
		processor.interruptResponder.killSim();
	}

	public void reset()
	{
		reset(false);
	}

	public void reset(boolean clear_contents)
	{

	}

	public void saveContents(String directory, String file_name)
	{

	}

	public void saveSettings(String directory, String file_name)
	{

	}

	public void loadSettings(String directory, String file_name)
	{

	}

	public void loadContents(String directory, String file_name)
	{

	}

	public DataAccessor getDataAccessor()
	{
		return processor.dataHandler;
	}

	public SettingConfigurer getSettings()
	{
		return settings;
	}

	public void loadSettings(Object... settings)
	{
		if (settings.length == 1)
		{
			if (settings.getClass().equals(SettingConfigurer.class))
			{
				this.settings = (SettingConfigurer) settings[0];
			}
		}
	}

	public EnvironmentContent getContents()
	{
		return content;
	}

	public void loadContents(EnvironmentContent content)
	{
		this.content = content;
	}

	public void addComponents(Component... components)
	{
		for (Component component : components)
		{
			content.getContents().addComponent(component);
		}
	}

	public void addComponents(Component component, Integer quantity)
	{

		content.getContents().addComponent(component, quantity);

	}

	private void initializeComponents(boolean pre_loaded_content)
	{
		environments.put(content.toString(), this);
		settings = FileExchanger.loadSettings();
		processor = new CentralProcessor(this);
		if (pre_loaded_content)
		{
			processor.dataHandler.loadStoreStates();
		}
	}

	public static HybridEnvironment getEnvironment(String content_id)
	{
		if (environments.containsKey(content_id))
		{
			return environments.get(content_id);
		} else
		{
			return null;
		}
	}
}
