package edu.ucsc.cross.hse.core.object.domain;

import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import bs.commons.unitvars.units.NoUnit;

public class ValueDomain
{

	private Double value; // fixed value
	private Double min; // minimum value of the range

	/*
	 * Get minimum value of domain
	 */
	public Double getMin()
	{
		return min;
	}

	/*
	 * Get maximum value of domain
	 */
	public Double getMax()
	{
		return max;
	}

	private Double max; // maximum value of the range

	/*
	 * Constructor that implements a value domain with a fixed value
	 */
	public ValueDomain(Double value)
	{
		setFixedValue(value);
	}

	/*
	 * Constructor that implements a domain that is ranged
	 */
	@SuppressWarnings("unchecked")
	public ValueDomain(Double min, Double max)
	{
		setFixedValue(min);
		setRandomValues(min, max);
	}

	/*
	 * Set range of values to select random values from
	 */
	@SuppressWarnings("unchecked")
	public void setRandomValues(Double min, Double max)
	{
		this.min = min;
		this.max = max;

	}

	/*
	 * Set a fixed value as the domain
	 */
	@SuppressWarnings("unchecked")
	public void setFixedValue(Double val)
	{
		value = min = max = val;
	}

	/*
	 * Get a value from the domain, will be random is domain is ranged
	 */
	public Double getValue()
	{
		return getValue(true);

	}

	/*
	 * Get a value from the domain, can select if randomized
	 */
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

	/*
	 * Set the value only
	 */
	public void setValue(Double val)
	{
		value = val;
	}

	/*
	 * Check if the domain is a range
	 */
	public boolean isRange()
	{
		return min != max;
	}
}
