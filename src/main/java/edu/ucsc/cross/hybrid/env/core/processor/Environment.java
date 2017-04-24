package edu.ucsc.cross.hybrid.env.core.processor;

import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;
import edu.ucsc.cross.hybrid.env.core.settings.SettingCollection;
import edu.ucsc.cross.hybrid.env.core.structure.EnvironmentElements;

public class Environment//implements Environment
{

	// Execution
	EnvironmentElements environment;
	ElementOperator elements;
	DataCollector data;
	SimulationEngine simEngine;
	ExecutionManager simThread;
	IOParser saver;
	OutputConsole notifier; // system notification manager ie % complet

	public Environment()
	{
		this.environment = new EnvironmentElements();
		initializeComponents();
	}

	public Environment(EnvironmentElements environment)
	{
		this.environment = environment;
		initializeComponents();
	}

	public void loadEnvironment(EnvironmentElements environment)
	{
		this.environment = environment;
	}

	private void initializeComponents()
	{

		//queue = new ArrayList<Environment>();
		simEngine = new SimulationEngine(this);
		simThread = new ExecutionManager(this);
		data = new DataCollector(this);
		notifier = new OutputConsole(this);
		elements = new ElementOperator(this);
		saver = new IOParser(this);

		//componentMonitor = new ComponentOperator(this);
	}

	public void start()
	{
		prepareEnvironment();
		environment.getStartTime().seconds(Double.valueOf(System.currentTimeMillis()) / 1000);//Time.newSecondValue(0.0);
		simThread.runSim(false);
	}

	public <S extends HybridSystem> void addSystem(S component)
	{
		environment.addSystem(component, 1);
	}

	public <S extends HybridSystem> void addSystem(S component, Integer quantity)
	{
		environment.addSystem(component, quantity);
	}

	public EnvironmentElements getEnvironment()
	{
		return environment;
	}

	public IOParser getDataFileIOUtility()
	{
		return saver;
	}

	public DataCollector getDataCollector()
	{
		return data;
	}

	public void setSettings(SettingCollection settings)
	{
		this.environment.setSettings(settings);
	}

	public SettingCollection getSettings()
	{
		return this.environment.getSettings();
	}

	public ElementOperator getElementOperator()
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
