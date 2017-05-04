package edu.ucsc.cross.hybrid.env.structural;

import bs.commons.io.system.IO;
import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.core.UnitValue;
import edu.ucsc.cross.hybrid.env.structural.BaseData;
import edu.ucsc.cross.hybrid.env.structural.ComponenDefinition;

public class DynamicData<T> extends ProtectedData<T>
{

	protected DynamicData(T element, ComponenDefinition type, String name, String description)
	{
		super(element, type, name, description);
		derivative = cloneZeroDerivative(element);
		// TODO Auto-generated constructor stub
	}

	private T derivative;

	public T getDt()
	{
		if (!super.getProperties().getClassification().equals(BaseData.DYNAMIC_STATE))
		{
			IO.warn("attempted to get derivative of " + derivative.toString() + " when it is not a dynamic variable");
		}
		return derivative;
	}

	public void setDt(T derivative)
	{
		if (super.getProperties().getClassification().equals(BaseData.DYNAMIC_STATE))
		{
			this.derivative = derivative;
		} else
		{
			IO.warn("attempted to set derivative of " + get().toString() + " when it is not a dynamic variable");
		}
	}

	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub

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

}
