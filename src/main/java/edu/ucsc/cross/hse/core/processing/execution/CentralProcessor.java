package edu.ucsc.cross.hse.core.processing.execution;

import com.be3short.data.cloning.ObjectCloner;

import bs.commons.objects.access.FieldFinder;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
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

	protected Thread environmentThread;

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
	protected void initializeProcessingElements()
	{
		Double runTime = 0.0;

		try
		{
			runTime = executionMonitor.getRunTime();
		} catch (Exception e)
		{

		}
		contentAdmin = ContentOperator.getOperator(environmentInterface.getContents());
		simulationEngine = new SimulationEngine(this);
		dataHandler = new DataHandler(this);
		systemConsole = new SystemConsole(this);
		componentAdmin = new ComponentAdministrator(this);
		fileExchanger = new FileExchanger(this);
		jumpEvaluator = new JumpEvaluator(this);
		interruptResponder = new InterruptResponder(this);
		executionMonitor = new ExecutionMonitor(this, runTime);
	}

	/*
	 * Get a runnable version of the integration task for use when running
	 * threadded
	 */
	private Runnable getEnvironmentTask(boolean resume)
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{

				launchEnvironment(resume);
				while (!environmentThread.isInterrupted())
				{

				}
				interruptResponder.interruptEnv();
			}

		};
		return task;

	}

	protected void start()
	{
		start(false);
	}

	protected void start(boolean resume)
	{
		environmentThread = new Thread(getEnvironmentTask(resume));
		environmentThread.start();
	}

	protected void launchEnvironment(boolean resume_paused)
	{
		Boolean running = false;
		while (!running)
		{
			if (!resume_paused)
			{
				systemConsole.print("Environment Started");
				EnvironmentContent content = (EnvironmentContent) ObjectCloner
				.xmlClone(environmentInterface.getContents());
				prepareEnvironment(content);
			} else
			{
				systemConsole.print("Environment Resumed");
				prepareEnvironment(environmentInterface.getContents());
			}
			executeEnvironment();
			running = !interruptResponder.isTerminating();
			running = running || interruptResponder.isPauseTemporary();
			running = running || interruptResponder.isTerminatedEarly();
			running = running || !environmentInterface.getSettings().getExecutionSettings().rerunOnFatalErrors;
			running = running || ComponentOperator.getOperator(environmentInterface.content).outOfAllDomains()
			&& !interruptResponder.isOutsideDomainError();
		}
		systemConsole
		.print("Environment Stopped - Simulation Time: " + environmentInterface.getContents().getEnvironmentTime()
		+ " sec - Run Time : " + executionMonitor.getRunTime() + "sec");
	}

	/*
	 * Gets the environment ready for execution
	 */
	public void executeEnvironment()
	{
		contentAdmin = ContentOperator.getOperator(environmentInterface.getContents());
		while (this.contentAdmin.isJumpOccurring())
		{
			this.componentAdmin.performAllTasks(true);
			contentAdmin.getEnvironmentHybridTime().incrementJumpIndex();
		}
		this.contentAdmin.performTasks(false);
		executionMonitor.runSim(true);// environmentInterface.getSettings().getExecutionSettings().runThreadded);
	}

	/*
	 * Gets the environment ready for execution
	 */
	public void prepareEnvironment(EnvironmentContent content)
	{
		// content.getContents().constructTree();
		environmentInterface.content = content;
		contentAdmin = ContentOperator.getOperator(content);
		initializeProcessingElements();
		contentAdmin.prepareEnvironmentContent();
		storeConfigurations();
		dataHandler.loadStoreStates();
		simulationEngine.initialize();
		storeConfigurations();
	}

	/*
	 * Store the state of all components right before a simulation begins to
	 * allow the simulation to be run again in the future
	 */
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

	private void initializeComponents(boolean pre_loaded_content)
	{
		environmentInterface.environments.put(environmentInterface.content.toString(), this.environmentInterface);
		environmentInterface.settings = FileExchanger.loadSettings(null);
		environmentInterface.processor = new CentralProcessor(this.environmentInterface);
		if (pre_loaded_content)
		{
			dataHandler.loadStoreStates();
		}
	}

	protected void resetComponents(boolean re_initialize)
	{
		for (Component component : environmentInterface.content.getContents().getComponents(true))
		{
			if (FieldFinder.containsSuper(component, Data.class))
			{
				Data data = (Data) component;
				data.getActions().getStoredValues().clear();

			}
			ComponentOperator.getOperator(component).setInitialized(!re_initialize);
		}
	}
}
