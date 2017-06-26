package edu.ucsc.cross.hse.core.object.domain;

import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import bs.commons.unitvars.units.NoUnit;

public class ValueDomain<T>
{

	private T value;
	private Unit unit;
	private Double min;
	private Double max;

	@SuppressWarnings("unchecked")
	public ValueDomain(T value)
	{
		assignInitialValue(value);
	}

	private void assignInitialValue(T val)
	{
		value = val;
		try
		{
			if (val.getClass().getSuperclass().equals(UnitValue.class))
			{
				unit = (Unit) ((UnitValue) val).getUnit();
				try
				{

					Double minmax = (Double) ((UnitValue) val).get(unit);
					min = minmax;
					max = minmax;
				} catch (UnitException e)
				{
					// TODO Auto-generated catch block
					min = max = 0.0;
					e.printStackTrace();
				}
			} else
			{
				unit = NoUnit.NONE;
				if (val.getClass().equals(Double.class))
				{
					Double doubleVal = (Double) val;
					min = max = doubleVal;
				}
			}
		} catch (Exception e)
		{
			unit = NoUnit.NONE;
		}
	}

	@SuppressWarnings("unchecked")
	public void setRandomValues(Double min, Double max)
	{
		this.min = min;
		this.max = max;

	}

	@SuppressWarnings("unchecked")
	public void setFixedValue(Double val)
	{
		min = max = val;

	}

	@SuppressWarnings("unchecked")
	public void setRandomValues(Double min, Double max, Unit unit)
	{
		this.min = min;
		this.max = max;
		this.unit = unit;

	}

	@SuppressWarnings("unchecked")
	public void setFixedValue(Double val, Unit unit)
	{
		min = max = val;
		this.unit = unit;

	}

	@SuppressWarnings("unchecked")
	public T getValue()
	{
		Double generatedValue = 0.0;
		T newVal = value;
		if (min != null && max != null)
		{
			generatedValue = Double.class.cast(((max - min) * Math.random()) + min);

			if (value.getClass().equals(Double.class))
			{

				newVal = (T) generatedValue;
			} else if (!unit.equals(NoUnit.NONE))
			{
				// newVal = ObjectCloner.cloner.deepClone(value);
				UnitValue unitVal = (UnitValue) newVal;
				try
				{
					unitVal.set(generatedValue, unit);
				} catch (UnitException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return newVal;

	}

	@SuppressWarnings("unchecked")
	public Double getNumberValue()
	{
		Double numVal = (max - min) * Math.random() + min;
		return numVal;//
		// return Double.class.cast(((max - min) * Math.random()) + min);

	}

	public void setValue(T val)
	{
		value = val;
	}

}
