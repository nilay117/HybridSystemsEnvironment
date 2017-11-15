package edu.ucsc.cross.hse.core.container;

import com.be3short.io.format.FileSpecifications;
import com.be3short.io.format.ImageFormat;
import edu.cross.ucsc.hse.core.chart.ChartConfiguration;
import edu.cross.ucsc.hse.core.chart.ChartView;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.task.TaskManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EnvironmentOutputs
{

	// Mapping of all plots to be generated and output file specifications for plots that will be saved to a file
	private HashMap<FileSpecifications<ImageFormat>, ChartConfiguration> plots;

	public void addChart(ChartConfiguration... plot)
	{
		for (ChartConfiguration plo : plot)
		{
			plots.put(new FileSpecifications<ImageFormat>(), plo);
		}
	}

	public void addChart(ChartConfiguration plot, FileSpecifications<ImageFormat> specs)
	{
		plots.put(specs, plot);
	}

	public void addChart(ChartConfiguration plot, String path, ImageFormat format)
	{
		plots.put(format.createFileSpecs(path), plot);
	}

	public void generateOutputs()
	{

		generateCharts(ExecutionOperator.getContainingEnvironment(this),
		ExecutionOperator.getContainingEnvironment(this).getSettings().getOutputSettings().saveChartsToFile);
	}

	public void generateOutputs(Environment envi)
	{
		generateCharts(envi, envi.getSettings().getOutputSettings().saveChartsToFile);
	}

	public void generateOutputs(Environment envi, boolean create_files)
	{
		generateCharts(envi, create_files);
	}

	public ArrayList<ChartConfiguration> getCharts()
	{
		return new ArrayList<ChartConfiguration>(plots.values());
	}

	public HashMap<FileSpecifications<ImageFormat>, ChartConfiguration> getChartsWithFileSpecs()
	{
		return plots;
	}

	public void removeChart(ChartConfiguration... charts)
	{
		ArrayList<ChartConfiguration> chartList = new ArrayList<ChartConfiguration>(Arrays.asList(charts));
		ArrayList<FileSpecifications<ImageFormat>> filez = new ArrayList<FileSpecifications<ImageFormat>>(
		plots.keySet());
		for (FileSpecifications<ImageFormat> chartFile : filez)
		{
			if (chartList.contains(plots.get(chartFile)))
			{
				plots.remove(chartFile);
			}
		}
	}

	private void generateCharts(Environment envi, boolean create_files)
	{

		HashMap<FileSpecifications<ImageFormat>, ChartConfiguration> plotz = ExecutionOperator.getAppendedFiles(plots,
		this);

		int append = 1;
		for (FileSpecifications<ImageFormat> specs : plotz.keySet())
		{
			ChartConfiguration plot = plotz.get(specs);
			if (create_files)
			{
				if (specs.isNullFile())
				{
					specs = envi.getSettings().getOutputSettings().chartFileFormat
					.createFileSpecs(("chart" + append++) + ExecutionOperator.getStartTime(envi, true).toString());

				}

				specs.prependDirectory(ExecutionOperator.getStartTime(envi, false).toString());
				specs.prependDirectory(envi.getSettings().getOutputSettings().outputDirectory);
				new ChartView(envi.getData(), plot, TaskManager.createStage(), specs);
			} else

			{

				new ChartView(envi.getData(), plot, TaskManager.createStage());

			}
		}

	}

	public EnvironmentOutputs(Environment environment)
	{
		plots = new HashMap<FileSpecifications<ImageFormat>, ChartConfiguration>();
	}

}
