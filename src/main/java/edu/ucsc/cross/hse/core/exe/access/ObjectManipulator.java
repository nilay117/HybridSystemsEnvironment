package edu.ucsc.cross.hse.core.exe.access;

import bs.commons.objects.access.FieldFinder;
import com.jcabi.aspects.Loggable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

@Loggable(Loggable.TRACE)
public class ObjectManipulator
{

	public Object getParent()
	{
		return parent;
	}

	private Field field;

	public Field getField()
	{
		return field;
	}

	private Object parent;
	private Object change;
	boolean simulated;

	public ObjectManipulator(Field field, Object parent, Object change)
	{
		this.field = field;
		this.change = change;
		this.parent = parent;
	}

	public void updateObject(Object update)
	{
		try
		{
			field.set(parent, update);
		} catch (IllegalArgumentException | IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateObject(Object object, Object alternate_parent)
	{
		try
		{
			field.set(alternate_parent, object);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void storeJumpValue(boolean pre_jump)
	{
		try
		{
			if (pre_jump)
			{
				updateObject(field.get(parent), change);
			} else
			{
				updateObject(field.get(change), parent);
			}
		} catch (IllegalArgumentException | IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Object getObject()
	{
		try
		{
			return field.get(parent);
		} catch (IllegalArgumentException | IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0.0;
		}

	}

	public Object getChangeParent()
	{
		return change;
	}

	public Object getObject(Object alternate)
	{
		try
		{
			return field.get(alternate);
		} catch (IllegalArgumentException | IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return null;
	}

	public Object getChange()
	{
		return getObject(change);
	}

	public static void updateValues(Object current_state, Object new_state)
	{
		for (Field field : StateFieldMapper.getClassFields(new_state.getClass()))
		{
			if (field.isAccessible())
			{
				try
				{
					if (StateFieldMapper.isState(field.getType()))
					{
						updateValues(field.get(current_state), field.get(new_state));
					} else // if (field.get(new_state) != null)
					{
						field.set(current_state, field.get(new_state));
					}
				} catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public static boolean validValue(ObjectManipulator manipulator, Object update)
	{
		boolean valid = true;
		if (manipulator.field.getType().equals(double.class) || manipulator.field.getType().equals(Double.class))
		{
			try
			{
				Double val = (Double) update;
				valid = valid && (!Double.isNaN(val) && Double.isFinite(val));
				if (!valid)
				{

					throw new Exception("Invalid number entered for field " + manipulator.field.getName() + " of state "
					+ manipulator.parent.getClass().getSimpleName() + " : " + val);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				System.exit(1);
			}
		}
		return valid;
	}
}
