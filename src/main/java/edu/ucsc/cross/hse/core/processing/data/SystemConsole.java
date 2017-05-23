package edu.ucsc.cross.hse.core.processing.data;

import java.util.HashMap;

import bs.commons.io.system.IO;
import bs.commons.io.system.StringFormatter;
import bs.commons.objects.access.CallerRetriever;
import edu.ucsc.cross.hse.core.component.constructors.Component;
import edu.ucsc.cross.hse.core.component.constructors.DataSet;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.processing.management.Environment;
import edu.ucsc.cross.hse.core.processing.management.Processor;

public class SystemConsole extends Processor
{

	private static CallerRetriever classRetriever = new CallerRetriever();;
	private Double nextPrintTime;
	private Double printInterval;

	public SystemConsole(Environment processor)
	{
		super(processor);
		nextPrintTime = 0.0;
		printInterval = getSettings().trial().simDuration / getSettings().io().totalSimTimePrintOuts;
	}

	private void initialize()
	{
		IO.settings.printCallingClass = false;
		// classRetriever = new CallerRetriever();
	}

	public void printUpdates()
	{
		progressUpdate();
	}

	public void progressUpdate()
	{
		if (nextPrintTime < getEnvironment().getEnvironmentTime().getTime())
		{
			nextPrintTime = getEnvironment().getEnvironmentTime().getTime() + printInterval;
			Double percentComplete = 100 * getEnvironment().getEnvironmentTime().getTime()
			/ getSettings().trial().simDuration;
			IO.out("Status : " + Math.round(percentComplete) + "% complete - simulation time at "
			+ getEnvironment().getEnvironmentTime().getTime() + " seconds");
		}
	}

	public String getDiscreteEventIndication()
	{
		String string = null;
		if (getSettings().io().printDiscreteEventIndicator)
		{
			string = ("discrete event detected at " + getEnvironment().getEnvironmentTime().getTime() + " seconds");
		}
		return string;
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String getDataElementStoreString(Double time, boolean simulated_only)
	{
		String storeString = null;
		if (getSettings().io().printStoreDataIndicator)
		{
			storeString = ("data stored at " + time + " seconds");// +
																	// super.getComputationEngine().getODEValueVector().toString());

			if (getSettings().io().printStoreDataReport)
			{
				HashMap<String, Component> systemNames = new HashMap<String, Component>();
				for (Component rootSystem : super.getEnvironment().getComponents(true))

				{
					String sysName = StringFormatter.getAppendedName(rootSystem.getProperties().getName(),
					systemNames.keySet());
					systemNames.put(sysName, rootSystem);
					storeString += "\n" + sysName + " - [";
					HashMap<String, DataSet> dataSetNames = new HashMap<String, DataSet>();
					for (Component component : rootSystem.getComponents(DataSet.class, true))
					{
						DataSet dataSet = (DataSet) component;
						String dataSetName = StringFormatter.getAppendedName(dataSet.getProperties().getName(),
						dataSetNames.keySet());
						dataSetNames.put(dataSetName, dataSet);
						storeString += sysName + " - [" + dataSetName + " ";
						try
						{
							for (Component element : dataSet.getComponents(true))
								try
								{
									Data data = (Data) element;

									storeString = storeString + ("{" + data.getProperties().getName() + " = "
									+ Data.getStoredNumberValue(data, time) + "} ");

								} catch (Exception notElement)
								{

								}
						} catch (Exception notElement)
						{

						}
						storeString += " ]\n";
					}
				}
			}
		}
		return storeString;
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
		System.out.println("[" + StringFormatter.getAbsoluteHHMMSS() + "][" + getCallingClassName(1) + "] " + message);
	}

}
