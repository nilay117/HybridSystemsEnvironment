package edu.ucsc.cross.hse.core.framework.data;

import java.util.ArrayList;
import java.util.HashMap;

import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import edu.ucsc.cross.hse.core.framework.component.ComponentWorker;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.object.domain.ValueDomain;

public class DataWorker<T> extends ComponentWorker
{

	protected static HashMap<Data<?>, DataWorker<?>> dataActions = new HashMap<Data<?>, DataWorker<?>>();
	public HashMap<Double, ArrayList<HybridTime>> storedTimes;
	public Data<T> data;

	public DataWorker(Data<T> data)
	{
		super(data);
		this.data = data;
	}

	public HashMap<HybridTime, T> getStoredValues(Double time)
	{
		HashMap<HybridTime, T> values = new HashMap<HybridTime, T>();
		try
		{
			if (storedTimes.containsKey(time))
				for (HybridTime storedVal : storedTimes.get(time))
				{
					values.put(storedVal, getStoredValue(storedVal));
				}
		} catch (Exception noValue)
		{

		}
		return values;
	}

	public HashMap<HybridTime, T> getStoredHybridValues()
	{
		return data.savedHybridValues;
	}

	public HashMap<Double, ArrayList<HybridTime>> getStoredTimesMap()
	{
		storedTimes = new HashMap<Double, ArrayList<HybridTime>>();
		for (HybridTime hybridTime : data.savedHybridValues.keySet())
		{
			if (storedTimes.containsKey(hybridTime.getTime()))
			{
				storedTimes.get(hybridTime.getTime()).add(hybridTime);
			} else
			{
				ArrayList<HybridTime> times = new ArrayList<HybridTime>();
				times.add(hybridTime);
				storedTimes.put(hybridTime.getTime(), times);
			}
		}
		return storedTimes;
	}

	public T getStoredValue(HybridTime hybrid_time)
	{
		T val = this.getStoredHybridValues().get(hybrid_time);
		return val;
	}

	public void setStorePreviousValues(boolean store)
	{
		data.save = store;
	}

	// Internal Operation Functions
	public static <S> DataWorker<S> getConfigurer(Data<S> component)
	{
		if (dataActions.containsKey(component))
		{
			return (DataWorker<S>) dataActions.get(component);

		} else
		{

			DataWorker<S> config = new DataWorker<S>(component);
			dataActions.put(component, config);
			return config;

		}
	}
}
