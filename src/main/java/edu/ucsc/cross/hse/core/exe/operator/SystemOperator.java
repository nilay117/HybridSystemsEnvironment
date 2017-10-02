package edu.ucsc.cross.hse.core.exe.operator;

import bs.commons.objects.access.FieldFinder;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.access.ObjectManipulator;
import edu.ucsc.cross.hse.core.exe.access.StateFieldMapper;
import edu.ucsc.cross.hse.core.obj.structure.HybridSystem;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

@Loggable(Loggable.DEBUG)
public class SystemOperator
{

	private EnvironmentManager content;

	public SystemOperator(EnvironmentManager content)
	{
		this.content = content;
	}

	@Loggable(Loggable.TRACE)
	public boolean checkDomain(boolean jump)
	{
		boolean domain = false;
		for (HybridSystem<?> hs : content.getContents().getSystems())
		{
			if (jump)
			{
				domain = domain || hs.jumpSet();
			} else
			{
				domain = domain || hs.flowSet();
			}
		}
		return domain;
	}

	public void applyDynamics(boolean jump_occurring)
	{
		boolean jump = checkDomain(true);
		if (!jump)
		{
			storeChangeValuez(null);
		} else
		{
			storeChangeValuez(true);
		}
		for (HybridSystem<?> hs : content.getContents().getSystems())
		{
			// ObjectManipulator.updateValues(, hs.getState());
			if (jump && jump_occurring)
			{
				hs.G();
			} else
			{
				// clearChangeValuez();
				// SystemOperator.clearChangeValues(HybridSystem.getDynamicState(hs));
				{
					hs.F();
				}
			}
		}
		processDynamics(jump_occurring);
	}

	public void processDynamics(boolean jump)
	{
		if (jump)
		{
			storeChangeValuez(false);
			// for (HybridSystem<?> hs : content.getContents().getSystems())
			// {
			// if (hs.jumpSet())
			// {
			// SystemOperator.storeJumpValues(hs.getState(), HybridSystem.getDynamicState(hs), true);
			// }
			// }
		}
	}

	@Loggable(Loggable.TRACE)
	public static void storeChangeValues(Object state_class, Object change, boolean discrete)
	{
		for (Field field : StateFieldMapper.elements.get(state_class.getClass()))
		{
			try
			{
				if (StateFieldMapper.isState(field.getType()))
				{
					storeChangeValues(field.get(state_class), field.get(change), discrete);
				} else if (field.getType().equals(Double.class) || field.getType().equals(double.class))
				{
					field.set(state_class, field.get(change));
				} else if (discrete)
				{
					field.set(state_class, field.get(change));
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Loggable(Loggable.TRACE)
	public void storeChangeValuez(Boolean pre_jump)
	{
		for (ObjectManipulator field : content.getObjControl().getFieldParentMap().values())
		{

			try
			{
				if (pre_jump == null)
				{
					if (field.getField().getType().equals(Double.class)
					|| field.getField().getType().equals(double.class))
					{
						field.updateObject(0.0, field.getChangeParent());
					}
				} else
				{
					if (pre_jump)
					{
						field.updateObject(field.getObject(), field.getChangeParent());
					} else
					{
						field.updateObject(field.getChange());
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	@Loggable(Loggable.TRACE)
	public static void storeJumpValues(Object old_state, Object new_state, boolean pre_jump)
	{
		ObjectManipulator.updateValues(old_state, new_state);
	}
}
