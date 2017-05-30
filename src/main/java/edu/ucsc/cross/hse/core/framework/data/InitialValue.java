package edu.ucsc.cross.hse.core.framework.data;

import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import bs.commons.unitvars.units.NoUnit;
import edu.ucsc.cross.hse.core.procesing.utils.CloneUtil;

public class InitialValue<T>
{

	private T value;
	private Unit unit;
	private Double min;
	private Double max;

	@SuppressWarnings("unchecked")
	public InitialValue(T value)
	{
		assignInitialValue(value);
	}

	private void assignInitialValue(T val)
	{
		value = val;

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
		if (min != null && max != null)
		{
			generatedValue = Double.class.cast(((max - min) * Math.random()) + min);
		}
		if (value.getClass().equals(Double.class))
		{

			return (T) generatedValue;
		} else if (!unit.equals(NoUnit.NONE))
		{
			T newVal = CloneUtil.cloner.deepClone(value);
			UnitValue unitVal = (UnitValue) newVal;
			try
			{
				unitVal.set(generatedValue, unit);
			} catch (UnitException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return newVal;

		} else

		{
			return value;
		}

	}

	@SuppressWarnings("unchecked")
	public Double getNumberValue()
	{

		return Double.class.cast(((max - min) * Math.random()) + min);

	}

	public void setValue(T val)
	{
		value = val;
	}

}
