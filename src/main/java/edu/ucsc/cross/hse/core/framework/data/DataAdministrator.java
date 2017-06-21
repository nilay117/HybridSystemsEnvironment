package edu.ucsc.cross.hse.core.framework.data;

import java.util.HashMap;

import edu.ucsc.cross.hse.core.framework.component.ComponentAdministrator;

public class DataAdministrator<T> extends ComponentAdministrator
{

	protected static HashMap<Data<?>, DataAdministrator<?>> dataOperators = new HashMap<Data<?>, DataAdministrator<?>>();
	Data<T> element;

	protected DataAdministrator(Data<T> component)
	{
		super(component);
		try
		{
			element = component;
		} catch (Exception e)
		{

			element = (Data<T>) DataFactory.property.create("Not Data");
		}

	}

	public static <S> DataAdministrator<S> getOperator(Data<S> data)
	{
		if (dataOperators.containsKey(data))
		{
			return (DataAdministrator<S>) dataOperators.get(data);

		} else
		{

			DataAdministrator config = new DataAdministrator(data);
			dataOperators.put(data, config);
			return config;

		}
	}

	public void storeValue(Double time)
	{
		element.storeValue(time, true);
	}

	public void storeValue(Double time, boolean override_save)
	{
		element.storeValue(time, override_save);
	}

	public boolean isPreviousDataStored()
	{
		return element.save;
	}

	public void setStoredValues(HashMap<Double, T> vals)
	{
		element.savedValues = vals;
	}

	public void storePreJumpValue()
	{
		element.storePreJumpValue();
	}

}