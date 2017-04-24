package edu.ucsc.cross.hybrid.env.core.settings;

import java.io.File;
import java.io.IOException;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.manipulation.XMLParser;

public class SettingCollection
{

	private PrintSettings printSettings;
	private ComputationSettings computation;
	private DataSettings data;
	private ExecutionSettings trial;

	private SettingCollection()
	{
		printSettings = new PrintSettings();
		computation = new ComputationSettings();
		data = new DataSettings();
		trial = new ExecutionSettings();
	}

	public static SettingCollection getSettings()
	{
		SettingCollection settings = null;
		try
		{
			if (new File(DataSettings.defaultSettingDirectory, DataSettings.defaultSettingFileName)
			.exists())
			{
				settings = (SettingCollection) XMLParser.getObject(
				new File(DataSettings.defaultSettingDirectory, DataSettings.defaultSettingFileName));
			} else
			{
				throw new IOException();
			}
		} catch (Exception badDefault)
		{
			badDefault.printStackTrace();
			settings = new SettingCollection();
			try
			{
				FileSystemOperator.createOutputFile(
				new File(DataSettings.defaultSettingDirectory, DataSettings.defaultSettingFileName),
				XMLParser.serializeObject(settings));
			} catch (Exception badFile)
			{
				badFile.printStackTrace();
			}
		}
		if (settings == null)
		{
			settings = new SettingCollection();
		}
		return settings;

	}

	public ComputationSettings computation()
	{
		return computation;
	}

	public PrintSettings io()
	{
		return printSettings;
	}

	public static void setComputationSettings(SettingCollection settings, ComputationSettings computation)
	{
		settings.computation = computation;
	}

	public DataSettings getData()
	{
		return data;
	}

	public static void setDataSettings(SettingCollection settings, DataSettings data)
	{
		settings.data = data;
	}

	public ExecutionSettings trial()
	{
		return trial;
	}

	public static void setTrialSettings(SettingCollection settings, ExecutionSettings simulation)
	{
		settings.trial = simulation;
	}

	public static enum SimulationSettingType
	{
		COMPUTATION(
			"Computation",
			ComputationSettings.class),
		DATA(
			"Data",
			DataSettings.class),
		TRIAL(
			"Trial",
			ExecutionSettings.class);
		//		TRIAL_CONFIG(
		//			"TrialConfig",
		//			TrialConfiguration.class);

		public final String name;
		public final Class<?> settingClass;

		private <T> SimulationSettingType(String name, Class<T> setting_class)
		{
			settingClass = setting_class;
			this.name = name;
		}
	}

}
