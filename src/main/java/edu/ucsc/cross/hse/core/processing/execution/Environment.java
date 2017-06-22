package edu.ucsc.cross.hse.core.processing.execution;

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

	public ComponentOrganizer getContent()
	{
		return content.getContents();
	}

	public DataCollector getDataCollector()
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
		settings = new SettingConfigurations();// .loadSettings();
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

		this.getDataCollector().loadStoreStates();
		processor.start();
	}

	// Configration Functionsß
	public void setSettings(SettingConfigurations settings)
	{
		this.settings = settings;
	}

	protected GlobalEnvironmentContent getEnvironmentContent()
	{
		return content;
	}

	public static GlobalEnvironmentContent getEnvironmentSystem(Environment env)
	{
		return env.content;
	}
}
