package edu.ucsc.cross.hybrid.env.core.processing;

import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;
import edu.ucsc.cross.hybrid.env.core.containers.EnvironmentContent;

public class Environment//implements Environment
{

	// Execution
	EnvironmentContent environment;
	ElementManager elements;
	DataCollector data;
	SimulationEngine simEngine;
	ExecutionMonitor simThread;
	FileParser saver;
	OutputPrinter notifier; // system notification manager ie % complet

	public Environment()
	{
		this.environment = new EnvironmentContent();
		initializeComponents();
	}

	public Environment(EnvironmentContent environment)
	{
		this.environment = environment;
		initializeComponents();
	}

	public void loadEnvironment(EnvironmentContent environment)
	{
		this.environment = environment;
	}

	private void initializeComponents()
	{

		//queue = new ArrayList<Environment>();
		simEngine = new SimulationEngine(this);
		simThread = new ExecutionMonitor(this);
		data = new DataCollector(this);
		notifier = new OutputPrinter(this);
		elements = new ElementManager(this);
		saver = new FileParser(this);

		//componentMonitor = new ComponentOperator(this);
	}

	public void start()
	{
		prepareEnvironment();
		environment.getStartTime().seconds(Double.valueOf(System.currentTimeMillis()) / 1000);//Time.newSecondValue(0.0);
		simThread.runSim(true);
	}

	public <S extends HybridSystem> void addSystem(S component)
	{
		environment.addSystem(component, 1);
	}

	public <S extends HybridSystem> void addSystem(S component, Integer quantity)
	{
		environment.addSystem(component, quantity);
	}

	public EnvironmentContent getEnvironment()
	{
		return environment;
	}

	public FileParser getDataFileIOUtility()
	{
		return saver;
	}

	public DataCollector getDataCollector()
	{
		return data;
	}

	public void setSettings(SettingConfigurer settings)
	{
		this.environment.setSettings(settings);
	}

	public SettingConfigurer getSettings()
	{
		return this.environment.getSettings();
	}

	public ElementManager getElementOperator()
	{
		return elements;
	}

	public void prepareEnvironment()
	{
		//environment.scanAllSystems();
		elements.initialize();
		simEngine.initialize();
		data.loadStoreStates();
	}

}
