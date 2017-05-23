package edu.ucsc.cross.hybrid.env.core.processing;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hybrid.env.core.settings.ComputationSettings;
import edu.ucsc.cross.hybrid.env.core.settings.DataSettings;
import edu.ucsc.cross.hybrid.env.core.settings.ExecutionSettings;
import edu.ucsc.cross.hybrid.env.core.settings.PrintSettings;

public class SettingConfigurer
{

	private PrintSettings printSettings;
	private ComputationSettings computation;
	private DataSettings data;
	private ExecutionSettings trial;
	private HashMap<Class<?>, Object> settings;

	private SettingConfigurer()
	{
		//		printSettings = new PrintSettings();
		//		computation = new ComputationSettings();
		//		data = new DataSettings();
		//		trial = new ExecutionSettings();
		initializeSettingMap();
	}

	private void initializeSettingMap()
	{
		settings = new HashMap<Class<?>, Object>();
		//		settings.put(CoreSetting.PRINT, printSettings);
		//		settings.put(CoreSetting.COMPUTATION, computation);
		//		settings.put(CoreSetting.DATA, data);
		//		settings.put(CoreSetting.EXECUTION, trial);
		settings.put(PrintSettings.class, new PrintSettings());
		settings.put(ComputationSettings.class, new ComputationSettings());
		settings.put(DataSettings.class, new DataSettings());
		settings.put(ExecutionSettings.class, new ExecutionSettings());

	}

	public static SettingConfigurer getSettings()
	{
		SettingConfigurer settings = null;
		try
		{
			if (new File(DataSettings.defaultSettingDirectory, DataSettings.defaultSettingFileName).exists())
			{
				settings = (SettingConfigurer) XMLParser
				.getObject(new File(DataSettings.defaultSettingDirectory, DataSettings.defaultSettingFileName));
			} else
			{
				throw new IOException();
			}
		} catch (Exception badDefault)
		{
			badDefault.printStackTrace();
			settings = new SettingConfigurer();
			try
			{
				FileSystemOperator
				.createOutputFile(new File(DataSettings.defaultSettingDirectory, DataSettings.defaultSettingFileName),
				XMLParser.serializeObject(settings));
			} catch (Exception badFile)
			{
				badFile.printStackTrace();
			}
		}
		if (settings == null)
		{
			settings = new SettingConfigurer();
		}
		return settings;

	}

	public ComputationSettings computation()
	{
		//return computation;
		return (ComputationSettings) settings.get(ComputationSettings.class);
	}

	public PrintSettings io()
	{
		return (PrintSettings) settings.get(PrintSettings.class);
	}

	public static void setComputationSettings(SettingConfigurer settings, ComputationSettings computation)
	{
		settings.computation = computation;
	}

	public DataSettings getData()
	{
		return (DataSettings) settings.get(DataSettings.class);
		//return data;
	}

	public static void setDataSettings(SettingConfigurer settings, DataSettings data)
	{
		settings.data = data;
	}

	public ExecutionSettings trial()
	{
		return (ExecutionSettings) settings.get(ExecutionSettings.class);
		//return trial;
	}

	public static void setTrialSettings(SettingConfigurer settings, ExecutionSettings simulation)
	{
		settings.trial = simulation;
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
