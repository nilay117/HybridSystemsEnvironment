package edu.ucsc.cross.hse.core.operator;

import com.be3short.io.xml.XMLParser;
import com.be3short.obj.manipulation.DynamicObjectManipulator;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import edu.ucsc.cross.hse.core.object.HybridSystem.HybridSystemOperator;

public class SystemOperator
{

	private ExecutionOperator content;

	public void applyDynamics(boolean jump_occurring)
	{
		prepareDynamicComponents(jump_occurring);

		applySystemDynamics(jump_occurring);

		processDynamicComponents(jump_occurring);
	}

	public void applySystemDynamics(boolean jump_occurring)
	{
		for (HybridSystem<?> hs : content.getContents().getSystems())
		{
			try
			{
				boolean hasPriority = true;
				if (jump_occurring)
				{
					if (!getJumpPriority())
					{
						if (HybridSystemOperator.c(hs))
						{
							hasPriority = false;
						}
					}
					if (hasPriority)
					{
						HybridSystemOperator.g(hs);
					}
				} else
				{
					if (getJumpPriority())
					{
						if (HybridSystemOperator.d(hs))
						{
							hasPriority = false;
						}
					}
					if (hasPriority)
					{
						HybridSystemOperator.f(hs);
					}
				}
			} catch (Exception dynamicsError)
			{
				Console.error("Apply Dynamics Error on " + hs.getClass() + " with state: \n"
				+ XMLParser.serializeObject(hs.getState()), dynamicsError);
			}
		}
	}

	public boolean checkDomain(boolean jump)
	{
		boolean domain = false;
		for (HybridSystem<?> hs : content.getContents().getSystems())
		{
			if (jump)
			{
				domain = domain || HybridSystemOperator.d(hs);
			} else
			{
				domain = domain || HybridSystemOperator.c(hs);
			}
		}
		return domain;
	}

	public void clearChangeValues()
	{
		for (DynamicObjectManipulator field : content.getExecutionContent().getSimulatedObjectAccessVector())
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

	public void prepareDynamicComponents(boolean jump)
	{
		if (jump)
		{
			storeChangeValues(true);
		} else
		{
			clearChangeValues();
		}
	}

	public void processDynamicComponents(boolean jump)
	{
		// if jump has occurred (ode has settled state and is paused)
		if (jump)
		{
			storeChangeValues(false); // store the change values to the state
			clearChangeValues(); // clear the change variables

		}

	}

	public void storeChangeValues(boolean pre_jump)
	{
		for (DynamicObjectManipulator field : content.getExecutionContent().getFieldParentMap().values())
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

	public SystemOperator(ExecutionOperator content)
	{
		this.content = content;
	}

	public boolean getJumpPriority()
	{
		Boolean jumpPriority = content.getSettings().getComputationSettings().jumpPriority;
		if (jumpPriority == null)
		{
			Double randomSelect = Math.random() - .5;
			if (randomSelect >= 0.0)
			{
				jumpPriority = true;
			} else
			{
				jumpPriority = false;
			}
		}
		return jumpPriority;

	}
}
