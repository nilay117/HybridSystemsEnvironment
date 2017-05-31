package edu.ucsc.cross.hse.core.processing.execution;

import bs.commons.objects.access.CoreComponent;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentHierarchy;
import edu.ucsc.cross.hse.core.framework.environment.GlobalAccessor;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystem;
import edu.ucsc.cross.hse.core.framework.environment.HybridTime;
import edu.ucsc.cross.hse.core.object.settings.SettingConfigurations;
import edu.ucsc.cross.hse.core.procesing.output.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataCollector;
import edu.ucsc.cross.hse.core.processing.data.FileParser;
import edu.ucsc.cross.hse.core.processing.event.EventMonitor;

public class Environment //extends ProcessorAccess// implements Environment
{

	private GlobalSystem content; // all elements that make up the environment itself such as data, components, systems etc
	private Processor processor; // environment processor that handles events, computations, maintenance, etc 
	private SettingConfigurations settings; // collection of settings that configure the performance of the environment

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

	public ComponentHierarchy content()
	{
		return content.getHierarchy();
	}

	public DataCollector data()
	{
		return processor.data;//;.getData();
	}

	public SettingConfigurations settings()
	{
		return settings;
	}

	public Processor execution()
	{
		return processor;
	}
	//	public FileParser getFileParser()
	//	{
	//		return fileParser;
	//	}

	//	public void setEnvironmentContent(GlobalSystem environmentContent)
	//	{
	//		this.environmentContent = environmentContent;
	//	}

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
}
