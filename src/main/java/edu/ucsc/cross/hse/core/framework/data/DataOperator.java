package edu.ucsc.cross.hse.core.framework.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;

public class DataOperator<T> extends ComponentOperator
{

	protected static HashMap<Data<?>, DataOperator<?>> dataOperators = new HashMap<Data<?>, DataOperator<?>>();
	Data<T> element;
	/*
	 * This list determines what elements will need to be cloned before being
	 * saved to ensure the right values are stored. For example, a list would
	 * need to be copied because a list of some sort that was saved without
	 * being copied will reflect any changes made in the future unless the list
	 * is reinitialized. A value such as a double can be saved without being
	 * copied because the stored pointer points to the correct saved value even
	 * when a new value is stored. This list below contains general classes that
	 * not need to be cloned to save
	 */
	static public final ArrayList<Class> changableClasses = new ArrayList<Class>(Arrays.asList(new Class[]
	{ Double.class, String.class, Integer.class, Long.class, Number.class, Boolean.class, Enum.class }));

	protected DataOperator(Data<T> component)
	{
		super(component);
		try
		{
			element = component;
		} catch (Exception e)
		{

			element = (Data<T>) DataFactory.data.create("Not Data");
		}

	}

	public static <S> DataOperator<S> getOperator(Data<S> data)
	{
		if (dataOperators.containsKey(data))
		{
			return (DataOperator<S>) dataOperators.get(data);

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

	public boolean isDataStored()
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

	public void removeDataValue(Double time)
	{
		if (element.getActions().getStoredValues().containsKey(time))
		{
			element.getActions().getStoredValues().remove(time);
		}
	}

	public void setFlow(boolean flow)
	{
		element.setDerivative(element.zeroDerivative);
	}

	/*
	 * Determine if a copy is required to save a given object
	 * 
	 * @param object - object to be evaluated
	 * 
	 * @return true if a copy needs to be made, false otherwise
	 */
	public static <T> boolean isCopyRequiredOnSave(T object)
	{
	
		if (object != null)
		{
			return changableClasses.contains(object);
		}
		return true;
	}
}