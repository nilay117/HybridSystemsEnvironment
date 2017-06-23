package edu.ucsc.cross.hse.core.processing.execution;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.environment.GlobalContentAdministrator;
import edu.ucsc.cross.hse.core.framework.environment.GlobalEnvironmentContent;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataHandler;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.event.ExecutionMonitor;
import edu.ucsc.cross.hse.core.processing.event.InterruptResponder;
import edu.ucsc.cross.hse.core.processing.event.JumpEvaluator;

/*
 * This class is responsible for all task execution, and contains each of the
 * processing elements used by the environment. It is the back end of the
 * environment class that communicates with the processing elements to run the
 * environment and complete user requests
 */
public class CentralProcessor
{

	protected HybridEnvironment environment; // main user interface

	protected GlobalContentAdministrator contentAdmin; // operates the main
														// global environment
														// component

	protected ComponentAdministrator elements; // controls all components

	protected DataHandler data; // handles the storage and access of data

	protected SimulationEngine simulationEngine; // performs the computations
													// necessary for the ode to
													// produce simulated
													// solutions

	protected ExecutionMonitor executionMonitor; // monitors and resolves any
													// issues that arise while
													// the environment is
													// running

	protected JumpEvaluator jumpHandler; // continuously detects and handles
											// discrete events

	protected InterruptResponder terminator; // reacts to user or system
												// interruptions to keep the
												// system from crashing

	protected FileExchanger fileParser; // manages all file input and output
										// tasks

	protected SystemConsole outputPrinter; // prints system notifications and
											// any user defined outputs

	protected CentralProcessor(HybridEnvironment processor)
	{
		environment = processor;
		initializeProcessingElements();
	}

	protected void initializeProcessingElements()
	{
		contentAdmin = GlobalContentAdministrator.getContentAdministrator(environment.getContents());
		simulationEngine = new SimulationEngine(this);
		data = new DataHandler(this);
		outputPrinter = new SystemConsole(this);
		elements = new ComponentAdministrator(this);
		fileParser = new FileExchanger(this);
		jumpHandler = new JumpEvaluator(this);
		terminator = new InterruptResponder(this);
		executionMonitor = new ExecutionMonitor(this);
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
