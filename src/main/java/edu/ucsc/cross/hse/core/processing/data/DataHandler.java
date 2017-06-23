package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.processing.execution.Processor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class DataHandler extends ProcessorAccess implements DataAccessor
{

	private Double lastStoreTime = -10.0; // time since last data was stored,
											// used to store data at specified
											// interval
	private ArrayList<Data> dataElementsToStore; // list of all data elements
													// that are to be stored

	public static Double postJumpStoreIncrement = .0000000001; // amount of time
																// subtracted to
																// store data
																// before a jump
																// occurs in
																// order to
																// capture data
																// before and
																// after a jump

	public DataHandler(Processor processor)
	{
		super(processor);
		dataElementsToStore = new ArrayList<Data>();

	}

	public Data getDataByIndex(Integer index)
	{
		return dataElementsToStore.get(index);
	}

	@Override
	public ArrayList<Data> getAllStateData()
	{
		return dataElementsToStore;
	}

	@Override
	public ArrayList<String> getAllStateNames()
	{
		ArrayList<String> stateElements = new ArrayList<String>();
		for (Data allStates : dataElementsToStore)
		{
			if (!stateElements.contains(allStates.getInformation().getName()))
			{
				stateElements.add(allStates.getInformation().getName());
			}
		}
		// System.out.println(stateElements.toString());

		return stateElements;
	}

	@Override
	public ArrayList<Data> getDataByTitle(String title)
	{
		ArrayList<Data> datas = new ArrayList<Data>();
		for (Data element : dataElementsToStore)
		{
			if (element.getInformation().getName().equals(title))
			{
				datas.add(element);
			}
		}
		return datas;
	}

	// public Data checkIfMatchingElementInDataSet(Data data, String title)
	// {
	// for (Component component : getEnv().getContents().getComponents(true))
	// {
	// try
	// {
	// if (component.getContents().getComponents(true).contains(data))
	// {
	// for (Data dat : component.getContents().getObjects(Data.class, true))
	// {
	//
	// if (dat.getInformation().getName().equals(title))
	// {
	// return dat;
	// }
	//
	// }
	// }
	//
	// } catch (Exception notDataSet)
	// {
	//
	// }
	// }
	//
	// return null;
	// }

	public void storeData(Double time)
	{
		for (Data element : dataElementsToStore)
		{
			if (getDataOperator(element).isPreviousDataStored())
			{
				getDataOperator(element).storeValue(time);
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
		} else if (t > lastStoreTime + getSettings().getDataSettings().dataStoreIncrement)// settings.dataStoreIncrement)
		{
			lastStoreTime = t;
			storeData(t);
		}
	}

	public void loadStoreStates()
	{
		dataElementsToStore.clear();
		for (Component component : super.getEnv().getContents().getComponents(true))
		{
			try
			{
				Data element = (Data) component;

				if (element.getActions().getDataProperties().changesContinuously())
				{
					if (getDataOperator(element).isPreviousDataStored())
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
