package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingElement;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class DataHandler extends ProcessingElement implements DataAccessor
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

	ArrayList<Double> storeTimes;

	public DataHandler(CentralProcessor processor)
	{
		super(processor);
		dataElementsToStore = new ArrayList<Data>();
		storeTimes = new ArrayList<Double>();
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
			if (!stateElements.contains(allStates.getLabels().getClassification()))
			{
				stateElements.add(allStates.getLabels().getClassification());
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
			if (element.getLabels().getClassification().equals(title))
			{
				datas.add(element);
			}
		}
		return datas;
	}

	@Override
	public Data getDifferentDataFromSameDataSet(Data data, String title)
	{
		for (Component component : getEnv().getContents().getComponents(true))
		{
			try
			{
				if (component.getContents().getComponents(true).contains(data))
				{
					for (Data dat : component.getContents().getObjects(Data.class, true))
					{

						if (dat.getLabels().getClassification().equals(title))
						{
							return dat;
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
		//if (!this.getComponents().outOfAllDomains())
		{
			Double adjustedTime = time;//Math.round(time * 100000.0) / 100000.0;
			storeTimes.add(adjustedTime);
			for (Data element : dataElementsToStore)
			{
				if (getDataOperator(element).isDataStored())
				{
					getDataOperator(element).storeValue(adjustedTime);
				}
			}
		}
		// IO.out(getConsole().getDataElementStoreString(time, true));
	}

	public void removeLastDataPoint()
	{
		for (Data element : dataElementsToStore)
		{
			if (getDataOperator(element).isDataStored())
			{
				getDataOperator(element).removeDataValue(lastStoreTime);
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
			//if ((!this.getComponents().outOfAllDomains()))
			{
				lastStoreTime = t;
				storeData(t);
			}
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

				// if (DataOperator.getOperator(element).isDataStored())
				{
					if (getDataOperator(element).isDataStored())
					{
						dataElementsToStore.add(element);
					}
				}
			} catch (Exception notElement)
			{

			}
		}
	}

	public void restoreDataAfterIntegratorFail()
	{

		Double revertTime = findRevertTime();
		if (revertTime >= 0)
		{
			for (Data element : dataElementsToStore)
			{
				//if (getDataOperator(element).isDataStored())
				{
					element.setValue(element.getActions().getStoredValue(revertTime));
				}
			}
			this.setEnvTime(revertTime);
			//this.getComponents().performAllTasks(false);
		}
	}

	private Double findRevertTime()
	{
		Double revertTime = getEnv().getEnvironmentTime() - this.getSettings().getComputationSettings().odeMaxStep;
		Double time = 0.0;
		for (Double t : storeTimes)
		{
			if (t > revertTime)
			{
				break;
			} else
			{
				time = t;
			}
		}
		return time;
	}
}
