package edu.ucsc.cross.hybrid.env.core.data;

import java.util.ArrayList;
import java.util.Arrays;

import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import edu.ucsc.cross.hybrid.env.core.classifications.ComponentClass;
import edu.ucsc.cross.hybrid.env.core.elements.Component;
import edu.ucsc.cross.hybrid.env.core.elements.Data;

@SuppressWarnings(
{ "unchecked", "rawtypes" })
public abstract class ProtectedData<T> extends Component
{

	protected T element;

	protected ProtectedData(T element, ComponentClass type, String label, String name)
	{
		super(label, type);
		this.element = element;

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

	static public final ArrayList<Class> changableClasses = new ArrayList<Class>(Arrays.asList(new Class[]
	{ Double.class, String.class, Integer.class, Long.class, Number.class, Boolean.class, Enum.class }));

	//	@Override

}
