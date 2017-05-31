package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.environment.GlobalSystem;
import edu.ucsc.cross.hse.core.object.settings.SettingConfigurations;
import edu.ucsc.cross.hse.core.procesing.output.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataManager;
import edu.ucsc.cross.hse.core.processing.data.FileParser;
import edu.ucsc.cross.hse.core.processing.event.EventMonitor;

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
