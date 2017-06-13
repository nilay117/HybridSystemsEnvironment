package edu.ucsc.cross.hse.core.framework.data;

import java.util.HashMap;

import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;

public class DataOperator<T> extends ComponentOperator
{

	protected static HashMap<Data, DataOperator> dataOperators = new HashMap<Data, DataOperator>();
	Data<T> element;

	protected DataOperator(Data<T> component)
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

	public static <S> DataOperator<S> getOperator(Data<S> data)
	{
		if (dataOperators.containsKey(data))
		{
			return dataOperators.get(data);

		} else
		{

			DataOperator config = new DataOperator(data);
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