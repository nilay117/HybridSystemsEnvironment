package edu.ucsc.cross.hse.core.framework.data;

import java.util.HashMap;

import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;

public class DataOperator extends ComponentOperator
{

	protected static HashMap<Data, DataOperator> dataOperators = new HashMap<Data, DataOperator>();
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

	public static DataOperator dataOp(Data data)
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

}