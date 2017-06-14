package edu.ucsc.cross.hse.core.procesing.io;

import java.util.HashMap;

import bs.commons.objects.access.CallerRetriever;
import bs.commons.objects.labeling.StringFormatter;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.processing.execution.Processor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

public class SystemConsole extends ProcessorAccess
{

	private static CallerRetriever classRetriever = new CallerRetriever();
	private Double nextPrintTime;
	private Double printInterval;

	public SystemConsole(Processor processor)
	{
		super(processor);
		nextPrintTime = 0.0;
		printInterval = getSettings().trial().simDuration / getSettings().io().totalSimTimePrintOuts;
	}

	private void initialize()
	{
		// IO.settings.printCallingClass = false;
		// classRetriever = new CallerRetriever();
	}

	public void printUpdates()
	{
		progressUpdate();
	}

	public void progressUpdate()
	{
		if (getSettings().io().printProgressUpdates)
		{
			if (nextPrintTime < getEnvironmentOperator().getEnvironmentHybridTime().getTime())
			{
				nextPrintTime = getEnvironmentOperator().getEnvironmentHybridTime().getTime() + printInterval;
				Double percentComplete = 100 * getEnvironmentOperator().getEnvironmentHybridTime().getTime()
				/ getSettings().trial().simDuration;
				print("Status : " + Math.round(percentComplete) + "% complete - simulation time at "
				+ getEnvironmentOperator().getEnvironmentHybridTime().getTime() + " seconds");
			}
		}
	}

	public String getDiscreteEventIndication()
	{
		String string = null;
		if (getSettings().io().printDiscreteEventIndicator)
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

	public static void print(String message)
	{
		// System.out.println("[" + StringFormatter.getAbsoluteHHMMSS() + "][" +
		// getCallingClassName(1) + "] " + message);
		if (message != null)
		{
			System.out.println(
			"[" + StringFormatter.getMemoryUsageInfoString() + "]" + "[" + getCallingClassName(1) + "] " + message);
		}
	}

}
