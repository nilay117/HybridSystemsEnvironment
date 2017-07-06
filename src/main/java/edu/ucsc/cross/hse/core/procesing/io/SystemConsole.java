package edu.ucsc.cross.hse.core.procesing.io;

import java.util.HashMap;

import com.be3short.data.info.SystemInfo;

import bs.commons.objects.access.CallerRetriever;
import bs.commons.objects.labeling.StringFormatter;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingConnector;

public class SystemConsole extends ProcessingConnector
{

	static SystemInfo info = new SystemInfo();
	private static CallerRetriever classRetriever = new CallerRetriever();
	private Double nextPrintTime;
	private Double printInterval;

	public SystemConsole(CentralProcessor processor)
	{
		super(processor);
		nextPrintTime = 0.0;
		printInterval = getSettings().getExecutionSettings().simDuration
		/ getSettings().getConsolePrintSettings().totalSimTimePrintOuts;
	}

	public void printUpdates()
	{
		progressUpdate();
	}

	public void progressUpdate()
	{
		if (getSettings().getConsolePrintSettings().printProgressUpdates)
		{
			if (nextPrintTime < getEnvironmentOperator().getEnvironmentHybridTime().getTime())
			{
				nextPrintTime = getEnvironmentOperator().getEnvironmentHybridTime().getTime() + printInterval;
				Double percentComplete = 100 * getEnvironmentOperator().getEnvironmentHybridTime().getTime()
				/ getSettings().getExecutionSettings().simDuration;
				print("Status : " + Math.round(percentComplete) + "% complete - simulation time at "
				+ getEnvironmentOperator().getEnvironmentHybridTime().getTime() + " seconds");
			}
		}
	}

	public String getDiscreteEventIndication()
	{
		String string = null;
		if (getSettings().getConsolePrintSettings().printDiscreteEventIndicator)
		{
			string = ("discrete event detected at " + getEnvironmentOperator().getEnvironmentHybridTime().getTime()
			+ " seconds");
		}
		return string;
	}

	public static String getCallingClassName()
	{
		return getCallingClassName(0);
	}

	public static String getCallingClassName(Integer increment)
	{
		return classRetriever.retriever.getCallingClasses()[2 + increment].getSimpleName();
	}

	public void print(String message)
	{
		// System.out.println("[" + StringFormatter.getAbsoluteHHMMSS() + "][" +
		// getCallingClassName(1) + "] " + message);
		if (message != null)
		{
			System.out.println(
			// "[" + StringFormatter.getMemoryUsageInfoString() + "]" + "[" +
			// getCallingClassName(1) + "] " + message);
			"[" + StringFormatter.getAbsoluteHHMMSS() + "][" + System.currentTimeMillis() / 1000 + "]["
			+ Math.round(info.usedMem() / Math.pow(1024, 2)) + "/" + Math.round(info.totalMem() / Math.pow(1024, 2))
			+ "]" + "[" + getCallingClassName(1) + "] " + message);

		}
	}

	public void printStartMessage()
	{

	}

	public void printInterruptMessage()
	{

	}
}
