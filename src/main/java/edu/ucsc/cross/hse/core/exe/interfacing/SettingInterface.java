package edu.ucsc.cross.hse.core.exe.interfacing;

import edu.ucsc.cross.hse.core.obj.config.ExecutionSettings;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.obj.config.DataSettings;
import edu.ucsc.cross.hse.core.obj.config.ExecutionParameters;
import edu.ucsc.cross.hse.core.obj.config.PrintSettings;


@Loggable(Loggable.TRACE)
public class SettingInterface
{

	private ExecutionSettings environmentSettings;
	private ExecutionParameters executionParameters;
	private DataSettings dataSettings;
	private PrintSettings logging;

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
		setEnvironmentSettings(new ExecutionSettings());
		executionParameters = new ExecutionParameters();
		dataSettings = new DataSettings();
		logging = new PrintSettings();
	}

	public ExecutionSettings getEnvironmentSettings()
	{
		return environmentSettings;
	}

	public void setEnvironmentSettings(ExecutionSettings environment_settings)
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

	public PrintSettings getLogging()
	{
		return logging;
	}

	public void setLogging(PrintSettings logging)
	{
		this.logging = logging;
	}
}
