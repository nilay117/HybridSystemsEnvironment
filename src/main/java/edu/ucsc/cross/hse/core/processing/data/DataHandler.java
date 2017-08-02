package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class DataHandler extends ProcessorAccess implements DataAccessor
{

	/*
	 * Time since last data was stored, used to store data at specified interval
	 */
	private Double lastStoreTime = -10.0;

	/*
	 * List of all data elements that are to be stored
	 */
	private ArrayList<Data<?>> dataElementsToStore;

	/*
	 * List of all times corresponding to stored data
	 */
	ArrayList<Double> storeTimes;

	/*
	 * Constructor that links central processor
	 * 
	 * @param processor - central processor that this data handler is a part of
	 */
	public DataHandler(CentralProcessor processor)
	{
		super(processor);
		dataElementsToStore = new ArrayList<Data<?>>();
		storeTimes = new ArrayList<Double>();
	}

	/*
	 * Gets a data element by its index in the list
	 * 
	 * @param index - index of data element
	 * 
	 * @return data element corresponding to the index
	 */
	public Data getDataByIndex(Integer index)
	{
		return dataElementsToStore.get(index);
	}

	/*
	 * Get list of all state data
	 */
	@Override
	public ArrayList<Data<?>> getAllStateData()
	{
		return dataElementsToStore;
	}

	/*
	 * Get list of all state names
	 */
	@Override
	public ArrayList<String> getAllStateNames()
	{
		ArrayList<String> stateElements = new ArrayList<String>();
		for (Data allStates : dataElementsToStore)
		{
			if (!stateElements.contains(allStates.component().getLabels().getClassification()))
			{
				stateElements.add(allStates.component().getLabels().getClassification());
			}
		}

		return stateElements;
	}

	/*
	 * Gets data that matches a given title
	 * 
	 * @param - title - title of data element
	 * 
	 * @return list of data elements corresponding to the specified title
	 */
	@Override
	public ArrayList<Data<?>> getDataByTitle(String title)
	{
		ArrayList<Data<?>> datas = new ArrayList<Data<?>>();
		for (Data element : dataElementsToStore)
		{
			if (element.component().getLabels().getClassification().equals(title))
			{
				datas.add(element);
			}
		}
		return datas;
	}

	/*
	 * Gets all data points for data elements that matches a given title
	 * 
	 * @param - title - title of data element
	 * 
	 * @return mapping of data elements to the list of it's data points for
	 * elements corresponding to the specified title
	 */
	@Override
	public HashMap<Data<?>, ArrayList<Double[]>> getData(String title)
	{
		HashMap<Data<?>, ArrayList<Double[]>> data = new HashMap<Data<?>, ArrayList<Double[]>>();
		for (Data dat : getDataByTitle(title))
		{
			ArrayList<Double[]> values = new ArrayList<Double[]>();
			Set<HybridTime> tim = dat.component().getStoredValues().keySet();
			for (HybridTime timez : tim)
			{
				Double[] vals = new Double[]
				{ timez.getTime(), (Double) dat.component().getStoredValue(timez) };
				values.add(vals);
			}
			data.put(dat, values);
		}
		return data;
	}

	@Override
	public Data getDifferentDataFromSameDataSet(Data data, String title)
	{
		for (Component component : getEnv().component().getContent().getComponents(true))
		{
			try
			{
				if (component.component().getContent().getComponents(true).contains(data))
				{
					for (Data dat : component.component().getContent().getObjects(Data.class, true))
					{

						if (dat.component().getLabels().getClassification().equals(title))
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

		storeTimes.add(time);
		for (Data element : dataElementsToStore)
		{
			if (getDataOperator(element).isDataStored())
			{
				getDataOperator(element).storeValue(time);
			}
		}

	}

	public void storeJumpData(HybridTime pre_jump_time)
	{
		HybridTime postJumpTime = this.getEnvironmentOperator().getEnvironmentHybridTime().getCurrent();
		storeTimes.add(pre_jump_time.getTime());
		storeTimes.add(postJumpTime.getTime());
		for (Data element : dataElementsToStore)
		{
			if (getDataOperator(element).isDataStored())
			{

				Data state = element;
				DataOperator.getOperator(state).getPreJumpValue();
				state.getValue();
				if (DataOperator.getOperator(state).getPreJumpValue() != null)
				{
					if (!DataOperator.getOperator(state).getPreJumpValue().equals(state.getValue()))
					{
						getDataOperator(state).storeValue(DataOperator.getOperator(state).getPreJumpValue(),
						pre_jump_time);

						getDataOperator(state).storeValue(state.getValue(), postJumpTime);
					}
				}
			}

		}

	}

	public HashMap<String, HashMap<HybridTime, ?>> getAllMaps()
	{
		HashMap<String, HashMap<HybridTime, ?>> mapz = new HashMap<String, HashMap<HybridTime, ?>>();
		for (Data element : dataElementsToStore)
		{
			if (getDataOperator(element).isDataStored())
			{
				mapz.put(element.component().getAddress(), element.component().getStoredValues());
			}
		}
		return mapz;
	}

	public void storeData(double t, boolean override_store_time)
	throws MaxCountExceededException, DimensionMismatchException
	{
		if (override_store_time)
		{
			storeData(t);
		} else if (t > lastStoreTime + getSettings().getDataSettings().dataStoreIncrement)// settings.dataStoreIncrement)
		{
			// if ((!this.getComponents().outOfAllDomains()))
			{
				lastStoreTime = t;
				storeData(t);
			}
		}
	}

	public void loadStoreStates()
	{
		dataElementsToStore.clear();
		ArrayList<Data> storeStates = new ArrayList<Data>();
		// storeStates.addAll(super.getEnv().component().getContent().getObjects(State.class,
		// true));
		for (Data data : super.getEnv().component().getContent().getData(true))
		{
			if (!storeStates.contains(data))
			{
				storeStates.add(data);
			}
		}
		for (Data component : storeStates)
		{
			try
			{

				// if (DataOperator.getOperator(element).isDataStored())
				{
					if (getDataOperator(component).isDataStored())
					{
						dataElementsToStore.add(component);
					}
				}
			} catch (Exception notElement)
			{

			}
		}
	}

	@Override
	public ArrayList<State> getStatesByTitle(String title)
	{
		ArrayList<State> datas = new ArrayList<State>();
		for (Data element : dataElementsToStore)
		{
			try
			{
				State state = (State) element;

				if (element.component().getLabels().getClassification().equals(title))
				{
					datas.add(state);
				}
			} catch (Exception notState)
			{

			}
		}
		return datas;
	}

}
