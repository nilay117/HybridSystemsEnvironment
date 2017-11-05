package edu.ucsc.cross.hse.core.data;

import com.be3short.obj.stringparse.StringParser;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class CSVFile
{

	private static HashMap<CSVFile, ExecutionOperator> managers = new HashMap<CSVFile, ExecutionOperator>();
	private static HashMap<CSVFile, PrintWriter> writers = new HashMap<CSVFile, PrintWriter>();
	private String contents;

	public CSVFile(ExecutionOperator manager)
	{
		managers.put(this, manager);
	}

	public CSVFile(ExecutionOperator manager, Boolean store_file_internally)
	{
		StringWriter out = new StringWriter();
		writers.put(this, new PrintWriter(out));
		managers.put(this, manager);
		createCSVOutput(null, true);
		contents = out.toString();
		writers.get(this).close();
		// writers.get(this).close();
	}

	public void createCSVOutput(File file)
	{
		createCSVOutput(file, false);
		writers.get(this).close();
	}

	public void createCSVOutput(File file, boolean skip_prepare)
	{
		if (!skip_prepare)
		{
			prepareDynamicOutput();
		}
		fileOut().println(getDynamicHeader());
		fileOut().flush();
		for (Integer index = 0; index < manager().getDataCollector().getStoreTimes().size(); index++)
		{
			String line = getLine(index);
			HybridTime time = manager().getDataCollector().getStoreTimes().get(index);
			for (DataSeries<?> data : manager().getDataCollector().getGlobalStateData())
			{
				line += "," + data.getStoredData(time).toString();
			}
			fileOut().println(line);
			fileOut().flush();
		}
		fileOut().close();
	}

	public void prepareDynamicOutput()
	{
		try
		{
			writers.put(this,
			new PrintWriter(
			new FileOutputStream(new File(manager().getEnvironment().getSettings().getOutputSettings().outputDirectory
			+ "/" + ExecutionOperator.getStartTime(manager().getEnvironment(), false) + "/environmentOutput"
			+ ExecutionOperator.getStartTime(manager().getEnvironment(), true) + ".csv"))));
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private String getDynamicHeader()
	{
		String header = "dataIndex,simulationTime,jumpIndex";
		for (Integer objIndex = 0; objIndex < manager().getExecutionContent()
		.getSimulatedObjectAccessVector().length; objIndex++)
		{
			DataSeries<?> data = manager().getDataCollector().getGlobalStateData().get(objIndex);
			header += "," + data.getHeader();
		}
		return header;

	}

	private String getLine(Integer index)
	{
		HybridTime time = manager().getDataCollector().getStoreTimes().get(index);
		String line = index + "," + time.getTime() + "," + time.getJumps();
		return line;
	}

	public EnvironmentData getLocalCSVData()
	{
		String lines[] = contents.split("\n");
		String line = lines[0];
		EnvironmentData envData = getEnvDataWithSeries(line);
		int i = 1;
		while (i < lines.length)
		{
			loadDataFromLine(envData, lines[i++]);
		}
		return envData;
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

	private PrintWriter fileOut()
	{
		PrintWriter fileOut = null;
		if (writers.containsKey(this))
		{
			try
			{
				fileOut = writers.get(this);
			} catch (Exception e)
			{

			}
		}
		return fileOut;
	}

	private ExecutionOperator manager()
	{
		ExecutionOperator fileOut = null;
		if (managers.containsKey(this))
		{
			try
			{
				fileOut = managers.get(this);
			} catch (Exception e)
			{

			}
		}
		return fileOut;
	}

}