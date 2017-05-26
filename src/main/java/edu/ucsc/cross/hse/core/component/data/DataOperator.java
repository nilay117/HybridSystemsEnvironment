package edu.ucsc.cross.hse.core.component.data;

import java.util.HashMap;

import edu.ucsc.cross.hse.core.component.foundation.ComponentOperator;

public class DataOperator extends ComponentOperator
{

	private static HashMap<Data, DataOperator> dataOperators = new HashMap<Data, DataOperator>();
	Data element;

	protected DataOperator(Data component)
	{
		super(component);
		try
		{
			element = component;
		} catch (Exception e)
		{

			element = DataFactory.property.create("Not Data");
		}

	}

	public static DataOperator operator(Data data)
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

	public <S> void storeValue(Double time)
	{
		element.storeValue(time, true);
	}

	public <S> void storeValue(Double time, boolean override_save)
	{
		element.storeValue(time, override_save);
	}

	public boolean isPreviousDataStored()
	{
		return element.save;
	}

	public void setStoredValues(HashMap<Double, ?> vals)
	{
		element.savedValues = vals;
	}

	public void restorePreJumpValue()
	{
		element.restorePreJumpValue();
	}

	public void storePreJumpValue()
	{
		element.storePreJumpValue();
	}

	// /*
	// * Toggles whether the data contained in the data set is simulated or not
	// */
	// public void simulated(boolean simulate)
	// {
	// boolean uninitialized = true;
	// if (isInitialized() != null)
	// {
	// uninitialized = uninitialized && !isInitialized();
	//
	// }
	// if (uninitialized)
	// {
	// set.s
	// }
	// }catch(
	//
	// Exception e)
	// {
	//
	// }
}