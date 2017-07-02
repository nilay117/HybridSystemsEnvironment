package edu.ucsc.cross.hse.core.processing.execution;

import com.be3short.data.cloning.ObjectCloner;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.environment.ContentOperator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;
import edu.ucsc.cross.hse.core.processing.computation.SimulationEngine;
import edu.ucsc.cross.hse.core.processing.data.DataHandler;
import edu.ucsc.cross.hse.core.processing.event.ExecutionMonitor;
import edu.ucsc.cross.hse.core.processing.event.InterruptResponder;
import edu.ucsc.cross.hse.core.processing.event.JumpEvaluator;

/*
 * This class is the back end of the HybridEnvironment class responsible for
 * task execution and management of processing elements. It also provides an
 * interface for the processing elements to interact in order to run the
 * environment and complete user requests.
 */
public class CentralProcessor
{

	protected HybridEnvironment environmentInterface; // main user interface

	protected ContentOperator contentAdmin; // operates the main
											// global environment
											// component

	protected ComponentAdministrator componentAdmin; // controls all components

	protected DataHandler dataHandler; // handles the storage and access of data

	protected SimulationEngine simulationEngine; // performs the computations
													// necessary for the ode to
													// produce simulated
													// solutions

	protected ExecutionMonitor executionMonitor; // monitors and resolves any
													// issues that arise while
													// the environment is
													// running

	protected JumpEvaluator jumpEvaluator; // continuously detects and handles
											// discrete events

	protected InterruptResponder interruptResponder; // reacts to user or system
	// interruptions to keep the
	// system from crashing

	protected FileExchanger fileExchanger; // manages all file input and output
											// tasks

	protected SystemConsole systemConsole; // prints system notifications and
											// any user defined outputs

	/*
	 * Constructor called by the main user interface
	 */
	protected CentralProcessor(HybridEnvironment processor)
	{
		environmentInterface = processor;
		initializeProcessingElements();
	}

	/*
	 * Initializes all processing elements and links them to this central
	 * processor, which allows them to access each other
	 */
	public void initializeProcessingElements()
	{
		contentAdmin = ContentOperator.getContentAdministrator(environmentInterface.getContents());
		simulationEngine = new SimulationEngine(this);
		dataHandler = new DataHandler(this);
		systemConsole = new SystemConsole(this);
		componentAdmin = new ComponentAdministrator(this);
		fileExchanger = new FileExchanger(this);
		jumpEvaluator = new JumpEvaluator(this);
		interruptResponder = new InterruptResponder(this);
		executionMonitor = new ExecutionMonitor(this);
	}

	/*
	 * Prepares and starts up the environment
	 */
	protected void start()
	{
		// HybridEnvironment c = (HybridEnvironment)
		// ObjectCloner.xmlClone(environmentInterface);
		// prepareEnvironment(environmentInterface.getContents());
		EnvironmentContent og = (EnvironmentContent) ObjectCloner.xmlClone(environmentInterface.getContents());
		// EnvironmentContent content = (EnvironmentContent)
		// ObjectCloner.xmlClone(environmentInterface.getContents());
		// this.environmentInterface = c;
		// c.loadContents((EnvironmentContent) ObjectCloner.xmlClone(ct));
		Boolean success = false;
		while (!success)
		{
			EnvironmentContent content = (EnvironmentContent) ObjectCloner.xmlClone(og);
			success = executeEnvironment(content);
			success = success || !environmentInterface.getSettings().getExecutionSettings().rerunOnFatalErrors;
			success = success
			|| (this.componentAdmin.outOfAllDomains() && !this.interruptResponder.isOutsideDomainError());
		}

	}

	/*
	 * Gets the environment ready for execution
	 */
	protected boolean executeEnvironment(EnvironmentContent content)
	{
		prepareEnvironment(content);
		simulationEngine.initialize();
		storeConfigurations();
		while (this.contentAdmin.isJumpOccurring())
		{
			this.componentAdmin.performAllTasks(true);
			contentAdmin.getEnvironmentHybridTime().incrementJumpIndex();
		}
		// this.contentAdmin.performTasks(false);
		interruptResponder = new InterruptResponder(this);
		return executionMonitor.runSim(false);// environmentInterface.getSettings().getExecutionSettings().runThreadded);
	}

	/*
	 * Gets the environment ready for execution
	 */
	public void prepareEnvironment(EnvironmentContent content)
	{
		environmentInterface.content = content;
		initializeProcessingElements();
		contentAdmin.prepareEnvironmentContent();
		dataHandler.loadStoreStates();
		simulationEngine.initialize();

	}

	protected void storeConfigurations()
	{
		ComponentOperator.getOperator(this.environmentInterface.getContents()).storeConfiguration();
		for (Component component : this.environmentInterface.getContents().getContents().getComponents(true))
		{
			ComponentOperator.getOperator(component).storeConfiguration();
		}
	}

	protected void correctPotentialSettingErrors()
	{
		// Double ehInterval =
		// environmentInterface.getSettings().getComputationSettings().ehMaxCheckInterval;
		// if (ehInterval <
		// environmentInterface.getSettings().getComputationSettings().odeMaxStep)
		// {
		// environmentInterface.getSettings().getComputationSettings().odeMaxStep
		// = ehInterval;
		// }
		if (environmentInterface.getSettings().getComputationSettings().odeMinStep > environmentInterface.getSettings()
		.getComputationSettings().odeMaxStep)
		{
			environmentInterface.getSettings().getComputationSettings().odeMinStep = environmentInterface.getSettings()
			.getComputationSettings().odeMaxStep / 2;
		}
		if (environmentInterface.getSettings().getDataSettings().dataStoreIncrement < environmentInterface.getSettings()
		.getComputationSettings().odeMinStep)
		{
			environmentInterface.getSettings().getDataSettings().dataStoreIncrement = environmentInterface.getSettings()
			.getComputationSettings().odeMinStep;
		}
	}

}
