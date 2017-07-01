package edu.ucsc.cross.hse.core.framework.data;

import java.util.HashMap;

import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.exceptions.UnitException;
import bs.commons.unitvars.units.NoUnit;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.environment.ContentOperator;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.object.domain.ValueDomain;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;

public class State extends Data<Double>// DynamicData<T>
{

	private Double derivative; // current derivative of the data (if the data
								// changes
								// continuously)

	protected ValueDomain elementDomain;

	private Double prejump; // pre-jump value stored immediately before jump
							// occurs

	@Override
	public Double getValue()
	{
		return getValue(false);
	}

	@Override
	public Double getValue(boolean randomize_from_domain)
	{

		if (randomize_from_domain)
		{
			element = elementDomain.getValue();

			return element;
		} else if (getEnvironment().isJumpOccurring())
		{
			return prejump;
		} else
		{

			return element;
		}
	}

	@Override
	public void setValue(Double min, Double max)
	{
		elementDomain.setRandomValues(min, max);
		element = elementDomain.getValue();
	}

	public Double getDerivative()
	{
		return derivative;
	}

	public void setDerivative(Double derivative)
	{
		this.derivative = derivative;
	}

	@Override
	public DataWorker<Double> getActions()
	{
		return DataWorker.getConfigurer(this);
	}

	@Override
	public void initialize()
	{
		boolean initialize = true;
		try
		{
			initialize = initialize && !ComponentOperator.getOperator(this).isInitialized();
		} catch (Exception nullInit)
		{
			initialize = true;
		}
		if (initialize)
		{
			setValue(elementDomain.getValue());
		}

	}

	public State(String name, Double obj)
	{
		super(name, obj, "", true);
		init(obj);
	}

	public State(String name, Double obj_min, Double obj_max)
	{
		super(name, obj_min, "", true);
		init(obj_min, obj_max);
	}

	public State(Double obj)
	{
		super("State", obj, "", true);
		init(obj);

	}

	public State(Double obj, String name, String description, Boolean save_default)
	{
		super(name, obj, description, save_default);
		init(obj);
	}

	public State(String name, String description, Boolean save_default)
	{
		super(name, 0.0, description, save_default);
		init(0.0);
	}

	public State(String name, Boolean save_default)
	{
		super(name, 0.0, "", save_default);
		init(0.0);
	}

	public State(String name)
	{
		super(name, 0.0, "", true);
		init(0.0);
	}

	public State()
	{
		super("State", 0.0, "", true);
		init(0.0);
	}

	private void init(Double... vals)
	{
		elementDomain = new ValueDomain(vals[0]);
		if (vals.length > 1)
		{
			elementDomain.setRandomValues(vals[0], vals[1]);
		}
		element = elementDomain.getValue();
		derivative = null;
		prejump = element;
	}

	/*
	 * Store a copy of a value immediately before a jump occurs allowing
	 * pre=jump value access even if the value is changed by another component
	 */
	void storePreJumpValue()
	{
		prejump = element;
	}

}
