package edu.ucsc.cross.hse.core.processing.execution;

import java.util.ArrayList;

import com.be3short.data.cloning.ObjectCloner;

import bs.commons.objects.access.FieldFinder;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.FullComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentOperator;
import edu.ucsc.cross.hse.core.framework.environment.HybridEnvironment;
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

	protected EnvironmentManager environmentInterface; // main user interface

	protected EnvironmentOperator contentAdmin; // operates the main
	// global environment
	// component

	protected ComponentDirector componentAdmin; // controls all components

	protected DataHandler dataHandler; // handles the storage and access of data

	protected SimulationEngine simulationEngine; // performs the computations
													// necessary for the ode to
													// produce simulated
													// solutions

	protected ExecutionMonitor integrationMonitor; // monitors and resolves any
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

	protected Thread environmentThread; // thread that the environment is
										// running on (if the environment is
										// running on a thread)

	/*
	 * Constructor called by the main user interface
	 */
	protected CentralProcessor(EnvironmentManager processor)
	{
		environmentInterface = processor;
		systemConsole = new SystemConsole(this);
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
			runTime = integrationMonitor.getRunTime();
		} catch (Exception e)
		{

		}
		contentAdmin = EnvironmentOperator.getOperator(environmentInterface.getEnvironment());
		simulationEngine = new SimulationEngine(this);
		dataHandler = new DataHandler(this);
		systemConsole.initialize();
		// systemConsole = new SystemConsole(this);
		componentAdmin = new ComponentDirector(this);
		fileExchanger = new FileExchanger(this);
		jumpEvaluator = new JumpEvaluator(this);
		interruptResponder = new InterruptResponder(this);
		integrationMonitor = new ExecutionMonitor(this, runTime);
	}

	/*
	 * Get a runnable version of the integration task for use when running
	 * threadded
	 */
	private Runnable getRunnableExecution(boolean resume)
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{

				runEnvironment(resume);
			}

		};
		return task;

	}

	/*
	 * Start up a new environment execution
	 */
	protected void startEnvironment()
	{
		startEnvironment(false);
	}

	/*
	 * Start up or resume an environment execution
	 */
	protected void startEnvironment(boolean resume)
	{
		if (environmentInterface.getSettings().getExecutionSettings().runThreadded)
		{
			environmentThread = new Thread(getRunnableExecution(resume));
			environmentThread.start();
		} else
		{
			runEnvironment(resume);
		}
	}

	/*
	 * Run the environment and adjust to correct any errors until the execution
	 * completes or is interrupted
	 */
	protected void runEnvironment(boolean resume)
	{
		Boolean running = false;
		while (!running)
		{
			attemptExecution(resume);
			running = repeatAttempt();
		}
		adknowledgeSuccess();
	}

	/*
	 * Attempt to execute the environment processing
	 */
	private void attemptExecution(boolean resume)
	{
		if (!resume)
		{
			resetEnvironment();
		}
		prepareEnvironment(!resume);
		runExecution();
	}

	/*
	 * Determine if the last execution attempt was successful or needs to be
	 * repeated
	 */
	private boolean repeatAttempt()
	{
		Boolean repeat = !interruptResponder.isTerminating();
		repeat = repeat || interruptResponder.isPaused();
		repeat = repeat || interruptResponder.isTerminatedEarly();
		repeat = repeat || !environmentInterface.getSettings().getExecutionSettings().rerunOnFatalErrors;
		repeat = repeat || FullComponentOperator.getOperator(environmentInterface.content).outOfAllDomains()
		&& !interruptResponder.isErrorTermination();
		return repeat;
	}

	/*
	 * Print an output if an execution has completed succesfully
	 */
	private void adknowledgeSuccess()
	{
		if (!interruptResponder.isTerminating() && !interruptResponder.isTerminatedEarly()
		&& !interruptResponder.isPaused())
		{
			systemConsole.print(
			"Environment Execution Completed - Simulation Time: " + environmentInterface.content.getEnvironmentTime()
			+ " sec - Run Time : " + integrationMonitor.getRunTime() + "sec");
		}
	}

	/*
	 * reset the environment to its initial state
	 */
	protected void resetEnvironment()
	{
		resetEnvironment(false);
	}

	/*
	 * reset the environment to its initial state and also reinitialize the
	 * components
	 */
	protected void resetEnvironment(boolean reinitialize_data)
	{
		this.contentAdmin.initializeTimeDomains();
		ArrayList<Data> dat = environmentInterface.content.component().getContent().getData(true);
		for (Component data : environmentInterface.content.component().getContent().getComponents(true))
		{
			if (!dat.contains(dat))
			{
				data.component().configure().setInitialized(false);
			}
		}
		for (Data data : dat)
		{
			DataOperator.getOperator(data).resetData();
			data.component().configure()
			.setInitialized(!FullComponentOperator.getOperator(data).isInitialized() || reinitialize_data);
		}

	}

	/*
	 * Gets the environment ready for execution
	 */
	public void runExecution()
	{
		contentAdmin = EnvironmentOperator.getOperator(environmentInterface.getEnvironment());
		if (this.contentAdmin.isJumpOccurring())
		{
			contentAdmin.storeData();
			componentAdmin.performAllTasks(true);
			componentAdmin.getEnvironmentOperator().getEnvironmentHybridTime()
			.setTime(componentAdmin.getEnvironmentOperator().getEnvironmentHybridTime().getTime() + Double.MIN_VALUE);
			contentAdmin.storeData();
		}
		integrationMonitor.launchEnvironment();
	}

	/*
	 * Gets the environment ready for execution
	 */
	public void prepareEnvironment(HybridEnvironment content)
	{
		if (content != null)
		{
			environmentInterface.content = content;
		}
		prepareEnvironment(true);
	}

	/*
	 * Prepare the environment for a new execution
	 */
	public void prepareEnvironment()
	{
		prepareEnvironment(true);
	}

	/*
	 * Prepare the environment for a new execution but reset content depending
	 * on the input flag
	 */
	public void prepareEnvironment(boolean reset_content)
	{
		contentAdmin = EnvironmentOperator.getOperator(environmentInterface.content);
		initializeProcessingElements();
		if (reset_content)
		{
			contentAdmin.initializeEnvironmentContent();
		}
		storeConfigurations();
		dataHandler.loadStoreStates();
		simulationEngine.initialize();
		// storeConfigurations();
	}

	/*
	 * Store the state of all components right before a simulation begins to
	 * allow the simulation to be run again in the future
	 */
	protected void storeConfigurations()
	{
		FullComponentOperator.getOperator(this.environmentInterface.getEnvironment()).storeConfiguration();
		for (Component component : this.environmentInterface.getEnvironment().component().getContent()
		.getComponents(true))
		{
			FullComponentOperator.getOperator(component).storeConfiguration();
		}
	}

	/*
	 * Correct any setting issues that would interfere with performance
	 */
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

	/*
	 * Refresh the data processing components of an environment if new data is
	 * detected
	 */
	public static void refreshIfDataPresent(EnvironmentManager env, Component component)
	{
		boolean loadData = false;
		for (Component comp : env.content.component().getContent().getComponents(true))
		{
			System.out.println(comp.toString());
			try
			{

				for (Data data : comp.component().getContent().getData(true))
				{
					if (data.component().getStoredValues().size() > 0)
					{
						loadData = true;
						break;
					}
				}
				if (loadData)
				{
					// processor = new CentralProcessor(this);
					break;
					// processor.dataHandler.loadStoreStates();
				}
			} catch (Exception noStates)
			{
				noStates.printStackTrace();
			}
		}
		// if (loadData)
		{
			// env.processor = new CentralProcessor(env);
			env.processor.prepareEnvironment();
			// env.processor.dataHandler.loadStoreStates();
		}
	}

}
