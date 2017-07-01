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

	// Stored Data Access Functions
	// ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	// Stored Data Access Functions: these functions are protected instead of
	// public because they access previosly stored data, which may or may not be
	// allowed depending on the situation. If a vehicle is traveling around
	// without a storage device or any means to record data, it would not have
	// access to past states and information. If it were to have a storage
	// device recording the states then a connector model can be implemented
	// ß to allow the system to access the data
	// ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	/*
	 * /////////////////////////////////////////////////////////////////////////
	 * Internal Operation Functions : everything below for the processing system
	 * only, it is not recommended to call these elsewhere unless trying to
	 * alter the standard behavior
	 * /////////////////////////////////////////////////////////////////////////
	 */
	public DataWorker(Data<T> data)
	{
		super(data);
		this.data = data;
	}

	public ValueDomain<T> getInitialValue()
	{
		return data.initialVal;
	}

	public Unit getDefaultUnit()
	{
		if (data.defaultUnit == null)
		{
			// IO.warn("attempted to get default unit of " + get().toString());
		}
		return data.defaultUnit;
	}

	public DataTypeProperties getDataProperties()
	{
		return data.dataType;
	}

	public Double getDoubleValue()
	{
		return getDoubleValue(null);
	}

	public Double getDoubleValue(Unit unit)
	{
		try
		{
			if (data.getValue().getClass().getSuperclass().equals(UnitValue.class))
			{
				if (unit == null)
				{
					unit = ((UnitValue) data.getValue()).getUnit();
				}
				try
				{
					return (Double) ((UnitValue) data.getValue()).get(unit);
				} catch (UnitException e)
				{
					e.printStackTrace();
					return null;
					// TODO Auto-generated catch block

				}
			} else if (data.getValue().getClass().equals(Double.class))
			{
				return (Double) data.getValue();
			} else
			{
				return null;
			}
		} catch (Exception nullVal)
		{
			return null;
		}
	}

	public Double getStoredDoubleValue(Double time)
	{
		if (storedTimes == null)
		{
			getStoredTimesMap();
		}
		Double value = null;
		try
		{

			value = (Double) data.savedHybridValues.get(storedTimes.get(time));
		} catch (Exception notDouble)
		{
			try
			{
				UnitValue unitVal = (UnitValue) data.savedHybridValues.get(storedTimes.get(time));
				value = (Double) unitVal.get(unitVal.getUnit());
			} catch (Exception notNumber)
			{

			}
		}
		return value;
	}

	public ArrayList<Double> getStoredDoubleValues(Double time)
	{
		if (storedTimes == null)
		{
			getStoredTimesMap();
		}
		ArrayList<Double> values = new ArrayList<Double>();
		ArrayList<HybridTime> times = storedTimes.get(time);
		for (HybridTime valTime : times)
		{
			try
			{

				values.add((Double) data.savedHybridValues.get(valTime));
			} catch (Exception notDouble)
			{
				try
				{
					UnitValue unitVal = (UnitValue) data.savedHybridValues.get(valTime);
					values.add((Double) unitVal.get(unitVal.getUnit()));
				} catch (Exception notNumber)
				{

				}
			}
		}
		return values;
	}

	public Double getStoredDoubleValue(Double time, Unit unit)
	{
		Double value = null;
		try
		{

			value = (Double) data.savedValues.get(time);
		} catch (Exception notDouble)
		{
			try
			{
				UnitValue unitVal = (UnitValue) data.savedValues.get(time);
				if (unit == null)
				{
					value = (Double) unitVal.get(unitVal.getUnit());
				} else
				{
					value = (Double) unitVal.get(unit);
				}
			} catch (Exception notNumber)
			{

			}
		}
		return value;
	}

	public T getStoredValue(Double time)
	{
		T value = null;
		try
		{
			value = data.savedValues.get(time);
		} catch (Exception noValue)
		{

		}
		return value;
	}

	public HashMap<Double, T> getStoredValues()
	{
		return data.savedValues;
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
