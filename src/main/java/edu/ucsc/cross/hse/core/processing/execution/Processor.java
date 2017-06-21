package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentAdministrator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataAdministrator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.procesing.io.FileParser;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataManager;
import edu.ucsc.cross.hse.core.processing.event.EventMonitor;
import edu.ucsc.cross.hse.core.processing.settings.SettingConfigurations;

public class Processor
{

	protected Environment environment;
	protected ComponentDirector elements;
	protected DataManager data;
	protected SimulationEngine simulationEngine;
	protected EventMonitor executionMonitor;
	protected FileParser fileParser;
	protected SystemConsole outputPrinter; // system notification manager ie % complet

	protected Processor(Environment processor)
	{
		environment = processor;
		initializeComponents();
	}

	private void initializeComponents()
	{
		simulationEngine = new SimulationEngine(this);
		executionMonitor = new EventMonitor(this);
		data = new DataManager(this);
		outputPrinter = new SystemConsole(this);
		elements = new ComponentDirector(this);
		fileParser = new FileParser(this);
	}

	protected void prepareEnvironment()
	{
		// environment.scanAllSystems();
		elements.prepareComponents();
		simulationEngine.initialize();
		data.loadStoreStates();
	}

	public void start()
	{
		prepareEnvironment();
		executionMonitor.runSim(false);
	}
}
