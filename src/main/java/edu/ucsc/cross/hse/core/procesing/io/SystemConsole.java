package edu.ucsc.cross.hse.core.procesing.io;

import java.io.IOException;
import java.io.OutputStream;

import com.be3short.data.info.SystemInfo;

import bs.commons.objects.access.CallerRetriever;
import bs.commons.objects.labeling.StringFormatter;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

/*
 * Prints output notifications, messages, warnings, and errors with additional
 * information such as the time, memory usage, and calling class if needed.
 * These features can be configured by making changes to the ConsoleSettings in
 * the SettingConfigurer.
 */
public class SystemConsole extends ProcessorAccess
{

	/*
	 * Information about the system such as memory usage and calling class of a
	 * message
	 */
	private static SystemInfo info = new SystemInfo();
	/*
	 * Time that the next progress update will be printed
	 */
	private Double nextPrintTime;

	/*
	 * Computed interval between progress print outs
	 */
	private Double printInterval;

	/*
	 * Optional alternate print location specification for using something like
	 * an external console
	 */
	private OutputStream alternatePrintLocation;

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
	 * Initialize the console components
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
		return CallerRetriever.retriever.getCallingClasses()[2 + increment].getSimpleName();
	}

	/*
	 * Print a message through the system console with additional information
	 * included depending on settings
	 */
	public void print(String message)
	{

		if (message != null)
		{
			String appendedMsg = getMessagePrefix() + message;

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

	/*
	 * Compiles the message prefix based on the current settings
	 */
	private String getMessagePrefix()
	{
		String prefix = "";
		if (getSettings().getConsolePrintSettings().printCurrentTime)
		{
			prefix += "[" + StringFormatter.getAbsoluteHHMMSS() + "]";
		}
		if (getSettings().getConsolePrintSettings().printMemoryUsage)
		{
			prefix += "[" + Math.round(info.usedMem() / Math.pow(1024, 2)) + "/"
			+ Math.round(info.totalMem() / Math.pow(1024, 2)) + "]";
		}
		if (getSettings().getConsolePrintSettings().printCallingClass)
		{
			prefix += "[" + getCallingClassName(1) + "]";
		}
		return prefix;
	}
}
