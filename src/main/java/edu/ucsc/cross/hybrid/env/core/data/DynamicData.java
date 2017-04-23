package edu.ucsc.cross.hybrid.env.core.data;

import java.util.ArrayList;
import java.util.Arrays;

import bs.commons.io.system.IO;
import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.core.UnitValue;
import edu.ucsc.cross.hybrid.env.core.components.Data;
import edu.ucsc.cross.hybrid.env.core.structure.ComponentClassification;

public class DynamicData<T> extends ProtectedData<T>
{

	private T derivative;
	//	private T preJumpValue;

	public static <S> DynamicData<S> systemState(S obj, ComponentClassification type, String name, String description)
	{
		DynamicData<S> newState = new DynamicData<S>(obj, isChangeableElement(obj), type, name, description);
		return newState;

	}

	protected DynamicData(T obj, boolean can_be_set, ComponentClassification type, String name, String description)
	{

		super(obj, type, can_be_set, name, description);
		derivative = cloneZeroDerivative(obj);

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

	public T getDt()
	{
		if (!super.getProperties().getClassification().equals(ComponentClassification.DYNAMIC_STATE))
		{
			IO.warn("attempted to get derivative of " + derivative.toString() + " when it is not a dynamic variable");
		}
		return derivative;
	}

	public void setDt(T derivative)
	{
		if (super.getProperties().getClassification().equals(ComponentClassification.DYNAMIC_STATE))
		{
			if (super.settable)
			{
				this.derivative = derivative;
			} else
			{
				IO.warn("attempted to set derivative object " + derivative.toString() + " when setting is disabled");
			}
		} else
		{
			IO.warn("attempted to set derivative of " + get().toString() + " when it is not a dynamic variable");
		}
	}

	//	public static <S> void setId(SystemState<S> state, ObjectId<SystemState<S>> object_id)
	//	{
	//		state.id = object_id;
	//	}

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

	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("rawtypes")
	static public final ArrayList<Class> changableClasses = new ArrayList<Class>(Arrays.asList(new Class[]
	{ Double.class, String.class, Integer.class, Long.class, Number.class, Boolean.class, Enum.class }));

	//	@Override
	//	public Component getComponent()
	//	{
	//		// TODO Auto-generated method stub
	//		return this;
	//	}

	/// Moved to Data 
	//	
	//	public InitialValue<T> getInitialVal()
	//	{
	//		return initialVal;
	//	}
	//
	//	public Unit defaultUnit()
	//	{
	//		if (defaultUnit == null)
	//		{
	//			IO.warn("attempted to get default unit of " + get().toString());
	//		}
	//		return defaultUnit;
	//	}
	//
	//	public void initializeValue()
	//	{
	//		//		if (ElementFactory.isChangeableElement(object))
	//		//		{
	//		if (get().getClass().getSuperclass().equals(UnitValue.class))
	//		{
	//			try
	//			{
	//				((UnitValue) get()).set((Double) initialVal.getValue(), ((UnitValue) get()).getUnit());
	//			} catch (UnitException e)
	//			{
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//		} else
	//		{
	//			set(initialVal.getValue());
	//		}
	//		//	}
	//	}

	//	public static Double getVal(DynamicData element, boolean defaultUnits)
	//	{
	//		try
	//		{
	//			if (element.get().getClass().getSuperclass().equals(UnitValue.class))
	//			{
	//				Unit unit = element.defaultUnit();
	//				if (!defaultUnits)
	//				{
	//					unit = ((UnitValue) element.get()).getUnit();
	//				}
	//				try
	//				{
	//					return (Double) ((UnitValue) element.get()).get(element.defaultUnit());
	//				} catch (UnitException e)
	//				{
	//					e.printStackTrace();
	//					return null;
	//					// TODO Auto-generated catch block
	//
	//				}
	//			} else if (element.get().getClass().equals(Double.class))
	//			{
	//				return (Double) element.get();
	//			} else
	//			{
	//				return null;
	//			}
	//		} catch (Exception nullVal)
	//		{
	//			return null;
	//		}
	//	}

	//	public static Double getDt(Data element, boolean defaultUnits)
	//	{
	//		if (element.get().getClass().getSuperclass().equals(UnitValue.class))
	//		{
	//			Unit unit = element.defaultUnit();
	//			if (!defaultUnits)
	//			{
	//				unit = ((UnitValue) element.getDt()).getUnit();
	//			}
	//			try
	//			{
	//				return (Double) ((UnitValue) element.getDt()).get(element.defaultUnit());
	//			} catch (UnitException e)
	//			{
	//				e.printStackTrace();
	//				return null;
	//
	//			}
	//		} else if (element.getDt().getClass().equals(Double.class))
	//		{
	//			return (Double) element.getDt();
	//		} else
	//		{
	//			return null;
	//		}
	//	}
}