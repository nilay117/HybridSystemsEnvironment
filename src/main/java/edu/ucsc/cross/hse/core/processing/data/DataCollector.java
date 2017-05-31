package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

import edu.ucsc.cross.hse.core.component.constructors.DataSet;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.CoreDataGroup;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.processing.execution.Environment;
import edu.ucsc.cross.hse.core.processing.execution.Processor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class DataCollector extends ProcessorAccess
{

	private Double lastStoreTime = -10.0; // time since last data was stored,
											// used to store data at specified
											// interval
	private ArrayList<Data> dataElementsToStore; // list of all data elements
													// that are to be stored

	public DataCollector(Processor processor)
	{
		super(processor);
		dataElementsToStore = new ArrayList<Data>();
	}

	public Data getValue(Integer index)
	{
		return dataElementsToStore.get(index);
	}

	public ArrayList<Data> getDataStates()
	{
		return dataElementsToStore;
	}

	public ArrayList<String> getAllPossibleStateElements()
	{
		ArrayList<String> stateElements = new ArrayList<String>();
		for (Data allStates : dataElementsToStore)
		{
			if (!stateElements.contains(allStates.getProperties().getName()))
			{
				stateElements.add(allStates.getProperties().getName());
			}
		}
		// System.out.println(stateElements.toString());

		return stateElements;
	}

	public ArrayList<Data> findElementsByName(String title)
	{
		ArrayList<Data> datas = new ArrayList<Data>();
		for (Data element : dataElementsToStore)
		{
			if (element.getProperties().getName().equals(title))
			{
				datas.add(element);
			}
		}
		return datas;
	}

	public Data checkIfMatchingElementInDataSet(Data data, String title)
	{
		for (Component component : getEnvironment().getComponents(true))
		{
			try
			{
				DataSet set = (DataSet) component;
				if (set.getComponents(true).contains(data))
				{
					for (Component subComponent : set.getComponents(true))
					{
						try
						{
							Data dat = (Data) subComponent;
							if (dat.getProperties().getName().equals(title))
							{
								return dat;
							}

						} catch (Exception notData)
						{

						}
					}
				}

			} catch (Exception notDataSet)
			{

			}
		}

		return null;
	}

	public void storeData(Double time)
	{
		for (Data element : dataElementsToStore)
		{
			if (element.save)
			{
				dataOps(element).storeValue(time);
			}
		}
		// IO.out(getConsole().getDataElementStoreString(time, true));
	}

	public void storeData(double t, boolean override_store_time)
	throws MaxCountExceededException, DimensionMismatchException
	{
		if (override_store_time)
		{
			storeData(t);
		} else if (t > lastStoreTime + getSettings().getData().dataStoreIncrement)// settings.dataStoreIncrement)
		{
			lastStoreTime = t;
			storeData(t);
		}
	}

	public void loadStoreStates()
	{
		dataElementsToStore.clear();
		for (Component component : super.getEnvironment().getComponents(true))
		{
			try
			{
				Data element = (Data) component;

				if (CoreDataGroup.ALL_DATA.contains(element))
				{
					if (element.save)
					{
						dataElementsToStore.add(element);
					}
				}
			} catch (Exception notElement)
			{

			}
		}
	}
}
