package edu.ucsc.cross.hse.core.file;

import com.be3short.obj.stringparse.StringParser;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.data.HybridArc;
import edu.ucsc.cross.hse.core.monitor.DataMonitor;
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
import java.util.regex.Pattern;

public class CSVFile
{

	private String contents;

	public void createCSVOutput(File file)
	{
		createCSVOutput(file, false);
		writers.get(this).close();
	}

	public void createCSVOutput(File file, boolean skip_prepare)
	{
		StringWriter writer = null;
		if (!skip_prepare)
		{
			prepareDynamicOutput();
		} else
		{
			writer = new StringWriter();
			writers.put(this, new PrintWriter(writer));
		}
		fileOut().println(getDynamicHeader());
		fileOut().flush();
		for (Integer index = 0; index < manager().getDataCollector().getStoreTimes().size(); index++)
		{
			String line = getLine(index);
			HybridTime time = manager().getDataCollector().getStoreTimes().get(index);
			for (DataSeries<?> data : manager().getDataCollector().getAllDataSeries())
			{

				line += "," + data.getStoredData(time).toString();
			}
			fileOut().println(line);
			fileOut().flush();
		}
		if (writer != null)
		{
			contents = writer.toString();
		}
		fileOut().close();
	}

	public EnvironmentData extractDataFromContents()
	{
		String lines[] = contents.split(Pattern.quote("**!!@#$"));
		String line = lines[0];
		EnvironmentData envData = getEnvDataWithSeries(line);
		lines = lines[1].split(("\n"));
		// EnvironmentData envData = getEnvDataWithSeries(line);
		int i = 1;
		while (i < lines.length)
		{
			loadDataFromLine(envData, lines[i++]);
		}
		return envData;
	}

	public void storeDataToContents()
	{
		contents = "";
		// StringWriter out = new StringWriter();
		// writers.put(this, new PrintWriter(out));
		managers.put(this, manager());
		createCSVOutput(null, true);
		// contents += out.toString();
		writers.get(this).close();
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

	private String getDynamicHeader()
	{
		String header = "dataIndex,simulationTime,jumpIndex";
		for (Integer objIndex = 0; objIndex < manager().getDataCollector().getAllDataSeries().size(); objIndex++)
		{
			DataSeries<?> data = manager().getDataCollector().getAllDataSeries().get(objIndex);
			header += "," + data.getHeader();
		}
		header += "**!!@#$";
		return header;

	}

	private String getLine(Integer index)
	{
		HybridTime time = manager().getDataCollector().getStoreTimes().get(index);
		String line = index + "," + time.getTime() + "," + time.getJumps();
		return line;
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

	private void prepareDynamicOutput()
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

	public CSVFile(ExecutionOperator manager)
	{
		managers.put(this, manager);
	}

	public CSVFile(ExecutionOperator manager, Boolean store_file_internally)
	{
		managers.put(this, manager);
		createCSVOutput(null, true);
		// prepareDynamicOutput(); // writers.get(this).close();
	}

	private static HashMap<CSVFile, ExecutionOperator> managers = new HashMap<CSVFile, ExecutionOperator>();

	private static HashMap<CSVFile, PrintWriter> writers = new HashMap<CSVFile, PrintWriter>();

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

	private static EnvironmentData getEnvDataWithSeries(String header)
	{
		EnvironmentData data = new EnvironmentData();
		String[] split = header.split(",");
		for (Integer i = 3; i < split.length; i++)
		{
			DataSeries<?> series = DataSeries.getDataSeries(data, split[i]);
			if (!data.getHybridArcMap().containsKey(series.getParent()))
			{
				data.getHybridArcMap().put(series.getParent(),
				HybridArc.createArc(series.getParent(), data.getStoreTimes()));
			}
			HybridArc<?> solution = data.getHybridArcMap().get(series.getParent());
			solution.addSeries(series.getChild(), DataSeries.getSeries(data.getStoreTimes(), series.getParent(),
			series.getChild(), series.getChild().getType()));

		}
		// EnvironmentData.populateListMap(data);
		return data;
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
			// e.printStackTrace();
		}
		return input;
	}

	private static void loadDataFromLine(EnvironmentData data, String header)
	{
		String[] split = header.split(",");
		HybridTime time = new HybridTime(Double.parseDouble(split[1]), Integer.parseInt(split[2]));
		data.getStoreTimes().add(time);

		for (Integer i = 3; i < split.length; i++)
		{
			try
			{
				DataMonitor.storeDataGeneral(data.getAllDataSeries().get(i - 3).getAllStoredData(),
				StringParser.parseString(split[i], data.getAllDataSeries().get(i - 3).getDataClass()));
			} catch (Exception badIndex)
			{
				badIndex.printStackTrace();
			}
		}
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

}