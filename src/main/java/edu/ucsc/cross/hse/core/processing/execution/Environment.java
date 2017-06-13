package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.ComponentHierarchy;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystem;
import edu.ucsc.cross.hse.core.processing.data.DataManager;
import edu.ucsc.cross.hse.core.processing.settings.SettingConfigurations;

public class Environment // extends ProcessorAccess// implements Environment
{

	private GlobalSystem content; // all elements that make up the environment
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
		content = new GlobalSystem();
		initializeComponents();
	}

	/*
	 * Named environment constructor that initializes an empty system with a
	 * specified name and default settings
	 */
	public Environment(String name)
	{
		content = new GlobalSystem(name);
		initializeComponents();
	}

	/*
	 * Predefined environment constructor that loads a specified environment and
	 * default settings
	 */
	public Environment(GlobalSystem environment)
	{
		content = environment;
		initializeComponents();
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

	private void initializeComponents()
	{
		settings = SettingConfigurations.loadSettings();
		processor = new Processor(this);
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

	protected GlobalSystem getEnvironmentContent()
	{
		return content;
	}

	public static GlobalSystem getEnvironmentSystem(Environment env)
	{
		return env.content;
	}
}
