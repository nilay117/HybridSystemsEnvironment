package edu.ucsc.cross.hse.core.component.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.expansions.InitialValue;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import bs.commons.unitvars.units.NoUnit;
import edu.ucsc.cross.hse.core.component.categorization.CoreDataGroup;
import edu.ucsc.cross.hse.core.component.classification.DataType;
import edu.ucsc.cross.hse.core.component.foundation.Component;
import edu.ucsc.cross.hse.core.procesing.output.SystemConsole;

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
	protected boolean simulated; // flag indicating whether object is simulated
	protected T element;
	private UnitValue prejump;
	// Access Functions

	public T getz()
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

	public T get()
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

	public Unit defaultUnit()
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

	public static Double getNumberValue(Data element)
	{
		return getNumberValue(element, null);
	}

	public static Double getNumberValue(Data element, Unit unit)
	{
		try
		{
			if (element.get().getClass().getSuperclass().equals(UnitValue.class))
			{
				if (unit == null)
				{
					unit = ((UnitValue) element.get()).getUnit();
				}
				try
				{
					return (Double) ((UnitValue) element.get()).get(unit);
				} catch (UnitException e)
				{
					e.printStackTrace();
					return null;
					// TODO Auto-generated catch block

				}
			} else if (element.get().getClass().equals(Double.class))
			{
				return (Double) element.get();
			} else
			{
				return null;
			}
		} catch (Exception nullVal)
		{
			return null;
		}
	}

	public static <S> Number getStoredNumberValue(Data<S> element, Double time)
	{
		Number value = null;
		try
		{

			value = (Number) element.savedValues.get(time);
		} catch (Exception notDouble)
		{
			try
			{
				UnitValue unitVal = (UnitValue) element.savedValues.get(time);
				value = (Double) unitVal.get(unitVal.getUnit());
			} catch (Exception notNumber)
			{

			}
		}
		return value;
	}

	public static <S> S getStoredValue(Data<S> element, Double time)
	{
		S value = null;
		try
		{
			value = element.savedValues.get(time);
		} catch (Exception noValue)
		{

		}
		return value;
	}

	public static <S> HashMap<Double, S> getStoredValues(Data<S> element)
	{
		return element.savedValues;
	}

	// Configuration Functions

	public static <S> void setInitialVal(Data<S> data, InitialValue<S> initial_val)
	{
		data.initialVal = initial_val;
		data.set(data.initialVal.getValue());
	}

	public static <S> void setStorePreviousValues(Data<S> element, boolean store)
	{
		element.save = store;
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////
	// Internal Operation Functions : everything below for the processing system
	// only, it is not recommended to call these elsewhere unless trying to
	// alter the standard behavior
	////////////////////////////////////////////////////////////////////////////////////////////////////

	static public final ArrayList<Class> changableClasses = new ArrayList<Class>(Arrays.asList(new Class[]
	{ Double.class, String.class, Integer.class, Long.class, Number.class, Boolean.class, Enum.class }));

	protected Data(T obj, DataType type, String name, String description, Boolean save_default)
	{
		// super(obj, type, name, description);
		super(name, Data.class);
		defaultUnit = NoUnit.NONE;

		element = obj;
		dataType = type;
		derivative = cloneZeroDerivative(element);
		this.element = obj;
		preJumpValue = (T) ObjectCloner.xmlClone(obj);
		simulated = true;
		save = save_default;
		savedValues = new HashMap<Double, T>();
		initialVal = new InitialValue<T>(obj);
		cloneToStore = !isCopyRequiredOnSave(obj); // super.id().description().information.set(description);
		try
		{
			if (obj.getClass().getSuperclass().equals(UnitValue.class))
			{
				defaultUnit = (Unit) ObjectCloner.xmlClone(((UnitValue) obj).getUnit());
				// initialVal = new InitialValue<T>(get());

				initialVal = new InitialValue<T>((T) ((UnitValue) obj).get(((UnitValue) obj).getUnit()));
			} else

			{
				// defaultUnit = null;
				initialVal = new InitialValue<T>(obj);
			}
		} catch (Exception badClass)
		{
			badClass.printStackTrace();
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
		if (!Component.isInitialized(this))
		{
			if (FieldFinder.containsSuper(get(), UnitValue.class))
			{
				try
				{
					if (initialVal.getValue().getClass().equals(UnitValue.class))
					{
						((UnitValue) get()).set(((UnitValue) initialVal.getValue()).get(defaultUnit), defaultUnit);
					} else
					{
						((UnitValue) get()).set(initialVal.getValue(), defaultUnit);
					}
				} catch (UnitException badUnitOrValue)
				{
					set(initialVal.getValue());
					badUnitOrValue.printStackTrace();
				}
			} else
			{
				try
				{
					set((T) initialVal.getRange().getValue());
				} catch (Exception ee)
				{
					initialVal = new InitialValue<T>(get());
				}
			}
			Component.setInitialized(this, true);
		}
	}

	public static <T> boolean isCopyRequiredOnSave(T object)
	{
		if (object != null)
		{
			return changableClasses.contains(object);
		}
		return true;
	}

	public static <S> void restorePreJumpValue(Data<S> state)
	{
		state.restorePreJumpValue();
	}

	public static <S> void storePreJumpValue(Data<S> state)
	{
		state.storePreJumpValue();
	}

	public static <S> void storeValue(Data<S> element, Double time)
	{
		element.storeValue(time);
	}

	public static <S> Data<S> instantiateData(S obj, DataType type, String name, String description,
	Boolean save_default)
	{
		Data<S> newData = new Data<S>(obj, type, name, description, save_default);// type,
																					// name,
																					// description);
		return newData;
		// TODO Auto-generated constructor stub
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
				return (T) ObjectCloner.xmlClone(get());
			} else
			{
				return get();
			}
		}

	}

	private void restorePreJumpValue()
	{
		element = preJumpValue;
	}

	private void storePreJumpValue()
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
		preJumpValue = getStoreValue();
	}

	private void storeValue(Double time)
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

	public static <S> void storeValue(Data<S> element, Double time, boolean override_save)
	{
		element.storeValue(time, override_save);
	}

	public static <S> boolean isSimulated(Data<S> element)
	{
		return element.simulated;
	}

	public static <S> void setSimulated(Data<S> element, boolean simulated)
	{
		element.simulated = simulated;
	}

	public static <S> boolean isPreviousDataStored(Data<S> element)
	{
		return element.save;
	}

	public static <S> void setStoredValues(Data<S> data, HashMap<Double, S> vals)
	{
		data.savedValues = vals;
	}
}
