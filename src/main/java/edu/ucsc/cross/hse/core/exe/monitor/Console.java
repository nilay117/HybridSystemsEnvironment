package edu.ucsc.cross.hse.core.exe.monitor;

import com.be3short.logging.basic.GeneralLogger;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.operator.EnvironmentManager;
import org.apache.maven.plugin.logging.Log;
import org.slf4j.impl.StaticLoggerBinder;

public class Console
{

	public static final Log out = initializeOutput();

	private static Log initializeOutput()
	{
		Log out = new GeneralLogger();
		StaticLoggerBinder.getSingleton().setMavenLog(out);
		return out;
	}

	private EnvironmentManager manager;
	private Double nextDebugStatusPrint;
	private Double nextInfoStatusPrint;
	private Thread debugStatusThread;

	public Console(EnvironmentManager manager)
	{
		this.manager = manager;

	}

	public void initializeStatusPrintTimes()
	{
		nextDebugStatusPrint = System.currentTimeMillis()
		+ manager.getSettings().getLogging().debugStatusUpdateInterval * 1000.0;
		nextInfoStatusPrint = manager.getSettings().getExecutionParameters().simulationDuration
		/ manager.getSettings().getLogging().numInfoStatusOutputs;
		startThread();
	}

	public void printStatus()
	{
		printInfoStatus();
		// printDebugStatus();
	}

	private void startThread()
	{
		Thread thread = new Thread(new Runnable()
		{

			public void run()
			{
				printDebugStatusLoop();
			}
		});
		thread.start();
	}

	private void printDebugStatusLoop()
	{
		while (manager.getTimeOperator()
		.getSimulationTime() < manager.getSettings().getExecutionParameters().simulationDuration
		&& !manager.getJumpEvaluator().isApproachingTermination())
		{
			printDebugStatus();
		}
	}

	private void printInfoStatus()
	{
		if (manager.getTimeOperator().getSimulationTime() > nextInfoStatusPrint)
		{
			nextInfoStatusPrint = nextInfoStatusPrint
			+ (manager.getSettings().getExecutionParameters().simulationDuration
			/ manager.getSettings().getLogging().numInfoStatusOutputs);
			Double percentComplete = 100 * manager.getTimeOperator().getSimulationTime()
			/ manager.getSettings().getExecutionParameters().simulationDuration;
			out.info("progress update : " + String.format("%.2f", percentComplete) + "%  complete : sim time = "
			+ manager.getTimeOperator().getSimulationTime() + " : jumps = "
			+ manager.getTimeOperator().getHybridSimulationTime().getJumps());
		}
	}

	public Thread getDebugStatusThread()
	{
		return debugStatusThread;
	}

	private void printDebugStatus()
	{
		if (nextDebugStatusPrint < System.currentTimeMillis())
		{

			Double percentComplete = 100 * manager.getTimeOperator().getSimulationTime()
			/ manager.getSettings().getExecutionParameters().simulationDuration;
			out.info("status = running : " + String.format("%.2f", percentComplete) + "% complete : sim time = "
			+ manager.getTimeOperator().getSimulationTime() + " : jumps = "
			+ manager.getTimeOperator().getHybridSimulationTime().getJumps());
			nextDebugStatusPrint = System.currentTimeMillis()
			+ manager.getSettings().getLogging().debugStatusUpdateInterval * 1000.0;

		}
	}
}