package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

import edu.ucsc.cross.hse.core.component.constructors.DataSet;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.CoreDataGroup;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.processing.execution.Processor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class DataManager extends ProcessorAccess
{

	private Double lastStoreTime = -10.0; // time since last data was stored,
											// used to store data at specified
											// interval
	private ArrayList<Data> dataElementsToStore; // list of all data elements
													// that are to be stored

	public DataManager(Processor processor)
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
			if (!stateElements.contains(allStates.properties().getName()))
			{
				stateElements.add(allStates.properties().getName());
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
			if (element.properties().getName().equals(title))
			{
				datas.add(element);
			}
		}
		return datas;
	}

	public Data checkIfMatchingElementInDataSet(Data data, String title)
	{
		for (Component component : getEnvironment().hierarchy().getComponents(true))
		{
			try
			{
				DataSet set = (DataSet) component;
				if (set.hierarchy().getComponents(true).contains(data))
				{
					for (Component subComponent : set.hierarchy().getComponents(true))
					{
						try
						{
							Data dat = (Data) subComponent;
							if (dat.properties().getName().equals(title))
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
			if (dataOps(element).isPreviousDataStored())
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
		for (Component component : super.getEnvironment().hierarchy().getComponents(true))
		{
			try
			{
				Data element = (Data) component;

				if (CoreDataGroup.ALL_DATA.contains(element))
				{
					if (dataOps(element).isPreviousDataStored())
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
