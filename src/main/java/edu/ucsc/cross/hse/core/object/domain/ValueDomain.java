package edu.ucsc.cross.hse.core.object.domain;

import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import bs.commons.unitvars.units.NoUnit;

public class ValueDomain
{

	private Double value; // fixed value
	private Double min; // minimum value of the range
	private Double max; // maximum value of the range

	/*
	 * Constructor that implements a value domain with a fixed range
	 */
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
		value = min = max = val;
	}

	@SuppressWarnings("unchecked")
	public Double getValue()
	{
		return getValue(true);

	}

	@SuppressWarnings("unchecked")
	public Double getValue(boolean regenerate)
	{
		Double generatedValue = value;
		if (regenerate)
		{
			if (min != null && max != null)
			{
				generatedValue = (((max - min) * Math.random()) + min);
				value = generatedValue;

			}
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
