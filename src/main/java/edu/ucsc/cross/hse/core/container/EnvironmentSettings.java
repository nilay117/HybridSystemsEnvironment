package edu.ucsc.cross.hse.core.container;

import edu.ucsc.cross.hse.core.setting.ComputationSettings;
import edu.ucsc.cross.hse.core.setting.ExecutionParameters;
import edu.ucsc.cross.hse.core.setting.LogSettings;
import edu.ucsc.cross.hse.core.setting.OutputSettings;

public class EnvironmentSettings
{

	private OutputSettings dataSettings;
	private ComputationSettings environmentSettings;
	private LogSettings logging;
	private ExecutionParameters parameters;

	public ExecutionParameters getExecutionParameters()
	{
		return parameters;
	}

	public ComputationSettings getComputationSettings()
	{
		return environmentSettings;
	}

	public LogSettings getLogSettings()
	{
		return logging;
	}

	public OutputSettings getOutputSettings()
	{
		return dataSettings;
	}

	public void loadAllSettings(EnvironmentSettings settings)
	{
		this.environmentSettings = settings.environmentSettings;
		this.dataSettings = settings.dataSettings;
		this.logging = settings.logging;
	}

	public void loadComputationSettings(ComputationSettings environment_settings)
	{
		// for (Field field : environment_settings.getClass().getFields())
		// {
		// try
		// {
		// field.set(environment_settings, field.get(environmentSettings));
		// } catch (IllegalArgumentException | IllegalAccessException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }
		this.environmentSettings = environment_settings;
	}

	public void loadExecutionParameters(ExecutionParameters parameters)
	{
		this.parameters = parameters;
	}

	public void loadLogSettings(LogSettings logging)
	{
		this.logging = logging;
	}

	public void loadOutputSettings(OutputSettings dataSettings)
	{
		this.dataSettings = dataSettings;
	}

	public EnvironmentSettings()
	{
		loadComputationSettings(new ComputationSettings());
		dataSettings = new OutputSettings();
		logging = new LogSettings();
		parameters = new ExecutionParameters();
	}

}
