package edu.ucsc.cross.hse.core.procesing.io;

import java.io.IOException;
import java.io.OutputStream;
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
	private OutputStream alternatePrintLocation;

	public SystemConsole(CentralProcessor processor)
	{
		super(processor);
		alternatePrintLocation = null;
		nextPrintTime = 0.0;
		initialize();

	}

	public void initialize()
	{
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
			String appendedMsg = "[" + StringFormatter.getAbsoluteHHMMSS() + "][" + System.currentTimeMillis() / 1000
			+ "][" + Math.round(info.usedMem() / Math.pow(1024, 2)) + "/"
			+ Math.round(info.totalMem() / Math.pow(1024, 2)) + "]" + "[" + getCallingClassName(1) + "] " + message;

			if (alternatePrintLocation != null)
			{
				try
				{
					alternatePrintLocation.write(appendedMsg.getBytes());
					//	alternatePrintLocation.flush();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			{
				System.out.println(appendedMsg);
			}
		}
	}

	public void setAlternatePrintLocation(OutputStream alternatePrintLocation)
	{
		this.alternatePrintLocation = alternatePrintLocation;
	}

	public void printStartMessage()
	{

	}

	public void printInterruptMessage()
	{

	}
}
