package edu.ucsc.cross.hse.core.exe.operator;

import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.access.ObjectManipulator;
import edu.ucsc.cross.hse.core.exe.monitor.JumpStatus;
import edu.ucsc.cross.hse.core.obj.data.DataSeries;
import edu.ucsc.cross.hse.core.obj.data.DataSet;
import edu.ucsc.cross.hse.core.obj.structure.EnvironmentContent;
import edu.ucsc.cross.hse.core.obj.structure.HybridTime;
import java.util.HashMap;

@Loggable(Loggable.TRACE)
public class DataOperator
{

	private EnvironmentManager manager;
	private Double nextStoreTime;

	public DataOperator(EnvironmentManager manager)
	{
		this.manager = manager;
		nextStoreTime = 0.0;
	}

	public void monitorData(double time, double state_vector[], JumpStatus jump_status)
	{
		updateTime(time, jump_status);
		loadData(state_vector, jump_status);
		updateData(jump_status);
	}

	private void loadData(double state_vector[], JumpStatus jump_status)
	{
		if (jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			manager.getVector().getValueVector(state_vector, true);
		} else
		{
			manager.getVector().readStateValues(state_vector);
		}
	}

	private void updateData(JumpStatus jump_status)
	{
		if (jump_status.equals(JumpStatus.JUMP_DETECTED) || jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			storeNewData();
			nextStoreTime = manager.getContents().getSimTime().getTime()
			+ manager.getSettings().getDataSettings().dataStoreIncrement;
		} else if (jump_status.equals(JumpStatus.NO_JUMP))
		{
			if (manager.getContents().getSimTime().getTime() >= nextStoreTime)
			{
				storeNewData();
				nextStoreTime += manager.getSettings().getDataSettings().dataStoreIncrement;
			}

		}
	}

	private void updateTime(Double time, JumpStatus jump_status)
	{
		if (jump_status.equals(JumpStatus.NO_JUMP) || jump_status.equals(JumpStatus.JUMP_DETECTED))
		{
			manager.getTimeOperator().updateSimulationTime(time);
		} else if (jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			manager.getTimeOperator().updateSimulationTime(
			new HybridTime(time, manager.getTimeOperator().getHybridSimulationTime().getJumps() + 1));
		}
	}

	public void storeNewData()
	{

		for (DataSeries<Double> data : manager.getDataCollector().getGlobalStateData().values())
		{

			data.storeData();
		}
		manager.getDataCollector().getStoreTimes().add(manager.getContents().getSimTime());
	}

	public void restoreInitialData()
	{
		nextStoreTime = 0.0;
		manager.getDataCollector().getStoreTimes().clear();
		for (DataSeries<Double> data : manager.getDataCollector().getGlobalStateData().values())
		{
			data.getElementLocation().updateObject(data.getAllStoredData().get(0));
			data.getAllStoredData().clear();
		}

	}

	public void loadMap()
	{
		// if (globalStateData == null)
		// {
		// storeTimes.clear();
		manager.getDataCollector().getGlobalStateData().clear();
		new HashMap<ObjectManipulator, DataSeries<Double>>();
		for (ObjectManipulator state : manager.getObjControl().getNumericalStateMap().values())
		{
			manager.getDataCollector().getGlobalStateData().put(state,
			new DataSeries<Double>(state, manager.getDataCollector().getStoreTimes()));
		}
		// }
	}
}
