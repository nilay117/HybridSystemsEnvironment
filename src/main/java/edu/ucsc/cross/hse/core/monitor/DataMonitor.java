package edu.ucsc.cross.hse.core.monitor;

import com.be3short.obj.manipulation.ObjectManipulator;
import com.be3short.obj.stringparse.StringParser;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.object.Objects;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class DataMonitor
{

	private ExecutionOperator manager;
	private PrintWriter fileOut;
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

	// public double[] getLastValueArray()
	// {
	// double[] values = new double[manager.getSimEngine().getDimension()];
	// int i = 0;
	// for (DataSeries<?> series : manager.getDataCollector().getGlobalStateData())
	// {
	//
	// values[i++] = series.getAllStoredData().get(series.getAllStoredData().size() - 1);
	// }
	// return values;
	// }

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

	public void createCSVOutput()
	{
		prepareDynamicOutput();
		fileOut.println(getDynamicHeader());
		fileOut.flush();
		for (Integer index = 0; index < manager.getDataCollector().getStoreTimes().size(); index++)
		{
			String line = getLine(index);
			HybridTime time = manager.getDataCollector().getStoreTimes().get(index);
			for (DataSeries<?> data : manager.getDataCollector().getGlobalStateData())
			{
				line += "," + data.getStoredData(time).toString();
			}
			fileOut.println(line);
			fileOut.flush();
		}
		fileOut.close();
	}

	public void prepareDynamicOutput()
	{
		try
		{
			fileOut = new PrintWriter(
			new FileOutputStream(new File(manager.getEnvironment().getSettings().getOutputSettings().outputDirectory
			+ "/" + ExecutionOperator.getStartTime(manager.getEnvironment(), false) + "/environmentOutput"
			+ ExecutionOperator.getStartTime(manager.getEnvironment(), true) + ".csv")));
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private String getDynamicHeader()
	{
		String header = "dataIndex,simulationTime,jumpIndex";
		for (Integer objIndex = 0; objIndex < manager.getExecutionContent()
		.getSimulatedObjectAccessVector().length; objIndex++)
		{
			DataSeries<?> data = manager.getDataCollector().getGlobalStateData().get(objIndex);
			header += "," + data.getHeader();
		}
		return header;

	}

	private String getLine(Integer index)
	{
		HybridTime time = manager.getDataCollector().getStoreTimes().get(index);
		String line = index + "," + time.getTime() + "," + time.getJumps();
		return line;
	}

	public static EnvironmentData getCSVData(File csv_file)
	{
		BufferedReader input = getFileInput(csv_file);
		String line = readLine(input);
		EnvironmentData envData = getEnvDataWithSeries(line);
		line = readLine(input);
		while (line != null)
		{
			loadDataFromLine(envData, line);
			line = readLine(input);
		}
		return envData;
	}

	private static String readLine(BufferedReader input)
	{
		String line = null;
		try
		{
			line = input.readLine();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		return line;
	}

	private static BufferedReader getFileInput(File file)
	{
		BufferedReader input = null;
		try
		{
			FileReader fileReader = new FileReader(file);
			input = new BufferedReader(fileReader);
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return input;
	}

	private static EnvironmentData getEnvDataWithSeries(String header)
	{
		EnvironmentData data = new EnvironmentData();
		String[] split = header.split(",");
		for (Integer i = 3; i < split.length; i++)
		{
			DataSeries<?> series = DataSeries.getDataSeries(data, split[i]);
			data.getGlobalStateData().add(series);
		}
		return data;
	}

	private static void loadDataFromLine(EnvironmentData data, String header)
	{
		String[] split = header.split(",");
		HybridTime time = new HybridTime(Double.parseDouble(split[1]), Integer.parseInt(split[2]));
		data.getStoreTimes().add(time);
		for (Integer i = 3; i < split.length; i++)
		{
			data.getGlobalStateData().get(i - 3)
			.storeDataGeneral(StringParser.parseString(split[i], data.getGlobalStateData().get(i - 3).getDataClass()));
		}
	}

}
