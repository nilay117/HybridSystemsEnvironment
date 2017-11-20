
package edu.ucsc.cross.hse.core.operator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import com.be3short.io.format.FileFormat;
import com.be3short.io.format.FileSpecifications;

import edu.ucsc.cross.hse.core.container.EnvironmentContent;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.container.EnvironmentOutputs;
import edu.ucsc.cross.hse.core.container.EnvironmentSettings;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.file.EnvironmentFile;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.monitor.ComputationMonitor;
import edu.ucsc.cross.hse.core.monitor.DataMonitor;
import edu.ucsc.cross.hse.core.monitor.EventMonitor;
import edu.ucsc.cross.hse.core.monitor.JumpStatus;
import edu.ucsc.cross.hse.core.setting.ExecutionParameters;

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
	public static HashMap<Environment, ExecutionOperator> operatorMap = new HashMap<Environment, ExecutionOperator>();

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
		generateFiles();
		// writeFiles(false);

	}

	public void stop()
	{

		jumpEvaluator.setRunning(false);
		// Console.printInfoStatus(this, true);
		Console.info("Environment Stopped");
	}

	private <T extends FileFormat> FileSpecifications<T> getFileSpecifications(Environment env, T spec_type,
	String file_name)
	{
		FileSpecifications<T> specs = new FileSpecifications<T>(file_name, spec_type);
		specs.prependDirectory(ExecutionOperator.getStartTime(env, false).toString());
		specs.prependDirectory(env.getSettings().getOutputSettings().outputDirectory);
		return specs;
	}

	public void generateFiles()
	{
		EnvironmentData data = env.getData();
		if (env.getSettings().getOutputSettings().saveConfigurationToFile)
		{
			FileSpecifications<EnvironmentFile> specs = getFileSpecifications(env, new EnvironmentFile(),
			env.getSettings().getOutputSettings().configurationFileName
			+ ExecutionOperator.getStartTime(env, true).toString());
			File spe = specs.getLocation(true);
			env.save(spe, false);
			Console.info("Configuration saved: " + spe.getAbsolutePath());
		}
		env.loadData(data);
		if (env.getSettings().getOutputSettings().saveEnvironmentToFile)
		{
			FileSpecifications<EnvironmentFile> specs = getFileSpecifications(env, new EnvironmentFile(),
			env.getSettings().getOutputSettings().environmentFileName
			+ ExecutionOperator.getStartTime(env, true).toString());
			File spe = specs.getLocation(true);
			env.save(spe, true);
			Console.info("Environment saved: " + spe.getAbsolutePath());
		}
		env.loadData(data);

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
						System.out.println("Stopping Environment");
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

	private static ArrayList<ExecutionOperator> operators = new ArrayList<ExecutionOperator>();

	private ArrayList<Object> components()
	{
		return new ArrayList<Object>(Arrays.asList(console, dataManager, env, exeContent, executionMonitor,
		jumpEvaluator, simEngine, systemControl));
	}

	// Operator
	public static Environment getContainingEnvironment(Object component)
	{
		for (Environment env : operatorMap.keySet())
		{
			ArrayList<Object> objs = new ArrayList<Object>(
			Arrays.asList(env.content, env.dataCollector, env.outputs, env.settings));
			if (objs.contains(component))
			{
				return env;
			}
		}
		return null;
	}

	public static <F extends FileFormat, T> HashMap<FileSpecifications<F>, T> getAppendedFiles(
	HashMap<FileSpecifications<F>, T> unappended, Object component)
	{
		HashMap<FileSpecifications<F>, T> appended = new HashMap<FileSpecifications<F>, T>();
		String prefix = String.valueOf(getStartTime(ExecutionOperator.getContainingEnvironment(component), false));
		for (FileSpecifications<F> spec : unappended.keySet())
		{
			FileSpecifications<F> specs = spec.copy();
			if (!specs.isNullFile())
			{
				specs.prependDirectory((FileSpecifications.checkSlashes(new File(
				ExecutionOperator.getContainingEnvironment(component).getSettings().getOutputSettings().outputDirectory)
				.getAbsolutePath(), prefix)));
				// System.out.println("final path: " + specs.getLocation(false).getAbsolutePath());

			}
			appended.put(specs, unappended.get(spec));
		}
		return appended;
	}

	public static ExecutionOperator getOperator(Object object)
	{
		ExecutionOperator op = null;
		if (operators.size() > 0)
		{
			op = operators.get(0);
		}
		for (ExecutionOperator operator : operators)
		{
			if (operator.components().contains(object))
			{
				op = operator;
			}
		}
		return op;
	}

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
