package edu.ucsc.cross.hse.core.file;

import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.data.HybridArc;
import edu.ucsc.cross.hse.core.data.HybridArc.HybridArcData;
import edu.ucsc.cross.hse.core.engine.EnvironmentEngine;
import edu.ucsc.cross.hse.core.monitor.DataMonitor;
import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;

public class CSVFileParser
{

	private EnvironmentData data;

	public CSVFileParser(EnvironmentData data)
	{
		this.data = data;
	}

	public <T extends ObjectSet> CSVFileParser(HybridArc<T> data)
	{
		this.data = new EnvironmentData();
		this.data.getStoreTimes().addAll(data.getDataDomain());
		EnvironmentData.getHybridArcMap(this.data).put(data.getCurrentObject(), new HybridArcData<T>(data));
	}

	private String createHeader()
	{
		String header = "time,jumps";
		for (DataSeries<?> series : DataMonitor.getAllDataSeries(data))
		{
			header += "," + series.getElementName();
		}
		return header;
	}

	public void createCSVOutput()
	{
		String filepath = "output/"
		+ EnvironmentEngine.getStartTime(EnvironmentEngine.getContainingEnvironment(data), false).toString()
		+ "/environmentData.csv";
		createCSVOutput(new File(filepath));
	}

	public void createCSVOutput(File file)
	{

		prepareFileWriter(file);

		fileOut().println(createHeader());
		fileOut().flush();
		for (Integer index = 0; index < data.getStoreTimes().size(); index++)
		{
			String line = getLineIntro(index);
			HybridTime time = data.getStoreTimes().get(index);
			for (DataSeries<?> data : DataMonitor.getAllDataSeries(data))
			{

				line += "," + data.getStoredData(time).toString();
			}
			fileOut().println(line);
			fileOut().flush();
		}

		fileOut().close();
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

	private String getLineIntro(Integer index)
	{
		HybridTime time = data.getStoreTimes().get(index);
		String line = time.getTime() + "," + time.getJumps();
		return line;
	}

	private void prepareFileWriter(File file)
	{
		try
		{
			// file.mkdirs();

			writers.put(this, new PrintWriter(new FileOutputStream(file)));
			// file.createNewFile();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static HashMap<CSVFileParser, PrintWriter> writers = new HashMap<CSVFileParser, PrintWriter>();

}
