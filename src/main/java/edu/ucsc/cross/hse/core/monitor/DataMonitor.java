package edu.ucsc.cross.hse.core.monitor;

import com.be3short.obj.manipulation.ObjectManipulator;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.object.Objects;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.time.HybridTime;

public class DataMonitor
{

	private ExecutionOperator manager;
	// private Double nextStoreTime;

	public DataMonitor(ExecutionOperator manager)
	{
		this.manager = manager;
		// nextStoreTime = 0.0;
	}

	public void gatherData(double time, double state_vector[], JumpStatus jump_status)
	{
		gatherData(time, state_vector, jump_status, false);
	}

	public void gatherData(double time, double state_vector[], JumpStatus jump_status, boolean override_store)
	{

		updateTime(time, jump_status);
		loadData(state_vector, jump_status);
		if (override_store)
		{
			storeNewData(time);

		} else
		{
			updateData(time, jump_status);
		}
	}

	private void loadData(double state_vector[], JumpStatus jump_status)
	{
		if (jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			manager.getExecutionContent().updateValueVector(state_vector);
		} else
		{
			manager.getExecutionContent().readStateValues(state_vector);
		}
	}

	private void updateData(Double time, JumpStatus jump_status)
	{

		if (jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			storeNewData(time);
		} else if (jump_status.equals(JumpStatus.JUMP_DETECTED))
		{
			removePreviousVals(time);
			storeNewData(time);
		} else // if (jump_status.equals(JumpStatus.APPROACHING_JUMP) || )
		{
			if (time > (lastTime() + manager.getSettings().getOutputSettings().dataPointInterval))
			{
				removePreviousVals(time);
				storeNewData(time);
			}

		}

	}

	private void updateTime(Double time, JumpStatus jump_status)
	{
		if (jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			manager.getExecutionContent().updateSimulationTime(time, 1);

		} else
		{
			manager.getExecutionContent().updateSimulationTime(time);
		}
	}

	public void storeNewData(Double time)
	{
		// removePreviousVals(time);
		for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
		.getSimulatedObjectAccessVector().length; objIndex++)
		{
			ObjectManipulator obj = manager.getExecutionContent().getSimulatedObjectAccessVector()[objIndex];
			DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
			data.storeDataGeneral(obj.getObject());
		}
		manager.getDataCollector().getStoreTimes()
		.add(new HybridTime(time, manager.getExecutionContent().getHybridSimTime().getJumps()));
	}

	public void removePreviousVals(Double time)
	{
		try
		{
			if (lastTime() > 0.0)
			{
				while (lastTime() >= time)
				{
					try
					{
						removeLastValue();
					} catch (Exception removeLastValueFail)
					{
						removeLastValueFail.printStackTrace();
					}
				}
			}

		} catch (Exception removeValsFail)
		{
			removeValsFail.printStackTrace();
		}
	}

	public void revertToLastStoredValue(Double time)
	{
		removePreviousVals(time);
		manager.getExecutionContent().readStateValues(manager.getExecutionContent().updateValueVector(null));
		manager.getExecutionContent().updateValueVector(null);
		manager.getExecutionContent().updateSimulationTime(lastTime());
		storeNewData(lastTime());
	}

	private void removeLastValue()
	{
		int i = manager.getDataCollector().getStoreTimes().indexOf(manager.getDataCollector().getLastStoredTime());

		if (manager.getDataCollector().getStoreTimes().size() > i)
		{
			for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
			.getSimulatedObjectAccessVector().length; objIndex++)
			{
				DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
				if (data.getAllStoredData().size() > i)
				{
					data.getAllStoredData().remove(i);
				}
			}
			manager.getDataCollector().getStoreTimes().remove(i);
		}

	}

	private Double lastTime()
	{
		return manager.getDataCollector().getLastStoredTime().getTime();
	}

	public void restoreInitialData()
	{
		manager.getDataCollector().getStoreTimes().clear();
		for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
		.getSimulatedObjectAccessVector().length; objIndex++)
		{
			ObjectManipulator obj = manager.getExecutionContent().getSimulatedObjectAccessVector()[objIndex];
			DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
			obj.updateObject(data.getAllStoredData().get(0));
			data.getAllStoredData().clear();
		}
	}

	public void loadMap()
	{

		manager.getDataCollector().getGlobalStateData().clear();
		for (ObjectManipulator state : manager.getExecutionContent().getSimulatedObjectAccessVector())
		{
			String parentName = state.getParent().getClass().getSimpleName();
			try
			{
				Objects parent = (Objects) state.getParent();
				parentName = parent.info().getName();
			} catch (Exception e)
			{

			}
			manager.getDataCollector().getGlobalStateData()
			.add(DataSeries.getSeries(manager.getDataCollector().getStoreTimes(), state.getObject().getClass(),
			state.getField().getName(), parentName, state.getParent().toString()));
		}
	}

}
