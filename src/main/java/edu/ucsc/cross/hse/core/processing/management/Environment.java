package edu.ucsc.cross.hse.core.processing.management;

import edu.ucsc.cross.hse.core.component.system.GlobalHybridSystem;
import edu.ucsc.cross.hse.core.object.settings.SettingConfigurations;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataCollector;
import edu.ucsc.cross.hse.core.processing.data.FileParser;
import edu.ucsc.cross.hse.core.processing.data.SystemConsole;
import edu.ucsc.cross.hse.core.processing.event.ExecutionMonitor;

public class Environment//implements Environment
{

	// Execution
	GlobalHybridSystem environmentContent;
	ComponentOperator elements;
	DataCollector data;
	SimulationEngine simulationEngine;
	ExecutionMonitor executionMonitor;
	FileParser fileParser;
	SystemConsole outputPrinter; // system notification manager ie % complet

	public Environment()
	{
		this.environmentContent = new GlobalHybridSystem();
		initializeComponents();
	}

	public Environment(GlobalHybridSystem environment)
	{
		this.environmentContent = environment;
		initializeComponents();
	}

	public void loadEnvironment(GlobalHybridSystem environment)
	{
		this.environmentContent = environment;
	}

	private void initializeComponents()
	{
		simulationEngine = new SimulationEngine(this);
		executionMonitor = new ExecutionMonitor(this);
		data = new DataCollector(this);
		outputPrinter = new SystemConsole(this);
		elements = new ComponentOperator(this);
		fileParser = new FileParser(this);
	}

	public void start()
	{
		prepareEnvironment();
		environmentContent.getStartTime().seconds(Double.valueOf(System.currentTimeMillis()) / 1000);//Time.newSecondValue(0.0);
		executionMonitor.runSim(true);
	}

	public GlobalHybridSystem environmentContent()
	{
		return environmentContent;
	}

	public FileParser fileParser()
	{
		return fileParser;
	}

	public DataCollector dataCollector()
	{
		return data;
	}

	public void setSettings(SettingConfigurations settings)
	{
		this.environmentContent.setSettings(settings);
	}

	public SettingConfigurations getSettings()
	{
		return this.environmentContent.getSettings();
	}

	public ComponentOperator getElementOperator()
	{
		return elements;
	}

	public void prepareEnvironment()
	{
		//environment.scanAllSystems();
		elements.initialize();
		simulationEngine.initialize();
		data.loadStoreStates();
	}

}
