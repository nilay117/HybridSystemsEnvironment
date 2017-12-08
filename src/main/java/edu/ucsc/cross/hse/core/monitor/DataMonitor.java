package edu.ucsc.cross.hse.core.monitor;

import com.be3short.obj.manipulation.ObjectManipulator;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.data.HybridArc.HybridArcData;
import edu.ucsc.cross.hse.core.engine.EnvironmentEngine;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.object.ObjectSet.ObjectSetAPI;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.util.ArrayList;
import java.util.HashMap;

public class DataMonitor
{

	private EnvironmentEngine manager;

	public void initializeUniqueNamesAndAddresses()
	{
		ArrayList<ObjectSet> duplicateNames = new ArrayList<ObjectSet>();
		for (ObjectManipulator state : manager.getExecutionContent().getFieldParentMap().values())// .getSimulatedObjectAccessVector())
		{
			try
			{
				ObjectSet parent = (ObjectSet) state.getParent();
				if (!duplicateNames.contains(parent))
				{
					int dup = 1;
					for (ObjectSet set : duplicateNames)
					{
						if (parent.data().getName().equals(set.data().getName()))
						{
							dup++;
						}
					}
					parent.data().setUniqueLabel(parent.data().getName() + "(" + dup + ")");
					duplicateNames.add(parent);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		removeIndicesFromNonDuplicateNames(duplicateNames);
	}

	public void loadMap()
	{

		EnvironmentData.getHybridArcMap(manager.getDataCollector()).clear();
		for (ObjectManipulator state : manager.getExecutionContent().getFieldParentMap().values())// .getSimulatedObjectAccessVector())
		{

			try
			{
				ObjectSet parent = (ObjectSet) state.getParent();
				if (ObjectSetAPI.isHistorySaved(parent))
				{
					if (!EnvironmentData.getHybridArcMap(manager.getDataCollector()).containsKey(parent))
					{

						EnvironmentData.getHybridArcMap(manager.getDataCollector()).put(parent,

						HybridArcData.createArc(parent, manager.getDataCollector().getStoreTimes()));
					}
					HybridArcData<?> solution = EnvironmentData.getHybridArcMap(manager.getDataCollector()).get(parent);
					solution.addSeries(state.getField(), DataSeries.getSeries(
					manager.getDataCollector().getStoreTimes(), parent, state.getField(), state.getField().getType()));
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		initializeUniqueNamesAndAddresses();
		DataMonitor.populateListMap(manager.getDataCollector());
	}

	public void performDataActions(double time, double state_vector[], JumpStatus jump_status)
	{
		performDataActions(time, state_vector, jump_status, false);
	}

	public void performDataActions(double time, double state_vector[], JumpStatus jump_status, boolean override_store)
	{

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
						// if (lastTime() > 0.0)
						{
							removeLastValue();
						}
					} catch (Exception removeLastValueFail)
					{
						removeLastValueFail.printStackTrace();
					}
				}
			} // else
				// {
				// System.out.println("yo");
				// }
		} catch (Exception removeValsFail)
		{
			removeValsFail.printStackTrace();
		}
	}

	public void restoreInitialData()
	{
		restoreInitialData(true);
	}

	public void restoreInitialData(boolean clear)
	{
		manager.getDataCollector().getStoreTimes().clear();
		for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
		.getSimulatedObjectAccessVector().length; objIndex++)
		{
			ObjectManipulator obj = manager.getExecutionContent().getSimulatedObjectAccessVector()[objIndex];
			DataSeries<?> data = DataMonitor.getAllDataSeries(manager.getDataCollector()).get(objIndex);
			obj.updateObject(data.getAllStoredData().get(0));
			if (clear)
			{
				data.getAllStoredData().clear();
			}
		}
	}

	public Double resetToLastData()
	{
		removePreviousVals(manager.getDataCollector().getLastStoredTime().getTime());
		HybridTime time = manager.getDataCollector().getLastStoredTime();
		for (DataSeries<?> data : DataMonitor.getAllDataSeries(manager.getDataCollector()))
		// .getSimulatedObjectAccessVector().length; objIndex++)
		{
			ObjectManipulator obj = manager.getExecutionContent().getFieldParentMap()
			.get(data.getParent().toString() + data.getChild().getName());// [objIndex];
			// DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
			obj.updateObject(data.getStoredData(time));
		}
		return time.getTime();
	}

	public void storeNewData(Double time)
	{
		// removePreviousVals(time);
		try
		{
			// if (!time.equals(manager.getDataCollector().getStoreTimes().get(0).getTime()))
			// {
			for (DataSeries<?> data : DataMonitor.getAllDataSeries(manager.getDataCollector()))
			// .getSimulatedObjectAccessVector().length; objIndex++)
			{
				ObjectManipulator obj = manager.getExecutionContent().getFieldParentMap()
				.get(data.getParent().toString() + data.getChild().getName());// [objIndex];
				// DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
				storeDataGeneral(data, obj.getObject());
			}
			// }
		} catch (

		Exception removeValsFail)
		{
			removeValsFail.printStackTrace();
		}
		manager.getDataCollector().getStoreTimes()
		.add(new HybridTime(time, manager.getExecutionContent().getHybridSimTime().getJumps()));
	}

	private Double lastTime()
	{
		return manager.getDataCollector().getLastStoredTime().getTime();
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

	/*
	 * Removes the index number appended to a unique name that was already unique.
	 */
	private void removeIndicesFromNonDuplicateNames(ArrayList<ObjectSet> objects)
	{
		for (ObjectSet obj : objects)
		{
			boolean nameAlreadyUnique = true;
			for (ObjectSet otherObj : objects)
			{
				if (!otherObj.equals(obj))
				{
					if (otherObj.data().getName().equals(obj.data().getName()))
					{
						nameAlreadyUnique = false;

					}
				}
			}
			if (nameAlreadyUnique)
			{
				obj.data().setUniqueLabel(obj.data().getName());
			}

		}
	}

	private void removeLastValue()
	{

		int i = manager.getDataCollector().getStoreTimes().indexOf(manager.getDataCollector().getLastStoredTime());

		if (manager.getDataCollector().getStoreTimes().size() > i)
		{
			for (Integer objIndex = 0; objIndex < DataMonitor.getAllDataSeries(manager.getDataCollector())
			.size(); objIndex++)
			{
				DataSeries<?> data = DataMonitor.getAllDataSeries(manager.getDataCollector()).get(objIndex);
				if (data.getAllStoredData().size() > i)
				{
					data.getAllStoredData().remove(i);
				}
			}
			manager.getDataCollector().getStoreTimes().remove(i);
		}

	}

	private void updateData(Double time, JumpStatus jump_status)
	{

		if (jump_status.equals(JumpStatus.JUMP_OCCURRED))
		{
			Console.debug(
			"Jump occurred : t = " + time + " j = " + manager.getExecutionContent().getHybridSimTime().getJumps());
			storeNewData(time);
		} else if (jump_status.equals(JumpStatus.JUMP_DETECTED))
		{
			Console.debug(
			"Jump detected : t = " + time + " j = " + manager.getExecutionContent().getHybridSimTime().getJumps());
			{
				storeNewData(time);
			}
		} else
		{
			if (jump_status.equals(JumpStatus.APPROACHING_JUMP))
			{
				if (time > (lastTime() + manager.getSettings().getOutputSettings().dataPointInterval))
				{
					removePreviousVals(time);
				}
			}
			if (time > (lastTime() + manager.getSettings().getOutputSettings().dataPointInterval))
			{
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

	public DataMonitor(EnvironmentEngine manager)
	{
		this.manager = manager;

		// nextStoreTime = 0.0;
	}

	public static HashMap<EnvironmentData, ArrayList<DataSeries<?>>> seriesListMap = new HashMap<EnvironmentData, ArrayList<DataSeries<?>>>();

	public static ArrayList<DataSeries<?>> getAllDataSeries(EnvironmentData dat)
	{
		if (!seriesListMap.containsKey(dat))
		{
			populateListMap(dat);
		}
		return seriesListMap.get(dat);
	}

	public static void populateListMap(EnvironmentData data)
	{
		seriesListMap.put(data, new ArrayList<DataSeries<?>>());
		for (HybridArcData<?> arc : EnvironmentData.getHybridArcMap(data).values())
		{
			for (DataSeries<?> series : arc.getData().values())
			{
				if (!seriesListMap.get(data).contains(series))
				{
					seriesListMap.get(data).add(series);
				}
			}
		}
	}

	/*
	 * Store the current value of the data element
	 */
	public static <X> void storeData(DataSeries<X> data, X value)
	{

		data.getAllStoredData().add(value);
	}

	public static <T extends Object> void storeDataGeneral(ArrayList<T> allStoredData, Object parseString)
	{
		// TODO Auto-generated method stub
		allStoredData.add((T) parseString);
	}

	/*
	 * Store the current value of the data element
	 */
	@SuppressWarnings("unchecked")
	public static <X> void storeDataGeneral(DataSeries<X> data, Object value)
	{

		data.getAllStoredData().add((X) value);
	}

}
