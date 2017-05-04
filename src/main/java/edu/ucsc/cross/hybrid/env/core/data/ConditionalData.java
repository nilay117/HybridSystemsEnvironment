package edu.ucsc.cross.hybrid.env.core.data;

import java.util.HashMap;

import edu.ucsc.cross.hybrid.env.core.components.Data;
import edu.ucsc.cross.hybrid.env.structural.BaseData;

public class ConditionalData<T> extends Data<T>
{

	private T zeroElement; // Zero value element - returned for states where value is zero
	private Data<?> dependency; // status that determines the value
	protected HashMap<Object, Data<T>> statusValues; // status value map

	public ConditionalData(String name, Data<?> state_value, T zero_element)
	{
		super(zero_element, BaseData.PROPERTY, name, "", false);
		zeroElement = zero_element;
		dependency = state_value;
		statusValues = new HashMap<Object, Data<T>>();
	}

	public ConditionalData(Data<?> state_value, T zero_element)
	{
		super(zero_element, BaseData.PROPERTY, "Conditional Value", "", false);
		zeroElement = zero_element;
		dependency = state_value;
		statusValues = new HashMap<Object, Data<T>>();
	}

	public static <S> void loadStateValue(ConditionalData<S> state, Object status, Data<S> value)
	{

		state.statusValues.put(status, value);
	}

	@Override
	public T get()
	{
		T returnValue = null;
		try
		{
			if (statusValues.containsKey(dependency.get()))
			{
				returnValue = statusValues.get(dependency.get()).get();

			}
		} catch (Exception ee)
		{

		}
		if (returnValue == null)
		{
			returnValue = zeroElement;
		}
		return returnValue;
	}

	public boolean allZeroValues()
	{
		for (Data<T> val : statusValues.values())
		{
			try
			{
				if (Data.getNumberValue(val, null) > 0)
				{
					return false;
				}
			} catch (Exception nonNumericVal)
			{
				if (Data.getNumberValue(val, null) != null)
				{
					return false;
				}
			}
		}
		return true;
	}
}
