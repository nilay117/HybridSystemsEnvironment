package edu.ucsc.cross.hse.core.object.domain;

import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import bs.commons.unitvars.units.NoUnit;

public class ValueDomain
{

	private Double value;
	private Double min;
	private Double max;

	@SuppressWarnings("unchecked")
	public ValueDomain(Double value)
	{
		assignInitialValue(value);
	}

	@SuppressWarnings("unchecked")
	public ValueDomain(Double min, Double max)
	{
		assignInitialValue(min);
		setRandomValues(min, max);
	}

	private void assignInitialValue(Double val)
	{
		value = val;

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
	public Double getValue()
	{
		Double generatedValue = value;

		if (min != null && max != null)
		{
			generatedValue = (((max - min) * Math.random()) + min);

		}
		return generatedValue;

	}

	@SuppressWarnings("unchecked")
	public Double getNumberValue()
	{
		Double numVal = (max - min) * Math.random() + min;
		return numVal;//
		// return Double.class.cast(((max - min) * Math.random()) + min);

	}

	public void setValue(Double val)
	{
		value = val;
	}

}
