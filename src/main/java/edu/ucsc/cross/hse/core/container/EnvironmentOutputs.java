package edu.ucsc.cross.hse.core.container;

import com.be3short.io.format.FileFormat;
import com.be3short.io.format.FileSpecifications;
import com.be3short.io.format.ImageFormat;
import edu.cross.ucsc.hse.core.chart.ChartView;
import edu.cross.ucsc.hse.core.chart.HybridChart;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.file.EnvironmentFile;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.task.TaskManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class EnvironmentOutputs
{

	private static HashMap<EnvironmentOutputs, Environment> environments = new HashMap<EnvironmentOutputs, Environment>();

	public static Environment getFromContent(EnvironmentOutputs ob)
	{
		if (environments.containsKey(ob))
		{
			return environments.get(ob);
		}
		return null;
	}

	// Mapping of all plots to be generated and output file specifications for plots that will be saved to a file
	private HashMap<FileSpecifications<ImageFormat>, HybridChart> plots;

	public EnvironmentOutputs(Environment environment)
	{
		environments.put(this, environment);
		plots = new HashMap<FileSpecifications<ImageFormat>, HybridChart>();
	}

	public void addPlot(HybridChart... plot)
	{
		for (HybridChart plo : plot)
		{
			plots.put(new FileSpecifications<ImageFormat>(), plo);
		}
	}

	public void addPlot(HybridChart plot, FileSpecifications<ImageFormat> specs)
	{
		plots.put(specs, plot);
	}

	public void addPlot(HybridChart plot, String path, ImageFormat format)
	{
		plots.put(format.createFileSpecs(path), plot);
	}

	public ArrayList<HybridChart> getPlots()
	{
		return new ArrayList<HybridChart>(plots.values());
	}

	HashMap<FileSpecifications<ImageFormat>, HybridChart> getPlotsWithFileSpecs(boolean appended)
	{
		if (appended)
		{
			return getAppendedFiles(plots);
		}
		return plots;

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

	public void generateOutputs(Environment envi)
	{
		initializeDataNames(envi);
		generatePlots(envi);
		generateFiles(envi);
	}

	public void generateOutputs(Environment envi, boolean create_files)
	{
		initializeDataNames(envi);
		if (create_files)
		{
			generateFiles(envi);
		}
		generatePlots(envi, create_files);

	}

	public void generatePlots(boolean create_files)
	{
		generatePlots(getFromContent(this), create_files);
	}

	public void generatePlots()
	{
		generatePlots(getFromContent(this), getFromContent(this).getSettings().getOutputSettings().saveChartsToFile);
	}

	public void generatePlots(Environment envi)
	{

		generatePlots(envi, envi.getSettings().getOutputSettings().saveChartsToFile);

	}

	public void generatePlots(Environment envi, boolean create_files)
	{

		HashMap<FileSpecifications<ImageFormat>, HybridChart> plotz = envi.getManager().getOutputs()
		.getPlotsWithFileSpecs(true);
		int append = 1;
		for (FileSpecifications<ImageFormat> specs : plotz.keySet())
		{
			HybridChart plot = plotz.get(specs);
			if (create_files)
			{
				if (specs.isNullFile())
				{
					specs = envi.getSettings().getOutputSettings().chartFileFormat
					.createFileSpecs(("chart" + append++) + ExecutionOperator.getStartTime(envi, true).toString());

				}

				specs.prependDirectory(ExecutionOperator.getStartTime(envi, false).toString());
				specs.prependDirectory(envi.getSettings().getOutputSettings().outputDirectory);
				new ChartView(envi, plot, TaskManager.createStage(), specs);
			} else

			{

				new ChartView(envi, plot, TaskManager.createStage());

			}
		}

	}

	private void generateFiles(Environment env)
	{
		if (env.getSettings().getOutputSettings().saveConfigurationToFile)
		{
			FileSpecifications<EnvironmentFile> specs = getSpecs(env, new EnvironmentFile(),
			env.getSettings().getOutputSettings().configurationFileName
			+ ExecutionOperator.getStartTime(env, true).toString());
			File spe = specs.getLocation(true);
			env.save(spe, false);
			Console.info("Configuration saved: " + spe.getAbsolutePath());
		}
		if (env.getSettings().getOutputSettings().saveEnvironmentToFile)
		{
			FileSpecifications<EnvironmentFile> specs = getSpecs(env, new EnvironmentFile(),
			env.getSettings().getOutputSettings().environmentFileName
			+ ExecutionOperator.getStartTime(env, true).toString());
			File spe = specs.getLocation(true);
			env.save(spe, true);
			Console.info("Environment saved: " + spe.getAbsolutePath());
		}
	}

	private void initializeDataNames(Environment env)
	{
		for (DataSeries<?> series : env.getData().getGlobalStateData())
		{
			env.getData().getLegendLabel(series);
		}
	}

	private <T extends FileFormat> FileSpecifications<T> getSpecs(Environment env, T spec_type, String file_name)
	{
		FileSpecifications<T> specs = new FileSpecifications<T>(file_name, spec_type);
		specs.prependDirectory(ExecutionOperator.getStartTime(env, false).toString());
		specs.prependDirectory(env.getSettings().getOutputSettings().outputDirectory);
		return specs;
	}
}
