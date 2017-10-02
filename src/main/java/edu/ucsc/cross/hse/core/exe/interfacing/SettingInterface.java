package edu.ucsc.cross.hse.core.exe.interfacing;

import edu.ucsc.cross.hse.core.obj.config.ComputationSettings;
import edu.ucsc.cross.hse.core.obj.config.DataSettings;
import edu.ucsc.cross.hse.core.obj.config.ExecutionParameters;
import edu.ucsc.cross.hse.core.obj.config.LogSettings;

public class SettingInterface
{

	private ComputationSettings environmentSettings;
	private ExecutionParameters executionParameters;
	private DataSettings dataSettings;
	private LogSettings logging;

	public DataSettings getDataSettings()
	{
		return dataSettings;
	}

	public void setDataSettings(DataSettings dataSettings)
	{
		this.dataSettings = dataSettings;
	}

	public SettingInterface()
	{
		setEnvironmentSettings(new ComputationSettings());
		executionParameters = new ExecutionParameters();
		dataSettings = new DataSettings();
		logging = new LogSettings();
	}

	public ComputationSettings getEnvironmentSettings()
	{
		return environmentSettings;
	}

	public void setEnvironmentSettings(ComputationSettings environment_settings)
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

	public ExecutionParameters getExecutionParameters()
	{
		return executionParameters;
	}

	public void setExecutionParameters(ExecutionParameters executionParameters)
	{
		this.executionParameters = executionParameters;
	}

	public LogSettings getLogging()
	{
		return logging;
	}

	public void setLogging(LogSettings logging)
	{
		this.logging = logging;
	}
}
