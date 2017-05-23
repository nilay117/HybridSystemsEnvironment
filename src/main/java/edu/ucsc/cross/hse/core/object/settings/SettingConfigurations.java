package edu.ucsc.cross.hse.core.object.settings;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import bs.commons.objects.manipulation.XMLParser;

public class SettingConfigurations
{

	private HashMap<Class<?>, Object> settings;

	private SettingConfigurations()
	{
		initialize();
	}

	private void initialize()
	{
		settings = new HashMap<Class<?>, Object>();
		settings.put(PrintSettings.class, new PrintSettings());
		settings.put(ComputationSettings.class, new ComputationSettings());
		settings.put(DataSettings.class, new DataSettings());
		settings.put(ExecutionSettings.class, new ExecutionSettings());
	}

	public static SettingConfigurations loadSettings()
	{
		return loadSettings(DataSettings.defaultSettingDirectory, DataSettings.defaultSettingFileName);
	}

	public static SettingConfigurations loadSettings(String directory, String file_name)
	{
		SettingConfigurations settings = null;
		try
		{
			if (new File(directory, file_name).exists())
			{
				settings = (SettingConfigurations) XMLParser.getObject(new File(directory, file_name));
			} else
			{
				throw new IOException();
			}
		} catch (Exception badDefault)
		{
			badDefault.printStackTrace();
			settings = new SettingConfigurations();
			try
			{
				// FileSystemOperator.createOutputFile(new File(directory,
				// file_name),
				// XMLParser.serializeObject(settings));
			} catch (Exception badFile)
			{
				badFile.printStackTrace();
			}
		}
		if (settings == null)
		{
			settings = new SettingConfigurations();
		}
		return settings;

	}

	public ComputationSettings computation()
	{
		// return computation;
		return (ComputationSettings) settings.get(ComputationSettings.class);
	}

	public PrintSettings io()
	{
		return (PrintSettings) settings.get(PrintSettings.class);
	}

	public static void setComputationSettings(SettingConfigurations settings, ComputationSettings computation)
	{
		settings.settings.put(ComputationSettings.class, computation);
	}

	public DataSettings getData()
	{
		return (DataSettings) settings.get(DataSettings.class);
		// return data;
	}

	public static void setDataSettings(SettingConfigurations settings, DataSettings data)
	{
		settings.settings.put(DataSettings.class, data);
	}

	public ExecutionSettings trial()
	{
		return (ExecutionSettings) settings.get(ExecutionSettings.class);
		// return trial;
	}

	public static void setTrialSettings(SettingConfigurations settings, ExecutionSettings simulation)
	{
		settings.settings.put(ExecutionSettings.class, simulation);
	}

	public <T> T setting(Class<T> setting_class)
	{
		T returnSetting = null;
		if (settings.containsKey(setting_class))
		{
			returnSetting = setting_class.cast(settings.get(setting_class));
		}
		return returnSetting;
	}

}
