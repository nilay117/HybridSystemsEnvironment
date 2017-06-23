package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentAdministrator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataAdministrator;
import edu.ucsc.cross.hse.core.framework.environment.GlobalContentAdministrator;
import edu.ucsc.cross.hse.core.framework.environment.GlobalEnvironmentContent;
import edu.ucsc.cross.hse.core.procesing.io.FileParser;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataHandler;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.event.EventMonitor;

public class Processor
{

	protected GlobalContentAdministrator contentAdmin;
	protected Environment environment;
	protected ComponentDirector elements;
	protected DataHandler data;
	protected SimulationEngine simulationEngine;
	protected EventMonitor executionMonitor;
	protected FileParser fileParser;
	protected SystemConsole outputPrinter; // system notification manager ie %
											// complet

	protected Processor(Environment processor)
	{
		environment = processor;
		initializeComponents();
	}

	protected void initializeComponents()
	{
		contentAdmin = GlobalContentAdministrator.getContentAdministrator(environment.getContents());
		simulationEngine = new SimulationEngine(this);
		executionMonitor = new EventMonitor(this);
		data = new DataHandler(this);
		outputPrinter = new SystemConsole(this);
		elements = new ComponentDirector(this);
		fileParser = new FileParser(this);
	}

	protected void prepareEnvironment()
	{
		// environment.scanAllSystems();

		contentAdmin = GlobalContentAdministrator.getContentAdministrator(environment.getContents());
		contentAdmin.prepareEnvironmentContent();
		simulationEngine.initialize();
		data.loadStoreStates();
	}

	protected void start()
	{
		prepareEnvironment();
		simulationEngine.initialize();
		executionMonitor.runSim(false);
	}
}
