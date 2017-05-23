package edu.ucsc.cross.hybrid.env.core.processing;

import edu.ucsc.cross.hybrid.env.core.containers.EnvironmentContent;
import edu.ucsc.cross.hybrid.env.core.containers.SettingConfiguration;

public class Environment//implements Environment
{

	// Execution
	EnvironmentContent environmentContent;
	ElementManager elements;
	DataCollector data;
	SimulationEngine simulationEngine;
	ExecutionMonitor executionMonitor;
	FileParser fileParser;
	SystemConsole outputPrinter; // system notification manager ie % complet

	public Environment()
	{
		this.environmentContent = new EnvironmentContent();
		initializeComponents();
	}

	public Environment(EnvironmentContent environment)
	{
		this.environmentContent = environment;
		initializeComponents();
	}

	public void loadEnvironment(EnvironmentContent environment)
	{
		this.environmentContent = environment;
	}

	private void initializeComponents()
	{
		simulationEngine = new SimulationEngine(this);
		executionMonitor = new ExecutionMonitor(this);
		data = new DataCollector(this);
		outputPrinter = new SystemConsole(this);
		elements = new ElementManager(this);
		fileParser = new FileParser(this);
	}

	public void start()
	{
		prepareEnvironment();
		environmentContent.getStartTime().seconds(Double.valueOf(System.currentTimeMillis()) / 1000);//Time.newSecondValue(0.0);
		executionMonitor.runSim(true);
	}

	public EnvironmentContent environmentContent()
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

	public void setSettings(SettingConfiguration settings)
	{
		this.environmentContent.setSettings(settings);
	}

	public SettingConfiguration getSettings()
	{
		return this.environmentContent.getSettings();
	}

	public ElementManager getElementOperator()
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
