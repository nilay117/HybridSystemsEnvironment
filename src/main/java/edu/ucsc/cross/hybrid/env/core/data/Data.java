package edu.ucsc.cross.hybrid.env.core.data;

import java.util.HashMap;

import bs.commons.io.system.IO;
import bs.commons.objects.expansions.InitialValue;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import edu.ucsc.cross.hybrid.env.core.structure.ComponentClassification;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class Data<T> extends DynamicData<T>
{

	private boolean simulated;
	private final boolean cloneToStore;
	private HashMap<Double, T> savedValues;
	public boolean save;
	private InitialValue<T> initialVal;
	private T preJumpValue;
	private Unit defaultUnit;

	public InitialValue<T> getInitialVal()
	{
		return initialVal;
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
			IO.warn("attempted to get default unit of " + get().toString());
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			try
			{
				Data.setValueUnprotected(this, (T) initialVal.getRange().getValue());
			} catch (Exception ee)
			{
				ee.printStackTrace();
			}
		}
		//	}
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

	protected Data(T obj, ComponentClassification type, String name, String description, Boolean can_be_set,
	Boolean save_default)
	{
		super(obj, can_be_set, type, name, description);
		preJumpValue = (T) ObjectCloner.xmlClone(obj);
		simulated = true;
		save = save_default;
		savedValues = new HashMap<Double, T>();
		cloneToStore = !super.isChangeableElement(obj); //super.id().description().information.set(description);
		try
		{
			if (obj.getClass().getSuperclass().equals(UnitValue.class))
			{
				defaultUnit = (Unit) ObjectCloner.xmlClone(((UnitValue) get()).getUnit());
				try
				{
					initialVal = new InitialValue<T>((T) ((UnitValue) get()).get(((UnitValue) get()).getUnit()));
				} catch (UnitException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else

			{
				defaultUnit = null;
				initialVal = new InitialValue<T>(get());
			}
		} catch (Exception badClass)
		{
			badClass.printStackTrace();
			defaultUnit = null;
			initialVal = new InitialValue<T>(get());
		}
		// TODO Auto-generated constructor stub
	}

	protected static <S> Data<S> newData(S obj, ComponentClassification type, String name)
	{
		return newData(obj, type, name, type.storeDefault());
	}

	protected static <S> Data<S> newData(S obj, ComponentClassification type)
	{
		return newData(obj, type, null, type.storeDefault());
	}

	protected static <S> Data<S> newData(S obj, ComponentClassification type, String name, boolean save_default)
	{
		Data<S> newState = null;
		if (name == null)
		{
			newState = new Data<S>(obj, type, "Object", "", isChangeableElement(obj), save_default);
		} else
		{
			newState = new Data<S>(obj, type, name, "", isChangeableElement(obj), save_default);
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

	public static <T> ConditionalData getConditionBasedParameters(Data<?> state_value, T zero_element, String name)
	{
		ConditionalData stateBasedValue = new ConditionalData(state_value, zero_element);
		return stateBasedValue;
	}

	public static final <T> Data<T> getDynamicStateElement(T object, String name)
	{
		Data<T> newElement = null;

		if (object.getClass().equals(Double.class))
		{
			newElement = Data.getElement(object, true, ComponentClassification.DYNAMIC_STATE, name, "");
		} else
		{
			newElement = Data.getElement(object, false, ComponentClassification.DYNAMIC_STATE, name, "");
		}

		//	HybridSystemFactory.storeField(CallingClass.INSTANCE.getCallingClasses(), newElement);
		return newElement;
	}

	public static <T> Data<T> getDynamicStateElement(T object)
	{
		return Data.getDynamicStateElement(object, null);
	}

	public static final <T> Data<T> getDataElement(T object, String name)
	{
		Data<T> newElement = Data.getElement(object, Data.isChangeableElement(object), ComponentClassification.DATA,
		name, null);
		//System.out.println(XMLParser.serializeObject(CallingClass.INSTANCE.getCallingClasses()));
		//	HybridSystemFactory.storeField(CallingClass.INSTANCE.getCallingClasses(), newElement);
		return newElement;
	}

	public static final <T> Data<T> getDiscreteStateElement(T object, String name, boolean save)
	{
		Data<T> newElement = Data.getElement(object, Data.isChangeableElement(object),
		ComponentClassification.DISCRETE_STATE, name, "");
		Data.setStoreValues(newElement, save);
		return newElement;
	}

	public static final <T> Data<T> getDiscreteStateElement(T object, String name)
	{
		return Data.getDiscreteStateElement(object, name, false);
	}

	public static final <T> Data<T> getDiscreteStateElement(T object)
	{
		return Data.getDiscreteStateElement(object, null);
	}

	public static final <T> Data<T> getProperty(T object, String name, boolean save)
	{

		Data newElement = Data.getElement(object, true, ComponentClassification.PROPERTY, name, "");
		Data.setStoreValues(newElement, save);
		//HybridSystemFactory.storeField(CallingClass.INSTANCE.getCallingClasses(), newElement);
		return newElement;
	}

	public static final <T> Data<T> getProperty(T object, String name)
	{

		Data<T> newElement = Data.getElement(object, false, ComponentClassification.PROPERTY, name, "");
		//HybridSystemFactory.storeField(CallingClass.INSTANCE.getCallingClasses(), newElement);
		return newElement;
	}

	public static <T> Data<T> getProperty(T object)
	{
		return Data.getProperty(object, null);
	}

	public static <T> Data<T> getParameter(T object, String name, boolean save)
	{

		Data newElement = Data.getElement(object, true, ComponentClassification.PARAMETER, name, "");
		Data.setStoreValues(newElement, save);
		//HybridSystemFactory.storeField(CallingClass.INSTANCE.getCallingClasses(), newElement);
		return newElement;
	}

	public static final <T> Data<T> getParameter(T object, String name)
	{
		return getParameter(object, name, false);
	}

	public static <T> Data<T> getParameter(T object)
	{
		return Data.getParameter(object, null);
	}

	public static <S> void storeValue(Data<S> element, Double time)
	{
		element.storeValue(time);
	}

	protected static <T> Data<T> getElement(T obj, boolean can_be_set, ComponentClassification type, String name,
	String description)
	{
		//System.out.println(obj + " " + can_be_set + " " + type + " " + name);
		return new Data<T>(obj, type, name, description, can_be_set, type.storeDefault());
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

	//	private void determineDefaultUnit()
	//	{
	//		try
	//		{
	//			if (obj.getClass().getSuperclass().equals(UnitValue.class))
	//			{
	//				defaultUnit = (Unit) ObjectCloner.xmlClone(((UnitValue) get()).getUnit());
	//				try
	//				{
	//					initialVal = new InitialValue<T>((T) ((UnitValue) get()).get(((UnitValue) get()).getUnit()));
	//				} catch (UnitException e)
	//				{
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//			} else
	//
	//			{
	//				defaultUnit = null;
	//				initialVal = new InitialValue<T>(get());
	//			}
	//		} catch (Exception badClass)
	//		{
	//			defaultUnit = null;
	//			initialVal = new InitialValue<T>(get());
	//		}
	//	}
}
