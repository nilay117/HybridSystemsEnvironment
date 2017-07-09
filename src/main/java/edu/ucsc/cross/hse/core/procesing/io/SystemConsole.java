package edu.ucsc.cross.hse.core.procesing.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import com.be3short.data.info.SystemInfo;

import bs.commons.objects.access.CallerRetriever;
import bs.commons.objects.labeling.StringFormatter;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

public class SystemConsole extends ProcessorAccess
{

	private static SystemInfo info = new SystemInfo(); // information about the
														// system such as memory
														// usage
	private static CallerRetriever classRetriever = new CallerRetriever(); // method
																			// call
																			// class
																			// detector

	private Double nextPrintTime; // time that the next progress update should
									// be printed
	private Double printInterval; // interval between progress update print outs
	private OutputStream alternatePrintLocation; // alternate print location

	/*
	 * Constructor that links the processor
	 */
	public SystemConsole(CentralProcessor processor)
	{
		super(processor);
		alternatePrintLocation = null;
		nextPrintTime = 0.0;
		initialize();

	}

	/*
	 * initialize the console components
	 */
	public void initialize()
	{
		nextPrintTime = 0.0;
		printInterval = getSettings().getExecutionSettings().simDuration
		/ getSettings().getConsolePrintSettings().totalSimTimePrintOuts;
	}

	/*
	 * Print updates if there are any
	 */
	public void printUpdates()
	{
		progressUpdate();
	}

	/*
	 * Print out a progress update message indicating how far along the
	 * environment execution is
	 */
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

	/*
	 * Compile a message indicating a discrete event occurrance
	 */
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

	/*
	 * Get the name of the class that has called a method
	 */
	public static String getCallingClassName()
	{
		return getCallingClassName(0);
	}

	/*
	 * Get the name of the class that has called a method, if the index is known
	 */
	public static String getCallingClassName(Integer increment)
	{
		return classRetriever.retriever.getCallingClasses()[2 + increment].getSimpleName();
	}

	/*
	 * Print a message through the system console with additional information
	 * included depending on settings
	 */
	public void print(String message)
	{

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
					// alternatePrintLocation.flush();
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

	/*
	 * Set an alternate location to print to
	 */
	public void setAlternatePrintLocation(OutputStream alternatePrintLocation)
	{
		this.alternatePrintLocation = alternatePrintLocation;
	}

}
