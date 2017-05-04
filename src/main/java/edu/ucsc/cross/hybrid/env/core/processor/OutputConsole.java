package edu.ucsc.cross.hybrid.env.core.processor;

import java.util.HashMap;

import bs.commons.io.system.IO;
import bs.commons.io.system.StringFormatter;
import edu.ucsc.cross.hybrid.env.core.components.Data;
import edu.ucsc.cross.hybrid.env.core.components.DataSet;
import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;
import edu.ucsc.cross.hybrid.env.structural.Component;

public class OutputConsole extends ProcessorComponent
{

	private Double nextPrintTime;
	private Double printInterval;

	OutputConsole(Environment processor)
	{
		super(processor);
		nextPrintTime = 0.0;
		printInterval = getSettings().trial().simDuration / getSettings().io().totalSimTimePrintOuts;
	}

	public void printUpdates()
	{
		progressUpdate();
	}

	public void progressUpdate()
	{
		if (nextPrintTime < getEnvironment().time().seconds())
		{
			nextPrintTime = getEnvironment().time().seconds() + printInterval;
			Double percentComplete = 100 * getEnvironment().time().seconds() / getSettings().trial().simDuration;
			IO.out("Status : " + Math.round(percentComplete) + "% complete - simulation time at "
			+ getEnvironment().time().seconds() + " seconds");
		}
	}

	public String getDiscreteEventIndication()
	{
		String string = null;
		if (getSettings().io().printDiscreteEventIndicator)
		{
			string = ("discrete event detected at " + getEnvironment().time().seconds() + " seconds");
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
			storeString = ("data stored at " + time + " seconds");// + super.getComputationEngine().getODEValueVector().toString());

			if (getSettings().io().printStoreDataReport)
			{
				HashMap<String, HybridSystem> systemNames = new HashMap<String, HybridSystem>();
				for (HybridSystem rootSystem : super.getEnvironment().getAllSystems())

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
							for (Component element : dataSet.getAllllComponents(true))
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
}
