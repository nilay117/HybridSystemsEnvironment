package edu.ucsc.cross.hse.core.processing.execution;

import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOrganizer;
import edu.ucsc.cross.hse.core.framework.environment.GlobalEnvironmentContent;
import edu.ucsc.cross.hse.core.processing.data.DataCollector;
import edu.ucsc.cross.hse.core.processing.settings.SettingConfigurations;

public class Environment // extends ProcessorAccess// implements Environment
{

	private GlobalEnvironmentContent content; // all elements that make up the
	// environment
	// itself such as data, components, systems
	// etc
	protected Processor processor; // environment processor that handles events,
									// computations, maintenance, etc
	private SettingConfigurations settings; // collection of settings that
											// configure the performance of the
											// environment

	/*
	 * Generic environment constructor that initializes an empty system with a
	 * default name and settings
	 */
	public Environment()
	{
		content = new GlobalEnvironmentContent();
		initializeComponents(false);
	}

	/*
	 * Named environment constructor that initializes an empty system with a
	 * specified name and default settings
	 */
	public Environment(String name)
	{
		content = new GlobalEnvironmentContent(name);
		initializeComponents(false);
	}

	/*
	 * Predefined environment constructor that loads a specified environment and
	 * default settings
	 */
	public Environment(GlobalEnvironmentContent environment)
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
		settings.trial().simDuration = duration;
		processor.start();
	}

	public void start(Time duration)
	{
		settings.trial().simDuration = duration.seconds();
		processor.start();
	}

	public void reset()
	{

	}

	public void save(String directory, String file_name)
	{

	}

	public DataCollector getData()
	{
		return processor.data;
	}

	public SettingConfigurations getSettings()
	{
		return settings;
	}

	public void setSettings(SettingConfigurations settings)
	{
		this.settings = settings;
	}

	public GlobalEnvironmentContent getEnvironment()
	{
		return content;
	}

	public void setEnvironment(GlobalEnvironmentContent content)
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
		settings = new SettingConfigurations();// .loadSettings();
		processor = new Processor(this);
		if (pre_loaded_content)
		{
			processor.data.loadStoreStates();
		}
	}

}
