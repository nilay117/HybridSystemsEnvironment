package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.ComponentHierarchy;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.processing.data.DataManager;
import edu.ucsc.cross.hse.core.processing.settings.SettingConfigurations;

public class Environment // extends ProcessorAccess// implements Environment
{

	private EnvironmentContent content; // all elements that make up the
										// environment
	// itself such as data, components, systems
	// etc
	private Processor processor; // environment processor that handles events,
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
		content = new EnvironmentContent();
		initializeComponents(false);
	}

	/*
	 * Named environment constructor that initializes an empty system with a
	 * specified name and default settings
	 */
	public Environment(String name)
	{
		content = new EnvironmentContent(name);
		initializeComponents(false);
	}

	/*
	 * Predefined environment constructor that loads a specified environment and
	 * default settings
	 */
	public Environment(EnvironmentContent environment)
	{
		content = environment;
		initializeComponents(true);
	}

	public ComponentHierarchy getContent()
	{
		return content.getHierarchy();
	}

	public DataManager getDataCollector()
	{
		return processor.data;// ;.getData();
	}

	public SettingConfigurations settings()
	{
		return settings;
	}

	public Processor execution()
	{
		return processor;
	}
	// public FileParser getFileParser()
	// {
	// return fileParser;
	// }

	// public void setEnvironmentContent(GlobalSystem environmentContent)
	// {
	// this.environmentContent = environmentContent;
	// }

	public void initializeComponents(boolean pre_loaded_content)
	{
		settings = SettingConfigurations.loadSettings();
		processor = new Processor(this);
		if (pre_loaded_content)
		{
			this.getDataCollector().loadStoreStates();
		}
	}

	private void initializeComponents(SettingConfigurations settings)
	{
		this.settings = settings;
		processor = new Processor(this);
	}

	public void start()
	{
		processor.start();
	}

	// Configration Functionsß
	public void setSettings(SettingConfigurations settings)
	{
		this.settings = settings;
	}

	protected EnvironmentContent getEnvironmentContent()
	{
		return content;
	}

	public static EnvironmentContent getEnvironmentSystem(Environment env)
	{
		return env.content;
	}
}
