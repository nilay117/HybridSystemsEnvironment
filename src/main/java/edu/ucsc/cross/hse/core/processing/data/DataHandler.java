package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.environment.HybridEnvironment;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
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
			if (!stateElements.contains(allStates.component().getLabels().getClassification()))
			{
				stateElements.add(allStates.component().getLabels().getClassification());
			}
		}

		return stateElements;
	}

	@Override
	public ArrayList<Data> getDataByTitle(String title)
	{
		ArrayList<Data> datas = new ArrayList<Data>();
		for (Data element : dataElementsToStore)
		{
			if (element.component().getLabels().getClassification().equals(title))
			{
				datas.add(element);
			}
		}
		return datas;
	}

	@Override
	public HashMap<Data, ArrayList<Double[]>> getData(String title)
	{
		HashMap<Data, ArrayList<Double[]>> data = new HashMap<Data, ArrayList<Double[]>>();
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

	public HashMap<Data, ArrayList<Double[]>> getDatazz(String title)
	{
		HashMap<Data, ArrayList<Double[]>> data = new HashMap<Data, ArrayList<Double[]>>();
		for (Data dat : getDataByTitle(title))
		{
			ArrayList<Double[]> values = new ArrayList<Double[]>();
			Set<HybridTime> tim = dat.component().getStoredValues().keySet();
			HashMap<Integer, ArrayList<HybridTime>> orderedTimes = new HashMap<Integer, ArrayList<HybridTime>>();
			for (HybridTime timez : tim)
			{
				if (!orderedTimes.containsKey(timez.getJumpIndex()))
				{
					orderedTimes.put(timez.getJumpIndex(), new ArrayList<HybridTime>());
				}

				orderedTimes.get(timez.getJumpIndex()).add(timez);
			}
			ArrayList<Integer> times = new ArrayList<Integer>(
			Arrays.asList(orderedTimes.keySet().toArray(new Integer[orderedTimes.size()])));
			Collections.sort(times);
			for (Integer timei : times)
			{
				ArrayList<HybridTime> timezz = orderedTimes.get(timei);
				for (HybridTime timez : timezz)
				{
					Double[] vals = new Double[]
					{ timez.getTime(), (Double) dat.component().getStoredValue(timez) };
					values.add(vals);
				}
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
		storeStates.addAll(super.getEnv().component().getContent().getObjects(State.class, true));
		for (Data data : super.getEnv().component().getContent().getObjects(Data.class, true))
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
