
package edu.ucsc.cross.hse.core.operator;

import edu.ucsc.cross.hse.core.container.EnvironmentContent;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.container.EnvironmentOutputs;
import edu.ucsc.cross.hse.core.container.EnvironmentSettings;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.monitor.ComputationMonitor;
import edu.ucsc.cross.hse.core.monitor.DataMonitor;
import edu.ucsc.cross.hse.core.monitor.EventMonitor;
import edu.ucsc.cross.hse.core.monitor.JumpStatus;
import edu.ucsc.cross.hse.core.setting.ExecutionParameters;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class ExecutionOperator
{

	Console console;
	DataMonitor dataManager;
	Environment env;
	ObjectOperator exeContent;
	ComputationMonitor executionMonitor;
	EventMonitor jumpEvaluator;
	SimulationOperator simEngine;
	SystemOperator systemControl;

	public Console getConsole()
	{
		return console;
	}

	public EnvironmentContent getContents()
	{
		return env.getContents();
	}

	public EnvironmentData getDataCollector()
	{
		return env.getData();
	}

	public DataMonitor getDataManager()
	{
		return dataManager;
	}

	public Environment getEnvironment()
	{
		return env;
	}

	public ObjectOperator getExecutionContent()
	{
		return exeContent;
	}

	public ComputationMonitor getExecutionMonitor()
	{
		return executionMonitor;
	}

	public ExecutionParameters getExecutionParameters()
	{
		return env.getSettings().getExecutionParameters();
	}

	public EventMonitor getJumpEvaluator()
	{
		return jumpEvaluator;
	}

	public EnvironmentOutputs getOutputs()
	{
		return env.getOutputs();
	}

	public EnvironmentSettings getSettings()
	{
		return env.getSettings();
	}

	public SimulationOperator getSimEngine()
	{
		return simEngine;
	}

	public SystemOperator getSystemControl()
	{
		return systemControl;
	}

	public void initializeComponents()
	{
		simEngine = new SimulationOperator(this);
		jumpEvaluator = new EventMonitor(this);
		executionMonitor = new ComputationMonitor(this);
		dataManager = new DataMonitor(this);
		systemControl = new SystemOperator(this);
		console = new Console(this);
		exeContent = new ObjectOperator(this);
	}

	public void prepare()
	{
		try
		{

			// resetData();

		} catch (Exception e)
		{

		}
		// prepareDir();
		prepareConsole();
		exeContent.prepareComponents(this);
		dataManager.loadMap();
		if (!getSettings().getInterfaceSettings().runInRealTime)
		{
			dataManager.performDataActions(0.0, exeContent.getValueVector(), JumpStatus.NO_JUMP, true);
		}
	}

	public void resetData()
	{
		dataManager.restoreInitialData();
		// dataCollector = new ExecutionData();
	}

	public void run()
	{
		// writeFiles(true);
		prepare();
		start();
		stop();
		env.getOutputs().generateOutputs(env, true);

		// writeFiles(false);

	}

	public void stop()
	{

		jumpEvaluator.setRunning(false);
		// Console.printInfoStatus(this, true);
		Console.info("Environment Stopped");
	}

	private void prepareConsole()
	{
		if (env.getSettings().getOutputSettings().saveLogToFile)
		{
			prepareDir();
			Console.startOutputFile(new File(env.getSettings().getOutputSettings().outputDirectory + "/"
			+ getStartTime(env, false) + "/log" + getStartTime(env, true) + ".txt"));
		}
	}

	private void prepareDir()
	{

		File dir = new File(
		env.getSettings().getOutputSettings().outputDirectory + "/" + getStartTime(env, false) + "/");
		dir.mkdirs();

	}

	private void start()
	{
		prepareDir();
		Console.info("Environment Started");
		jumpEvaluator.setRunning(true);
		if (!getSettings().getInterfaceSettings().runInRealTime)
		{
			console.startStatusPrintThread();
		}
		Thread term = launchTerminationThread();
		executionMonitor.launchEnvironment();
		term.stop();

	}

	private Thread launchTerminationThread()
	{
		final Scanner in = new Scanner(System.in);
		Thread debugStatusThread = new Thread(new Runnable()
		{

			public void run()
			{
				String input = "";
				while (!input.equals("q"))
				{
					input = in.nextLine();
					if (input.equals("q"))
					{
						System.out.println("killing");
						jumpEvaluator.setRunning(false);
					}
				}

			}
		});
		debugStatusThread.start();
		return debugStatusThread;
	}

	public ExecutionOperator(Environment envi)
	{
		env = envi;
		initializeComponents();
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("M_d_yy_HH_mm_ss_SSS");

	private static HashMap<Environment, String> startTimes = new HashMap<Environment, String>();

	public static String getStartTime(Environment env, boolean file_name)
	{

		if (!startTimes.containsKey(env))
		{
			startTimes.put(env, "[" + sdf.format(new Date()) + "]");
		}
		if (file_name && env.getSettings().getOutputSettings().appendFilesWithNumericDate)
		{
			return "_" + startTimes.get(env);
		} else if (!file_name)
		{
			return startTimes.get(env);
		} else
		{
			return "";
		}
	}
}
