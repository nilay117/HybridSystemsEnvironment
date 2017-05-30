package edu.ucsc.cross.hse.core.framework.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import bs.commons.unitvars.units.NoUnit;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.procesing.output.SystemConsole;
import edu.ucsc.cross.hse.core.procesing.utils.CloneUtil;

/*
 * This class protects and manages a data object to ensure that the correct
 * value is always maintaned, and the pointer to it is always correct. It is
 * common to be accessing data from multiple different locations, thus it is
 * important for only one instance with the correct object to exist. This class
 * is also responsible for storing previous values of the object if specified by
 * the user.
 */
@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class Data<T> extends Component// DynamicData<T>
{

	public boolean save; // flag indicating if object should be stored
	private final boolean cloneToStore; // flag indicating if object needs to be
	private DataType dataType; // classification of the data element, ie Hybrid
								// State or Property
	private Unit defaultUnit; // default unit (if object has units)
	private T derivative;

	private InitialValue<T> initialVal; // initial value of object
	private T preJumpValue; // stored pre-jump value
	public HashMap<Double, T> savedValues; // mapping of saved values
	// Data Properties

	protected T element;
	private UnitValue prejump;
	// Access Functions

	public T get()
	{

		try
		{

			if (getEnvironment().isJumpOccurring())
			{

				if (defaultUnit.equals(NoUnit.NONE))
				{
					if (element.equals(prejump.get(NoUnit.NONE)))
					{
						return (T) prejump.get(NoUnit.NONE);
					} else
					{
						return element;
					}
				} else
				{
					UnitValue val = (UnitValue) element;
					if (val.get(val.getUnit()).equals(((UnitValue) element).get(val.getUnit())))
					{
						return element;
					} else
					{
						T oldValue = (T) ObjectCloner.xmlClone(element);
						UnitValue oldVal = (UnitValue) oldValue;
						oldVal.set(prejump.get(prejump.getUnit()), prejump.getUnit());
						return oldValue;
					}
				}

			} else
			{
				// if (1 > 0)
				// {
				return element;
			}
		} catch (Exception e)
		{
			// System.out.println(this.getHierarchy().getParentComponent().toString());
			// e.printStackTrace();
			return element;
		}
		// } else
		// {
		// return element;
		// }
	}

	public T getz()
	{

		// System.out.println(this.getHierarchy().getParentComponent().toString());
		// e.printStackTrace();
		return element;

		// } else
		// {
		// return element;
		// }
	}

	public void set(T element)
	{
		this.element = element;
		// if (element != null)
		// {
		// if (FieldFinder.containsSuper(get(), UnitValue.class) &&
		// FieldFinder.containsSuper(get(), UnitValue.class))
		// {
		// try
		// {
		// UnitValue currentVal = (UnitValue) get();
		// UnitValue newVal = (UnitValue) element;
		// currentVal.set(newVal.get(newVal.getUnit()), newVal.getUnit());
		// } catch (UnitException ue)
		// {
		// ue.printStackTrace();
		// }
		//
		// } else
		// {
		// try
		// {
		// if (this.element.getClass().equals(element.getClass()))
		// {
		// this.element = element;
		// }
		// } catch (Exception ue)
		// {
		// ue.printStackTrace();
		// }
		// }
		// }
	}

	public T getDt()
	{
		if (!CoreDataGroup.HYBRID_STATE_ELEMENTS.contains(this))// (CoreData.DYNAMIC_STATE))
		{
			SystemConsole
			.print("attempted to get derivative of " + derivative.toString() + " when it is not a dynamic variable");
		}
		return derivative;
	}

	public void setDt(T derivative)
	{
		if (CoreDataGroup.HYBRID_STATE_ELEMENTS.contains(this))
		{
			this.derivative = derivative;
		} else
		{
			// IO.warn("attempted to set derivative of " + get().toString() + "
			// when it is not a dynamic variable");
		}
	}

	public T getPrejumpValue()
	{
		return preJumpValue;
	}

	public Unit getDefaultUnit()
	{
		if (defaultUnit == null)
		{
			// IO.warn("attempted to get default unit of " + get().toString());
		}
		return defaultUnit;
	}

	public DataType getDataClass()
	{
		return dataType;
	}

	public boolean isElementNull()
	{
		return (get() == null);
	}

	public InitialValue<T> getInitialVal()
	{
		return initialVal;
	}

	// Stored Data Access Functions
	// ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	// Stored Data Access Functions: these functions are protected instead of
	// public because they access previosly stored data, which may or may not be
	// allowed depending on the situation. If a vehicle is traveling around
	// without a storage device or any means to record data, it would not have
	// access to past states and information. If it were to have a storage
	// device recording the states then a connector model can be implemented
	// ß to allow the system to access the data
	// ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••

	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Internal Operation Functions : everything below for the processing system
	// only, it is not recommended to call these elsewhere unless trying to
	// alter the standard behavior
	////////////////////////////////////////////////////////////////////////////////////////////////////

	public Double getDoubleValue()
	{
		return getDoubleValue(null);
	}

	public Double getDoubleValue(Unit unit)
	{
		try
		{
			if (get().getClass().getSuperclass().equals(UnitValue.class))
			{
				if (unit == null)
				{
					unit = ((UnitValue) get()).getUnit();
				}
				try
				{
					return (Double) ((UnitValue) get()).get(unit);
				} catch (UnitException e)
				{
					e.printStackTrace();
					return null;
					// TODO Auto-generated catch block

				}
			} else if (get().getClass().equals(Double.class))
			{
				return (Double) get();
			} else
			{
				return null;
			}
		} catch (Exception nullVal)
		{
			return null;
		}
	}

	public Double getStoredDoubleValue(Double time)
	{
		Double value = null;
		try
		{

			value = (Double) savedValues.get(time);
		} catch (Exception notDouble)
		{
			try
			{
				UnitValue unitVal = (UnitValue) savedValues.get(time);
				value = (Double) unitVal.get(unitVal.getUnit());
			} catch (Exception notNumber)
			{

			}
		}
		return value;
	}

	public Double getStoredDoubleValue(Double time, Unit unit)
	{
		Double value = null;
		try
		{

			value = (Double) savedValues.get(time);
		} catch (Exception notDouble)
		{
			try
			{
				UnitValue unitVal = (UnitValue) savedValues.get(time);
				if (unit == null)
				{
					value = (Double) unitVal.get(unitVal.getUnit());
				} else
				{
					value = (Double) unitVal.get(unit);
				}
			} catch (Exception notNumber)
			{

			}
		}
		return value;
	}

	public T getStoredValue(Double time)
	{
		T value = null;
		try
		{
			value = savedValues.get(time);
		} catch (Exception noValue)
		{

		}
		return value;
	}

	public HashMap<Double, T> getStoredValues()
	{
		return savedValues;
	}

	public void setStorePreviousValues(boolean store)
	{
		save = store;
	}
	// Internal Operation Functions
	// ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	// The functions below are designed to be used by the processor in the
	// background, there is no need to use any of these functions for normal
	// operation. Using these is not recommended, any changes could disrupt the
	// whole flow, but I'm guessing you are curious / adventerous like me so be
	// my gues't if you'd like to work on some modifications or new features.
	// only, it is not recommended to call these elsewhere unless trying to
	// alter the standard behavior
	// ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••

	/*
	 * Constructor with almost all inputs possible. The important ones are obj,
	 * type, and name, which are the default/initial value, dataType, and the
	 * element name, respectively. description is optional and can be added
	 * whenever more detail is required, the save_default flag can be specified
	 * if desired, otherwise it will assume the default defined by the unit type
	 */
	protected Data(T obj, DataType type, String name, String description, Boolean save_default)
	{
		super(name, Data.class);

		element = obj;
		dataType = type;
		derivative = cloneZeroDerivative(element);
		this.element = obj;
		preJumpValue = (T) ObjectCloner.xmlClone(obj);
		simulated = true;
		save = save_default;
		defaultUnit = NoUnit.NONE;
		savedValues = new HashMap<Double, T>();
		initialVal = new InitialValue<T>(obj);
		cloneToStore = !isCopyRequiredOnSave(obj); // super.id().description().information.set(description);
		try
		{
			System.out.println(obj.getClass().getSuperclass());
			if (obj.getClass().getSuperclass().equals(UnitValue.class))
			{
				UnitValue val = ((UnitValue) obj);
				defaultUnit = (Unit) ((UnitValue) obj).getUnit();
				initialVal = new InitialValue<T>(obj);
				initialVal.setFixedValue((Double) val.get(defaultUnit), defaultUnit);

				// initialVal = new InitialValue<T>((T) ((UnitValue)
				// obj).get(((UnitValue) obj).getUnit()));
			} else

			{
				// defaultUnit = null;
				initialVal = new InitialValue<T>(obj);
			}
		} catch (Exception badClass)
		{
			badClass.printStackTrace();
			defaultUnit = NoUnit.NONE;
			// defaultUnit = null;
			initialVal = new InitialValue<T>(obj);
		}
		try
		{
			prejump = new UnitValue(obj, defaultUnit);
		} catch (UnitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize()
	{
		set(initialVal.getValue());
		// try
		// {
		// if (initialVal.getValue().getClass().equals(UnitValue.class))
		// {
		// ((UnitValue) get()).set(((UnitValue)
		// initialVal.getValue()).get(defaultUnit), defaultUnit);
		// } else
		// {
		// ((UnitValue) get()).set(initialVal.getValue(), defaultUnit);
		// }
		// } catch (UnitException badUnitOrValue)
		// {
		// set(initialVal.getValue());
		// badUnitOrValue.printStackTrace();
		// }
		// // else
		// {
		// try
		// {
		// set(initialVal.getValue());
		// } catch (Exception ee)
		// {
		// initialVal = new InitialValue<T>(get());
		// }
		// }
	}

	/*
	 * This list and changeable classes function determine what elements will
	 * need to be cloned before being saved to ensure the right values are
	 * stored. For example, a list would need to be copied because a list of
	 * some sort that was saved without being copied will reflect any changes
	 * made in the future unless the list is reinitialized. A value such as a
	 * double can be saved without being copied because the stored pointer
	 * points to the correct saved value even when a new value is stored. This
	 * list below contains general classes that not need to be cloned to save
	 */
	static public final ArrayList<Class> changableClasses = new ArrayList<Class>(Arrays.asList(new Class[]
	{ Double.class, String.class, Integer.class, Long.class, Number.class, Boolean.class, Enum.class }));

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

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private T cloneZeroDerivative(T obj)
	{
		T derivative;
		if (FieldFinder.containsSuper(obj, Number.class))
		{
			return (T) (Double) 0.0;
		} else if (FieldFinder.containsSuper(obj, UnitValue.class))
		{
			derivative = (T) ObjectCloner.xmlClone(obj);
			try
			{
				((UnitValue) derivative).set(0.0, ((UnitValue) derivative).getUnit());
			} catch (Exception badUnit)
			{
				badUnit.printStackTrace();
				System.err.println("dynamic variable creation failed : non-numeric classes not allowed");
				System.exit(1);
			}
		} else
		{
			derivative = null;
		}
		return derivative;
	}

	private T getStoreValue()
	{
		{
			if (cloneToStore)
			{
				return CloneUtil.cloner.deepClone(get());
				// return (T) ObjectCloner.xmlClone(get());
			} else
			{
				return get();
			}
		}

	}

	protected void restorePreJumpValue()
	{
		element = preJumpValue;
	}

	/*
	 * Store a copy of a value immediately before a jump occurs allowing
	 * pre=jump value access even if the value is changed by another component
	 */
	protected void storePreJumpValue()
	{
		try
		{
			if (defaultUnit != NoUnit.NONE)
			{
				UnitValue val = (UnitValue) element;
				Unit units = val.getUnit();

				prejump.set(val.get(units), units);
			} else
			{
				prejump.set(get(), NoUnit.NONE);
			}
		} catch (Exception e)
		{

		}
		// preJumpValue = getStoreValue();
	}

	protected void storeValue(Double time)
	{
		if (save)
		{
			T storeValue = getStoreValue();
			savedValues.put(time, storeValue);
		}
	}

	protected void storeValue(Double time, boolean override_save)
	{
		if (save || override_save)
		{
			T storeValue = getStoreValue();
			savedValues.put(time, storeValue);
		}
	}

}