package edu.ucsc.cross.hse.core.environment;

import com.be3short.data.cloning.ObjectCloner;
import com.be3short.io.format.FileSpecifications;
import com.be3short.io.format.ImageFormat;
import edu.cross.ucsc.hse.core.chart.ChartProperties;
import edu.ucsc.cross.hse.core.container.EnvironmentContent;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.container.EnvironmentOutputs;
import edu.ucsc.cross.hse.core.container.EnvironmentSettings;
import edu.ucsc.cross.hse.core.file.EnvironmentFile;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import edu.ucsc.cross.hse.core.operator.ExecutionOperator;
import edu.ucsc.cross.hse.core.setting.ExecutionParameters;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

/*
 * The main class of the hybrid systeme environment, conting the manager, contents, and the setting configurer.
 */
public class Environment
{

	private static HashMap<Environment, ExecutionOperator> operators = new HashMap<Environment, ExecutionOperator>();
	// Operator

	// Components
	final EnvironmentContent content;
	EnvironmentData dataCollector;
	final EnvironmentOutputs outputs;
	final EnvironmentSettings settings;

	public void add(ChartProperties... plot)
	{

		outputs.addPlot(plot);

	}

	public void add(ChartProperties plot, FileSpecifications<ImageFormat> specs)
	{
		outputs.addPlot(plot, specs);
	}

	public void add(ChartProperties plot, String path, ImageFormat format)
	{
		outputs.addPlot(plot, path, format);
	}

	public void add(HybridSystem<?>... new_systems)
	{
		content.add(new_systems);
	}

	public void add(HybridSystem<?> system, Integer quantity)
	{
		content.add(system, quantity);
	}

	public Environment copy()
	{
		return ObjectCloner.deepInstanceClone(this);
	}

	public void generateOutputs()
	{
		outputs.generateOutputs(this);
	}

	public EnvironmentContent getContents()
	{
		return content;
	}

	public EnvironmentData getData()
	{
		return dataCollector;
	}

	public ExecutionOperator getManager()
	{
		if (!operators.containsKey(this))
		{
			operators.put(this, new ExecutionOperator(this));
		}
		return operators.get(this);
	}

	public EnvironmentOutputs getOutputs()
	{
		return outputs;
	}

	public ExecutionParameters getParameters()
	{
		return getManager().getExecutionParameters();
	}

	public EnvironmentSettings getSettings()
	{
		return settings;
	}

	public void load(Environment new_env)
	{
		for (Field field : Environment.class.getDeclaredFields())
		{
			try
			{
				field.set(this, field.get(new_env));
			} catch (IllegalArgumentException | IllegalAccessException e)
			{
				Console.error("failed to load environment" + new_env);
			}
		}

	}

	public void load(File new_env)
	{
		Environment env = Environment.loadFile(new_env);
		try
		{
			if (env != null)
			{
				load(env);
			}
		} catch (Exception badManager)
		{
			// Console.error("Unable to load environment from file: " + new_env, badManager);
		}
	}

	public void load(EnvironmentContent contents)
	{
		getContents().load(contents);
	}

	public void load(EnvironmentSettings settings)
	{
		getSettings().loadAllSettings(settings);
	}

	public void start()
	{
		// Debug.debug("Starting environment");
		getManager().run();
		// manager.getOutputs().generatePlots();
	}

	public void start(Double run_time)
	{
		getManager().getExecutionParameters().maximumTime = (run_time);
		start();
	}

	public void start(Double run_time, Integer jump_limit)
	{
		getManager().getExecutionParameters().setMaximumTimeAndJumps(run_time, jump_limit);
		start();
	}

	public void start(ExecutionParameters params)
	{
		settings.loadExecutionParameters(params);
		start();
	}

	public void save(File file)
	{
		save(file, true);
	}

	public void save(File file, boolean save_data)
	{
		EnvironmentData savedData = dataCollector;
		if (!save_data)
		{
			dataCollector = null;
		}
		EnvironmentFile output = new EnvironmentFile();
		output.add(this);
		output.writeToFile(file);
		dataCollector = savedData;
	}

	public Environment()
	{

		getManager();
		settings = new EnvironmentSettings();
		content = new EnvironmentContent();
		outputs = new EnvironmentOutputs(this);
		dataCollector = new EnvironmentData();
	}

	public static Environment loadFile(File file)
	{
		Environment env = EnvironmentFile.readContentFromFile(file, EnvironmentFile.ENVIRONMENT);
		env.getManager();
		return env;
	}

}
