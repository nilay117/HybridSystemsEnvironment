
package edu.ucsc.cross.hse.core.engine;

import com.be3short.io.format.FileFormat;
import com.be3short.io.format.FileSpecifications;
import edu.ucsc.cross.hse.core.container.EnvironmentContents;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.container.EnvironmentOutputs;
import edu.ucsc.cross.hse.core.container.EnvironmentSettings;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.file.EnvironmentFile;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.monitor.ComputationMonitor;
import edu.ucsc.cross.hse.core.monitor.DataMonitor;
import edu.ucsc.cross.hse.core.monitor.InterruptMonitor;
import edu.ucsc.cross.hse.core.monitor.JumpMonitor;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import edu.ucsc.cross.hse.core.operator.ObjectOperator;
import edu.ucsc.cross.hse.core.operator.SimulationOperator;
import edu.ucsc.cross.hse.core.operator.SystemOperator;
import edu.ucsc.cross.hse.core.setting.ExecutionParameters;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class ExecutionEngine
{

	Environment env;

	// Monitors
	DataMonitor dataManager;
	ComputationMonitor computationMonitor;
	InterruptMonitor executionMonitor;
	JumpMonitor jumpEvaluator;

	// Operators
	SimulationOperator simEngine;
	SystemOperator systemControl;
	ObjectOperator exeContent;

	// Output console
	Console console;

	public static HashMap<Environment, ExecutionEngine> operatorMap = new HashMap<Environment, ExecutionEngine>();

	public Console getConsole()
	{
		return console;
	}

	public EnvironmentContents getContents()
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

	public InterruptMonitor getExecutionMonitor()
	{
		return executionMonitor;
	}

	public ComputationMonitor getComputationMonitor()
	{
		return computationMonitor;
	}

	public ExecutionParameters getExecutionParameters()
	{
		return env.getSettings().getExecutionParameters();
	}

	public JumpMonitor getJumpEvaluator()
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
		jumpEvaluator = new JumpMonitor(this);
		computationMonitor = new ComputationMonitor(this);
		dataManager = new DataMonitor(this);
		systemControl = new SystemOperator(this);
		console = new Console(this);
		exeContent = new ObjectOperator(this);
		executionMonitor = new InterruptMonitor(this);
	}

	public void prepare()
	{
		prepareConsole();
		exeContent.prepareComponents(this);
		dataManager.loadMap();
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
		specs.prependDirectory(ExecutionEngine.getStartTime(env, false).toString());
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
			+ ExecutionEngine.getStartTime(env, true).toString());
			File spe = specs.getLocation(true);
			env.save(spe, false);
			Console.info("Configuration saved: " + spe.getAbsolutePath());
		}
		env.loadData(data);
		if (env.getSettings().getOutputSettings().saveEnvironmentToFile)
		{
			FileSpecifications<EnvironmentFile> specs = getFileSpecifications(env, new EnvironmentFile(),
			env.getSettings().getOutputSettings().environmentFileName
			+ ExecutionEngine.getStartTime(env, true).toString());
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
		// if (!getSettings().getInterfaceSettings().runInRealTime)
		{
			console.startStatusPrintThread();
		}
		Thread term = launchTerminationThread();
		computationMonitor.launchEnvironment();
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

	public ExecutionEngine(Environment envi)
	{
		env = envi;
		initializeComponents();
	}

	private static SimpleDateFormat sdf = new SimpleDateFormat("M_d_yy_HH_mm_ss_SSS");

	private static HashMap<Environment, String> startTimes = new HashMap<Environment, String>();

	private static ArrayList<ExecutionEngine> operators = new ArrayList<ExecutionEngine>();

	private ArrayList<Object> components()
	{
		return new ArrayList<Object>(Arrays.asList(console, dataManager, env, exeContent, computationMonitor,
		jumpEvaluator, simEngine, systemControl));
	}

	// Operator
	public static Environment getSystemContainingEnvironment(HybridSystem<?> sys)
	{
		for (Environment env : operatorMap.keySet())
		{

			if (env.getContents().getSystems().contains(sys))
			{
				return env;
			}
		}
		return null;
	}

	// Operator
	public static Environment getContainingEnvironment(Object component)
	{
		for (Environment env : operatorMap.keySet())
		{
			ArrayList<Object> objs = new ArrayList<Object>(
			Arrays.asList(env.getContents(), env.getData(), env.getOutputs(), env.getSettings()));
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
		String prefix = String.valueOf(getStartTime(ExecutionEngine.getContainingEnvironment(component), false));
		for (FileSpecifications<F> spec : unappended.keySet())
		{
			FileSpecifications<F> specs = spec.copy();
			if (!specs.isNullFile())
			{
				specs.prependDirectory((FileSpecifications.checkSlashes(new File(
				ExecutionEngine.getContainingEnvironment(component).getSettings().getOutputSettings().outputDirectory)
				.getAbsolutePath(), prefix)));
				// System.out.println("final path: " + specs.getLocation(false).getAbsolutePath());

			}
			appended.put(specs, unappended.get(spec));
		}
		return appended;
	}

	public static ExecutionEngine getOperator(Object object)
	{
		ExecutionEngine op = null;
		if (operators.size() > 0)
		{
			op = operators.get(0);
		}
		for (ExecutionEngine operator : operators)
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