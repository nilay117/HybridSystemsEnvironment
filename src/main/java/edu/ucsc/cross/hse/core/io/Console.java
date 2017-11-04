package edu.ucsc.cross.hse.core.io;

import com.be3short.io.info.SystemInfo;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.setting.LogSettings;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Console
{

	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
	private static ExecutionOperator manager;
	private static OutputStream out;
	private static Double nextDebugStatusPrint;
	private static Double infoStatusPrintInterval;
	private static Double nextInfoPrint;
	private static PrintWriter fileOut;
	private static SystemInfo sysInfo = new SystemInfo();

	public Console(ExecutionOperator manage)
	{
		manager = manage;

	}

	public void startStatusPrintThread()
	{
		nextDebugStatusPrint = System.currentTimeMillis()
		+ manager.getSettings().getLogSettings().statusPrintInterval * 1000.0;
		infoStatusPrintInterval = manager.getExecutionParameters().getMaximumTime()
		/ manager.getSettings().getLogSettings().numStatusUpdateOutputs;
		nextInfoPrint = 0.0;
		startThread();
	}

	private void startThread()
	{
		Thread debugStatusThread = new Thread(new Runnable()
		{

			public void run()
			{
				printDebugStatusLoop();
			}
		});
		debugStatusThread.start();
	}

	private void printDebugStatusLoop()
	{
		while (manager.getJumpEvaluator().isRunning())
		{

			if (manager.getSettings().getLogSettings().statusPrintInterval != null)
			{
				printDebugStatus();
			}
		}
	}

	public static void printCompleteStatus(ExecutionOperator manager)
	{
		if (manager.getSettings().getLogSettings().numStatusUpdateOutputs > 0)
		{
			info("update : " + String.format("%2.2f", 100.0) + "%  complete : sim time = "
			+ manager.getExecutionContent().getSimulationTime() + " : jumps = "
			+ manager.getExecutionContent().getHybridSimTime().getJumps());
		}

	}

	public static void printInfoStatus(ExecutionOperator manager)
	{
		printInfoStatus(manager, false);
	}

	public static void printInfoStatus(ExecutionOperator manager, boolean override_time)
	{
		if ((manager.getExecutionContent().getSimulationTime() >= nextInfoPrint) || override_time)
		{

			Double percentComplete = 100 * manager.getExecutionContent().getSimulationTime()
			/ manager.getExecutionParameters().getMaximumTime();
			info("status: " + String.format("%.2f", percentComplete) + "%  complete : sim time = "
			+ String.format("%.2f", manager.getExecutionContent().getSimulationTime()) + " : jumps = "
			+ manager.getExecutionContent().getHybridSimTime().getJumps());
			if (manager.getExecutionContent().getSimulationTime() >= nextInfoPrint)
			{
				nextInfoPrint = nextInfoPrint + infoStatusPrintInterval;
			}
		}
	}

	private void printDebugStatus()
	{
		if (nextDebugStatusPrint < System.currentTimeMillis())
		{
			if (manager.getJumpEvaluator().isRunning())
			{
				Double percentComplete = 100 * manager.getExecutionContent().getSimulationTime()
				/ manager.getExecutionParameters().getMaximumTime();
				info("monitor : " + String.format("%.2f", percentComplete) + "% complete : sim time = "
				+ String.format("%.2f", manager.getExecutionContent().getSimulationTime()) + " : jumps = "
				+ manager.getExecutionContent().getHybridSimTime().getJumps());
				nextDebugStatusPrint = System.currentTimeMillis()
				+ manager.getSettings().getLogSettings().statusPrintInterval * 1000.0;
			}
		}
	}

	public static void info(String message)
	{
		print(LogSettings.infoLabel, message, OutputType.INFO);
	}

	public static void debug(String message)
	{
		print(LogSettings.debugLabel, message, OutputType.DEBUG);
	}

	public static void warn(String message)
	{
		print(LogSettings.warnLabel, message, OutputType.WARN);
	}

	public static void error(String message)
	{
		print(LogSettings.errorLabel, message, OutputType.ERROR);
	}

	public static void info(String message, Throwable throwable)
	{
		print(LogSettings.infoLabel, message, throwable, OutputType.INFO);
	}

	public static void debug(String message, Throwable throwable)
	{
		print(LogSettings.debugLabel, message, throwable, OutputType.DEBUG);
	}

	public static void warn(String message, Throwable throwable)
	{
		print(LogSettings.warnLabel, message, throwable, OutputType.WARN);
	}

	public static void error(String message, Throwable throwable)
	{
		print(LogSettings.errorLabel, message, throwable, OutputType.DEBUG);
	}

	private static boolean isOutputEnabled(OutputType type)
	{
		switch (type)
		{
			case INFO:
			{
				return manager.getSettings().getLogSettings().printInfo;

			}
			case DEBUG:
			{
				return manager.getSettings().getLogSettings().printDebug;

			}
			case WARN:
			{
				return manager.getSettings().getLogSettings().printWarning;

			}
			case ERROR:
			{
				return manager.getSettings().getLogSettings().printError;

			}
			default:
			{
				return true;
			}
		}

	}

	private static void print(String label, String message, OutputType type)
	{
		if (isOutputEnabled(type))
		{
			String statement = compileMessagePrefix(label) + " " + message;
			try
			{
				if (out != null)
				{
					out.write(statement.getBytes());
				} else
				{
					System.out.println(statement);
				}
				if (fileOut != null)
				{
					fileOut.println(statement);
					fileOut.flush();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void print(String label, String message, Throwable throwable, OutputType type)
	{
		String statement = compileMessagePrefix(label) + " " + message;
		try
		{
			if (out != null)
			{
				out.write(statement.getBytes());
			} else
			{
				System.out.println(statement);
			}
			if (fileOut != null)
			{
				fileOut.println(statement);
				fileOut.flush();
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		throwable.printStackTrace();
		if (fileOut != null)
		{
			throwable.printStackTrace(fileOut);
			fileOut.flush();
		}
	}

	private static String compileMessagePrefix(String label)
	{
		Runtime.getRuntime().totalMemory();
		String memoryUsage = String.valueOf((int) Math.round(sysInfo.usedMem() / (Math.pow(1024.0, 2))) + "/"
		+ String.valueOf((int) Math.round(sysInfo.totalMem() / (Math.pow(1024.0, 2)))));
		String prefix = "[" + sdf.format(new Date()) + "][" + memoryUsage + "][" + label + "]";
		return prefix;
	}

	public static void setNewOutputStream(OutputStream output)
	{
		out = output;
	}

	public static void startOutputFile(File file)
	{
		try
		{
			fileOut = new PrintWriter(new FileOutputStream(file));
		} catch (FileNotFoundException e)
		{

		}
	}

	public static void endOutputFile()
	{
		try
		{
			fileOut.close();
		} catch (Exception e)
		{

		}
	}

	public static enum OutputType
	{
		INFO,
		DEBUG,
		WARN,
		ERROR;
	}
}