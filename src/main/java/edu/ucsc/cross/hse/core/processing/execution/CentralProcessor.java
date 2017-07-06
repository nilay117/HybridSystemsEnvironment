package edu.ucsc.cross.hse.core.processing.execution;

import java.util.ArrayList;

import com.be3short.data.cloning.ObjectCloner;

import bs.commons.objects.access.FieldFinder;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
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
			runTime = integrationMonitor.getRunTime();
		} catch (Exception e)
		{

		}
		contentAdmin = ContentOperator.getOperator(environmentInterface.getE());
		simulationEngine = new SimulationEngine(this);
		dataHandler = new DataHandler(this);
		systemConsole = new SystemConsole(this);
		componentAdmin = new ComponentAdministrator(this);
		fileExchanger = new FileExchanger(this);
		jumpEvaluator = new JumpEvaluator(this);
		interruptResponder = new InterruptResponder(this);
		integrationMonitor = new ExecutionMonitor(this, runTime);
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

				runEnvironment(resume);
			}

		};
		return task;

	}

	protected void launchEnvironment()
	{
		launchEnvironment(false);
	}

	protected void launchEnvironment(boolean resume)
	{
		if (environmentInterface.getSettings().getExecutionSettings().runThreadded)
		{
			environmentThread = new Thread(getEnvironmentTask(resume));
			environmentThread.start();
		} else
		{
			runEnvironment(resume);
		}
	}

	protected void runEnvironment(boolean resume)
	{
		Boolean running = false;
		while (!running)
		{
			runExecution(resume);
			running = repeatRun();
		}
		adknowledgeSuccess();
	}

	private void runExecution(boolean resume)
	{
		if (!resume)
		{
			resetEnvironment();
		}
		prepareEnvironment(!resume);
		startExecution();
	}

	private boolean repeatRun()
	{
		Boolean repeat = !interruptResponder.isTerminating();
		repeat = repeat || interruptResponder.isPauseTemporary();
		repeat = repeat || interruptResponder.isTerminatedEarly();
		repeat = repeat || !environmentInterface.getSettings().getExecutionSettings().rerunOnFatalErrors;
		repeat = repeat || ComponentOperator.getOperator(environmentInterface.content).outOfAllDomains()
		&& !interruptResponder.isOutsideDomainError();
		return repeat;
	}

	private void adknowledgeSuccess()
	{
		if (!interruptResponder.isTerminating() && !interruptResponder.isTerminatedEarly()
		&& !interruptResponder.isPauseTemporary())
		{
			systemConsole.print(
			"Environment Execution Completed - Simulation Time: " + environmentInterface.content.getEnvironmentTime()
			+ " sec - Run Time : " + integrationMonitor.getRunTime() + "sec");
		}
	}

	protected void resetEnvironment()
	{
		resetEnvironment(false);
	}

	protected void resetEnvironment(boolean reinitialize_data)
	{
		ArrayList<Data> dat = environmentInterface.content.getContent().getData(true);
		for (Component data : environmentInterface.content.getContent().getComponents(true))
		{
			if (!dat.contains(dat))
			{
				data.getConfiguration().setInitialized(false);
			}
		}
		for (Data data : dat)
		{
			DataOperator.getOperator(data).resetData();
			data.getConfiguration()
			.setInitialized(ComponentOperator.getOperator(data).isInitialized() || reinitialize_data);
		}
	}

	/*
	 * Gets the environment ready for execution
	 */
	public void startExecution()
	{
		contentAdmin = ContentOperator.getOperator(environmentInterface.getE());
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
	public void prepareEnvironment(EnvironmentContent content)
	{
		if (content != null)
		{
			environmentInterface.content = content;
		}
		prepareEnvironment();
	}

	public void prepareEnvironment()
	{
		prepareEnvironment(true);
	}

	public void prepareEnvironment(boolean reset_content)
	{
		contentAdmin = ContentOperator.getOperator(environmentInterface.content);
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
		ComponentOperator.getOperator(this.environmentInterface.getE()).storeConfiguration();
		for (Component component : this.environmentInterface.getE().getContent().getComponents(true))
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

	public static void refreshIfDataPresent(HybridEnvironment env, Component component)
	{
		boolean loadData = false;
		for (Component comp : env.content.getContent().getComponents(true))
		{
			System.out.println(comp.toString());
			try
			{

				for (Data data : comp.getContent().getData(true))
				{
					if (data.getActions().getStoredValues().size() > 0)
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
		if (loadData)
		{
			// processor = new CentralProcessor(this);
			env.processor.prepareEnvironment();
			// processor.dataHandler.loadStoreStates();
		}
	}

}
