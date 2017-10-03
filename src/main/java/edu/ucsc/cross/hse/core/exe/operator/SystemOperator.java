package edu.ucsc.cross.hse.core.exe.operator;

import bs.commons.objects.manipulation.XMLParser;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.access.ObjectManipulator;
import edu.ucsc.cross.hse.core.exe.monitor.Console;
import edu.ucsc.cross.hse.core.obj.structure.HybridSystem;
import edu.ucsc.cross.hse.core.exe.operator.EnvironmentManager;

@Loggable(Loggable.TRACE)
public class SystemOperator
{

	private EnvironmentManager content;

	public SystemOperator(EnvironmentManager content)
	{
		this.content = content;
	}

	public boolean checkDomain(boolean jump)
	{
		boolean domain = false;
		for (HybridSystem<?> hs : content.getContents().getSystems())
		{
			ObjectManipulator.updateValues(HybridSystem.getInputState(hs), hs.getState());
			if (jump)
			{
				domain = domain || hs.D();
			} else
			{
				domain = domain || hs.C();
			}
		}
		return domain;
	}

	public void applyDynamics(boolean jump_occurring)
	{
		boolean jump = checkDomain(true);
		prepareDynamicComponents(jump);

		for (HybridSystem<?> hs : content.getContents().getSystems())
		{
			try
			{
				if (jump && jump_occurring)
				{
					hs.G();
				} else
				{
					hs.F();
				}
			} catch (Exception dynamicsError)
			{
				Console.out.error("Apply Dynamics Error on " + hs.getClass() + " with state: \n"
				+ XMLParser.serializeObject(hs.getState()), dynamicsError);
			}
		}
		processDynamicComponents(jump);
	}

	public void prepareDynamicComponents(boolean jump)
	{
		if (jump)
		{
			storeChangeValuez(true);
		} else
		{
			clearChangeValues();
		}
	}

	public void processDynamicComponents(boolean jump)
	{
		if (jump)
		{
			storeChangeValuez(false);
			clearChangeValues();
		}
	}

	@Loggable(Loggable.TRACE)
	public void clearChangeValues()
	{
		for (ObjectManipulator field : content.getObjControl().getNumericalStateMap().values())
		{
			try
			{
				if (field.getField().getType().equals(Double.class) || field.getField().getType().equals(double.class))
				{
					field.updateObject(0.0, field.getChangeParent());
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@Loggable(Loggable.TRACE)
	public void storeChangeValuez(boolean pre_jump)
	{
		for (ObjectManipulator field : content.getObjControl().getFieldParentMap().values())
		{
			try
			{

				if (pre_jump)
				{
					field.updateObject(field.getObject(), field.getChangeParent());
				} else
				{
					field.updateObject(field.getChange());
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

}
