package edu.ucsc.cross.hse.core.framework.data;

import java.util.HashMap;

import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import edu.ucsc.cross.hse.core.framework.component.ComponentActions;

public class DataActions<T> extends ComponentActions
{

	protected static HashMap<Data<?>, DataActions<?>> dataActions = new HashMap<Data<?>, DataActions<?>>();
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

	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Internal Operation Functions : everything below for the processing system
	// only, it is not recommended to call these elsewhere unless trying to
	// alter the standard behavior
	////////////////////////////////////////////////////////////////////////////////////////////////////

	public DataActions(Data<T> data)
	{
		super(data);
		this.data = data;
	}

	public Unit getDefaultUnit()
	{
		if (data.defaultUnit == null)
		{
			// IO.warn("attempted to get default unit of " + get().toString());
		}
		return data.defaultUnit;
	}

	public DataType getDataClass()
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
			if (data.value().getClass().getSuperclass().equals(UnitValue.class))
			{
				if (unit == null)
				{
					unit = ((UnitValue) data.value()).getUnit();
				}
				try
				{
					return (Double) ((UnitValue) data.value()).get(unit);
				} catch (UnitException e)
				{
					e.printStackTrace();
					return null;
					// TODO Auto-generated catch block

				}
			} else if (data.value().getClass().equals(Double.class))
			{
				return (Double) data.value();
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
		Double value = null;
		try
		{

			value = (Double) data.savedValues.get(time);
		} catch (Exception notDouble)
		{
			try
			{
				UnitValue unitVal = (UnitValue) data.savedValues.get(time);
				value = (Double) unitVal.get(unitVal.getUnit());
			} catch (Exception notNumber)
			{

			}
		}
		return value;
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

	public void setStorePreviousValues(boolean store)
	{
		data.save = store;
	}

	// Internal Operation Functions
	public static <S> DataActions<S> getConfigurer(Data<S> component)
	{
		if (dataActions.containsKey(component))
		{
			return (DataActions<S>) dataActions.get(component);

		} else
		{

			DataActions<S> config = new DataActions<S>(component);
			dataActions.put(component, config);
			return config;

		}
	}
}
