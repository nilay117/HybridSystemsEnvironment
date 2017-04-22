package edu.ucsc.cross.hybrid.env.core.data;

import bs.commons.io.system.IO;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import edu.ucsc.cross.hybrid.env.core.structure.Component;
import edu.ucsc.cross.hybrid.env.core.structure.ComponentClassification;

public abstract class ProtectedData<T> extends Component
{

	protected boolean settable;
	protected T element;

	protected ProtectedData(T element, ComponentClassification type, boolean settable, String name, String description)
	{
		super(name, type);
		this.element = element;
		this.settable = settable;
	}

	public T get()
	{
		return element;
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void set(T element)
	{
		if (settable)
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

		} else
		{
			new Exception().printStackTrace();
			IO.warn("attempted to set " + element + " when value setting is disabled");
		}
	}

	protected void setOverride(T element)
	{
		this.element = element;
	}

	public static <S> void setSettable(ProtectedData<S> element, boolean set)
	{
		element.settable = set;
	}

	public static <S> void setValOverride(ProtectedData<S> element, S value)
	{
		element.setOverride(value);
	}
}
