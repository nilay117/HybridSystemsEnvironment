package edu.ucsc.cross.hse.core.processing.management;

import edu.ucsc.cross.hse.core.component.system.GlobalHybridSystem;
import edu.ucsc.cross.hse.core.procesing.output.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataCollector;
import edu.ucsc.cross.hse.core.processing.data.FileParser;
import edu.ucsc.cross.hse.core.processing.event.ExecutionMonitor;

public class Environment extends ProcessorAccess// implements Environment
{

	// Execution
	GlobalHybridSystem environmentContent;
	ComponentDirector elements;
	DataCollector data;
	SimulationEngine simulationEngine;
	ExecutionMonitor executionMonitor;
	FileParser fileParser;
	SystemConsole outputPrinter; // system notification manager ie % complet

	public Environment()
	{
		super(null);
		this.environmentContent = new GlobalHybridSystem();
		initializeComponents();
	}

	public Environment(String name)
	{
		super(null);
		this.environmentContent = new GlobalHybridSystem(name);
		initializeComponents();
	}

	public Environment(GlobalHybridSystem environment)
	{
		super(null);
		this.environmentContent = environment;
		initializeComponents();
	}

	public GlobalHybridSystem getEnvironmentContent()
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

	public void setEnvironmentContent(GlobalHybridSystem environmentContent)
	{
		this.environmentContent = environmentContent;
	}

	private void initializeComponents()
	{
		super.processor = this;
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

}
