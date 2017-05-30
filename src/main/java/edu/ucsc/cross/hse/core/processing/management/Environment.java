package edu.ucsc.cross.hse.core.processing.management;

import bs.commons.objects.access.CoreComponent;
import bs.commons.unitvars.values.Time;
import edu.ucsc.cross.hse.core.framework.environment.GlobalAccessor;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystem;
import edu.ucsc.cross.hse.core.framework.environment.HybridTime;
import edu.ucsc.cross.hse.core.object.settings.SettingConfigurations;
import edu.ucsc.cross.hse.core.procesing.output.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataCollector;
import edu.ucsc.cross.hse.core.processing.data.FileParser;
import edu.ucsc.cross.hse.core.processing.event.ExecutionMonitor;

public class Environment extends ProcessorAccess// implements Environment
{

	// Execution
	GlobalSystem environmentContent;
	ComponentDirector elements;
	DataCollector data;
	SimulationEngine simulationEngine;
	ExecutionMonitor executionMonitor;
	FileParser fileParser;
	SystemConsole outputPrinter; // system notification manager ie % complet
	@CoreComponent
	private SettingConfigurations settings; // settings configuration

	public Environment()
	{
		super(null);
		this.environmentContent = new GlobalSystem();
		initializeComponents();
	}

	public Environment(String name)
	{
		super(null);
		this.environmentContent = new GlobalSystem(name);
		initializeComponents();
	}

	public Environment(GlobalSystem environment)
	{
		super(null);
		this.environmentContent = environment;
		initializeComponents();
	}

	public GlobalSystem getEnvironmentContent()
	{
		return environmentContent;
	}

	public DataCollector getData()
	{
		return data;
	}

	public FileParser getFileParser()
	{
		return fileParser;
	}

	public void setEnvironmentContent(GlobalSystem environmentContent)
	{
		this.environmentContent = environmentContent;
	}

	private void initializeComponents()
	{
		super.processor = this;
		settings = SettingConfigurations.loadSettings();
		simulationEngine = new SimulationEngine(this);
		executionMonitor = new ExecutionMonitor(this);
		data = new DataCollector(this);
		outputPrinter = new SystemConsole(this);
		elements = new ComponentDirector(this);
		fileParser = new FileParser(this);
	}

	public void start()
	{
		prepareEnvironment();
		executionMonitor.runSim(false);
	}

	public void prepareEnvironment()
	{
		// environment.scanAllSystems();
		elements.prepareComponents();
		simulationEngine.initialize();
		data.loadStoreStates();
	}

	public SettingConfigurations getSettings()
	{
		return settings;
	}

	// Configration Functionsß
	public void setSettings(SettingConfigurations settings)
	{
		this.settings = settings;
	}

}
