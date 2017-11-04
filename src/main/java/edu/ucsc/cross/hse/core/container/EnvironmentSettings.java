package edu.ucsc.cross.hse.core.container;

import edu.ucsc.cross.hse.core.setting.ComputationSettings;
import edu.ucsc.cross.hse.core.setting.LogSettings;
import edu.ucsc.cross.hse.core.setting.OutputSettings;

public class EnvironmentSettings
{

	private ComputationSettings environmentSettings;
	private OutputSettings dataSettings;
	private LogSettings logging;

	public void setDataSettings(OutputSettings dataSettings)
	{
		this.dataSettings = dataSettings;
	}

	public EnvironmentSettings()
	{
		loadExecutionSettings(new ComputationSettings());
		dataSettings = new OutputSettings();
		logging = new LogSettings();
	}

	public ComputationSettings getExecutionSettings()
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

	public void loadSettings(EnvironmentSettings settings)
	{
		this.environmentSettings = settings.environmentSettings;
		this.dataSettings = settings.dataSettings;
		this.logging = settings.logging;
	}

	public void loadLogSettings(LogSettings logging)
	{
		this.logging = logging;
	}

	public void loadExecutionSettings(ComputationSettings environment_settings)
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
}
