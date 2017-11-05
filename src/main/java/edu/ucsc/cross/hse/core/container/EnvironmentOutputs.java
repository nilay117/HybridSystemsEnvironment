package edu.ucsc.cross.hse.core.container;

import com.be3short.io.format.FileFormat;
import com.be3short.io.format.FileSpecifications;
import com.be3short.io.format.ImageFormat;
import edu.cross.ucsc.hse.core.chart.ChartProperties;
import edu.cross.ucsc.hse.core.chart.ChartView;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.file.CSVFile;
import edu.ucsc.cross.hse.core.file.EnvironmentFile;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.task.TaskManager;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class EnvironmentOutputs
{

	// Mapping of all plots to be generated and output file specifications for plots that will be saved to a file
	private HashMap<FileSpecifications<ImageFormat>, ChartProperties> plots;

	public void addChart(ChartProperties... plot)
	{
		for (ChartProperties plo : plot)
		{
			plots.put(new FileSpecifications<ImageFormat>(), plo);
		}
	}

	public void addChart(ChartProperties plot, FileSpecifications<ImageFormat> specs)
	{
		plots.put(specs, plot);
	}

	public void addChart(ChartProperties plot, String path, ImageFormat format)
	{
		plots.put(format.createFileSpecs(path), plot);
	}

	public void generateCharts()
	{
		generateCharts(getFromContent(this), getFromContent(this).getSettings().getOutputSettings().saveChartsToFile);
	}

	public void generateCharts(boolean create_files)
	{
		generateCharts(getFromContent(this), create_files);
	}

	public void generateCharts(Environment envi)
	{

		generateCharts(envi, envi.getSettings().getOutputSettings().saveChartsToFile);

	}

	public void generateCharts(Environment envi, boolean create_files)
	{

		HashMap<FileSpecifications<ImageFormat>, ChartProperties> plotz = envi.getManager().getOutputs()
		.getFileSpecChartMap(true);
		int append = 1;
		for (FileSpecifications<ImageFormat> specs : plotz.keySet())
		{
			ChartProperties plot = plotz.get(specs);
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

	public void generateOutputs(Environment envi)
	{
		initializeDataNames(envi);
		generateCharts(envi);
		generateFiles(envi);
	}

	public void generateOutputs(Environment envi, boolean create_files)
	{
		initializeDataNames(envi);

		generateCharts(envi, create_files);

		if (create_files)
		{
			generateFiles(envi);
		}
	}

	public ArrayList<ChartProperties> getLoadedCharts()
	{
		return new ArrayList<ChartProperties>(plots.values());
	}

	public void removeChart(ChartProperties... charts)
	{
		ArrayList<ChartProperties> chartList = new ArrayList<ChartProperties>(Arrays.asList(charts));
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

	public void generateFiles(Environment env)
	{
		EnvironmentData data = env.getData();
		if (env.getSettings().getOutputSettings().saveConfigurationToFile)
		{
			FileSpecifications<EnvironmentFile> specs = getFileSpecifications(env, new EnvironmentFile(),
			env.getSettings().getOutputSettings().configurationFileName
			+ ExecutionOperator.getStartTime(env, true).toString());
			File spe = specs.getLocation(true);
			env.save(spe, false);
			Console.info("Configuration saved: " + spe.getAbsolutePath());
		}
		env.loadData(data);
		if (env.getSettings().getOutputSettings().saveEnvironmentToFile)
		{
			FileSpecifications<EnvironmentFile> specs = getFileSpecifications(env, new EnvironmentFile(),
			env.getSettings().getOutputSettings().environmentFileName
			+ ExecutionOperator.getStartTime(env, true).toString());
			File spe = specs.getLocation(true);

			if (env.getSettings().getOutputSettings().saveDataToCSVFile)
			{
				env.save(spe, false);
			} else
			{
				env.save(spe, true);
			}

			Console.info("Environment saved: " + spe.getAbsolutePath());
		}
		env.loadData(data);
		if (env.getSettings().getOutputSettings().saveDataToCSVFile)
		{
			FileSpecifications<EnvironmentFile> specs = getFileSpecifications(env, new EnvironmentFile(),
			env.getSettings().getOutputSettings().environmentFileName
			+ ExecutionOperator.getStartTime(env, true).toString());
			File spe = specs.getLocation(true);
			String corrected = spe.getAbsolutePath().replaceAll(".hse", ".csv");
			spe = new File(corrected);
			CSVFile csv = new CSVFile(env.getManager());
			csv.createCSVOutput(spe);
		}
	}

	private <F extends FileFormat, T> HashMap<FileSpecifications<F>, T> getAppendedFiles(
	HashMap<FileSpecifications<F>, T> unappended)
	{
		HashMap<FileSpecifications<F>, T> appended = new HashMap<FileSpecifications<F>, T>();
		String prefix = String.valueOf(ExecutionOperator.getStartTime(getFromContent(this), false));
		for (FileSpecifications<F> spec : unappended.keySet())
		{
			FileSpecifications<F> specs = spec.copy();
			if (!specs.isNullFile())
			{
				specs.prependDirectory((FileSpecifications.checkSlashes(
				new File(getFromContent(this).getSettings().getOutputSettings().outputDirectory).getAbsolutePath(),
				prefix)));
				// System.out.println("final path: " + specs.getLocation(false).getAbsolutePath());

			}
			appended.put(specs, unappended.get(spec));
		}
		return appended;
	}

	private HashMap<FileSpecifications<ImageFormat>, ChartProperties> getFileSpecChartMap(boolean appended)
	{
		if (appended)
		{
			return getAppendedFiles(plots);
		}
		return plots;

	}

	private <T extends FileFormat> FileSpecifications<T> getFileSpecifications(Environment env, T spec_type,
	String file_name)
	{
		FileSpecifications<T> specs = new FileSpecifications<T>(file_name, spec_type);
		specs.prependDirectory(ExecutionOperator.getStartTime(env, false).toString());
		specs.prependDirectory(env.getSettings().getOutputSettings().outputDirectory);
		return specs;
	}

	private void initializeDataNames(Environment env)
	{
		env.getData().getLabelOrder();

	}

	public EnvironmentOutputs(Environment environment)
	{
		environments.put(this, environment);
		plots = new HashMap<FileSpecifications<ImageFormat>, ChartProperties>();
	}

	private static HashMap<EnvironmentOutputs, Environment> environments = new HashMap<EnvironmentOutputs, Environment>();

	public static Environment getFromContent(EnvironmentOutputs ob)
	{
		if (environments.containsKey(ob))
		{
			return environments.get(ob);
		}
		return null;
	}
}
