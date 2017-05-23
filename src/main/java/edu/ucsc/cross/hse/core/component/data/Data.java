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
import edu.ucsc.cross.hse.core.component.categorization.CoreDataGroup;
import edu.ucsc.cross.hse.core.component.classification.DataType;
import edu.ucsc.cross.hse.core.component.constructors.Component;

/*
 * This class protects and manages a data object to ensure that the correct
 * value is always maintaned, and the pointer to it is always correct. It is
 * common to be accessing data from multiple different locations, thus it is
 * important for only one instance with the correct object to exist. This class
 * is also responsible for storing previous values of the object if specified by
 * the user
 */
@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class Data<T> extends Component// DynamicData<T>
{

	// Data Properties
	private boolean simulated; // flag indicating whether object is simulated
	private final boolean cloneToStore; // flag indicating if object needs to be
	public boolean save; // flag indicating if object should be stored
	private Unit defaultUnit; // default unit (if object has units)
	private DataType dataType; // classification of the data element, ie Hybrid
								// State or Property

	// Values
	protected T element;
	private T preJumpValue; // stored pre-jump value
	private HashMap<Double, T> savedValues; // mapping of saved values
	private InitialValue<T> initialVal; // initial value of object

	public InitialValue<T> getInitialVal()
	{
		return initialVal;
	}

	public void updateInitialVal(InitialValue<T> initial_val)
	{
		initialVal = initial_val;
		set(initialVal.getValue());
	}

	public boolean nullElement()
	{
		return (get() == null);
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

	public void initializeValue()
	{
		if (get().getClass().getSuperclass().equals(UnitValue.class))
		{
			try
			{
				((UnitValue) get()).set((Double) initialVal.getValue(), ((UnitValue) get()).getUnit());
			} catch (UnitException e)
			{
				e.printStackTrace();
			}
		} else
		{
			try
			{
				Data.setValueUnprotected(this, (T) initialVal.getRange().getValue());
			} catch (Exception ee)
			{
				Data.setValueUnprotected(this, (T) initialVal.getValue());
				// ee.printStackTrace();
			}
		}
		// }
	}

	private void storePreJumpValue()
	{
		preJumpValue = getStoreValue();
	}

	private void restorePreJumpValue()
	{
		element = preJumpValue;
	}

	public static <S> void restorePreJumpValue(Data<S> state)
	{
		state.restorePreJumpValue();
	}

	public static <S> void storePreJumpValue(Data<S> state)
	{
		state.storePreJumpValue();
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

	public static Double getDt(Data element)
	{
		return getDt(element, null);
	}

	public static Double getDt(Data element, Unit unit)
	{
		if (element.get().getClass().getSuperclass().equals(UnitValue.class))
		{
			if (unit == null)
			{
				unit = ((UnitValue) element.get()).getUnit();
			}
			try
			{
				return (Double) ((UnitValue) element.getDt()).get(unit);
			} catch (UnitException e)
			{
				e.printStackTrace();
				return null;

			}
		} else if (element.getDt().getClass().equals(Double.class))
		{
			return (Double) element.getDt();
		} else
		{
			return null;
		}
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

	protected Data(T obj, DataType type, String name, String description, Boolean save_default)
	{
		// super(obj, type, name, description);
		super(name, Data.class);
		element = obj;
		dataType = type;
		derivative = cloneZeroDerivative(element);
		this.element = obj;
		preJumpValue = (T) ObjectCloner.xmlClone(obj);
		simulated = true;
		save = save_default;
		savedValues = new HashMap<Double, T>();
		initialVal = new InitialValue<T>(get());
		cloneToStore = !isChangeableElement(obj); // super.id().description().information.set(description);
		try
		{
			if (obj.getClass().getSuperclass().equals(UnitValue.class))
			{
				defaultUnit = (Unit) ObjectCloner.xmlClone(((UnitValue) get()).getUnit());
				initialVal = new InitialValue<T>(get());
				// try
				// {
				// initialVal = new InitialValue<T>(get());
				// } catch (UnitException e)
				// {
				// // TODO Auto-generated catch block
				// // initialVal = new InitialValue<T>(get());
				// e.printStackTrace();
				// }
			} else

			{
				defaultUnit = null;
				// initialVal = new InitialValue<T>(get());
			}
		} catch (Exception badClass)
		{
			badClass.printStackTrace();
			defaultUnit = null;
			initialVal = new InitialValue<T>(get());
		}
		// TODO Auto-generated constructor stub
	}

	protected static <S> Data<S> instantiateObj(S obj, DataType type, String name, String description,
	Boolean can_be_set, Boolean save_default)
	{
		Data<S> newObj = new Data<S>(obj, type, name, description, save_default);// type,
																					// name,
																					// description);
		return newObj;
		// TODO Auto-generated constructor stub
	}

	protected static <S> Data<S> newObj(S obj, DataType type, String name)
	{
		return newObj(obj, type, name, type.storePreviousDataByDefault());
	}

	protected static <S> Data<S> newObj(S obj, DataType type)
	{
		return newObj(obj, type, null, type.storePreviousDataByDefault());
	}

	protected static <S> Data<S> newObj(S obj, DataType type, String name, boolean save_default)
	{
		Data<S> newState = null;
		if (name == null)
		{
			newState = new Data<S>(obj, type, "Object", "", save_default);
		} else
		{
			newState = new Data<S>(obj, type, name, "", save_default);
		}

		return newState;

	}

	private void storeValue(Double time)
	{
		if (save)
		{
			T storeValue = getStoreValue();
			savedValues.put(time, storeValue);
		}
	}

	private void storeValue(Double time, boolean override_save)
	{
		if (save || override_save)
		{
			T storeValue = getStoreValue();
			savedValues.put(time, storeValue);
		}
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

	public static <S> void storeValue(Data<S> element, Double time, boolean override_save)
	{
		element.storeValue(time, override_save);
	}

	public static <S> void storeValue(Data<S> element, Double time)
	{
		element.storeValue(time);
	}

	protected static <T> Data<T> getElement(T obj, boolean can_be_set, DataType type, String name, String description)
	{
		// System.out.println(obj + " " + can_be_set + " " + type + " " + name);
		return new Data<T>(obj, type, name, description, type.storePreviousDataByDefault());
	}

	protected static <T> void setValueUnprotected(Data<T> obj, T new_val)
	{
		obj.element = new_val;
	}

	public static <S> void setStoreValues(Data<S> element, boolean store)
	{
		element.save = store;
	}

	public static <S> boolean isSimulated(Data<S> element)
	{
		return element.simulated;
	}

	public static <S> void isSimulated(Data<S> element, boolean simulated)
	{
		element.simulated = simulated;
	}

	public static <S> boolean isStored(Data<S> element)
	{
		return element.save;
	}

	public static <S> HashMap<Double, S> getStoredValues(Data<S> element)
	{
		return element.savedValues;
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

	private T derivative;

	public T getDt()
	{
		if (!CoreDataGroup.HYBRID_STATE_ELEMENTS.contains(this))// (CoreData.DYNAMIC_STATE))
		{
			// IO.warn("attempted to get derivative of " + derivative.toString()
			// + " when it is not a dynamic variable");
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

	public T get()
	{
		return element;
	}

	public void set(T element)
	{
		this.element = element;
		if (element != null)
		{
			if (!(get().getClass().getSuperclass().equals(UnitValue.class)
			&& element.getClass().getSuperclass().equals(UnitValue.class)))
			{
				this.element = element;
			} else if (element != null)
			{

				if (get().getClass().getSuperclass().equals(UnitValue.class)
				&& element.getClass().getSuperclass().equals(UnitValue.class))
				{
					try
					{
						UnitValue currentVal = (UnitValue) get();
						UnitValue newVal = (UnitValue) element;
						currentVal.set(newVal.get(newVal.getUnit()), newVal.getUnit());
					} catch (UnitException ue)
					{
						ue.printStackTrace();
					}
				}

			}
		}
	}

	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub

	}

	public static <T> boolean isChangeableElement(T object)
	{
		if (object != null)
		{
			try
			{
				if (Data.changableClasses.contains(object.getClass()))
				{
					return true;
				} else if (object.getClass().isEnum())
				{
					return true;
				} else
				{
					return false;
				}

			} catch (Exception nullpt)
			{
			}
		}
		return true;
	}

	public DataType getDataClass()
	{
		return dataType;
	}

	static public final ArrayList<Class> changableClasses = new ArrayList<Class>(Arrays.asList(new Class[]
	{ Double.class, String.class, Integer.class, Long.class, Number.class, Boolean.class, Enum.class }));

}