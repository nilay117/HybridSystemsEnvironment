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

/*
 * This class is very general description of a state variable, and is used to
 * define all state variables within the environment. It also stores the
 * derivative of the state variable to keep the user and processor separated for
 * convenience. As an extension of the data class, it provides protection by
 * storing the value internally so every component will always access the true
 * value (ensures correct pointers) including during jumps, where the value has
 * been changed before some other system has even applied dynamics. To be more
 * specific, no system has priority if more than one jump occurs at the same
 * time, meaning it would be possible to use the incorrect value without knowing
 * it. This class stores a copy of the value right before every jump occurs,
 * thus ensuing that every system is accessing the correct value regardless if
 * it has been changed by another system. The value is represented as a double
 * since it can also be used as an integer, bit, or other values. Note:
 * Non-numeric states can be defined using the data class
 */
public class State extends Data<Double>
{

	private Double derivative; // current derivative of the data (if the data
								// changes
								// continuously)

	protected ValueDomain elementDomain; // domain that allows for the state to
											// be initialized or reset to a
											// value within a range

	private Double prejump; // pre-jump value stored immediately before jump
							// occurs

	/*
	 * Gets the current state value
	 */
	@Override
	public Double getValue()
	{
		return getValue(false);
	}

	/*
	 * Gets the state value after being randomized within a specified domain if
	 * desired. For example a random transmission time in an unreliable network.
	 */
	@Override
	public Double getValue(boolean randomize_from_domain)
	{
		try
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
		} catch (Exception e)
		{
			return element;
		}

	}

	/*
	 * Sets the state value to a fixed number
	 */
	@Override
	public void setValue(Double val)
	{
		if (!ComponentOperator.getOperator(this).isInitialized())
		{
			elementDomain.setFixedValue(val);
		}
		element = val;

	}

	/*
	 * Sets the state value to a random number within the specified range
	 */
	@Override
	public void setValue(Double min, Double max)
	{
		elementDomain.setRandomValues(min, max);
		element = elementDomain.getValue();
	}

	/*
	 * Gets the current derivative of the state value, which will be null if thw
	 * object is not in the flow set
	 */
	public Double getDerivative()
	{
		return derivative;
	}

	/*
	 * Sets the derivative of the state value
	 */
	public void setDerivative(Double derivative)
	{
		this.derivative = derivative;
	}

	/*
	 * Acesses all of the built in funcions
	 */
	@Override
	public DataWorker<Double> getActions()
	{
		return DataWorker.getConfigurer(this);
	}

	public State(String name, Double obj)
	{
		super(name, obj, true);
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
