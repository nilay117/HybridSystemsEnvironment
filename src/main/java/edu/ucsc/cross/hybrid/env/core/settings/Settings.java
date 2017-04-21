package edu.ucsc.cross.hybrid.env.core.settings;

import java.io.File;
import java.io.IOException;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.manipulation.XMLParser;

public class Settings
{

	private PrintOutSettings io;
	private ComputationSettings computation;
	private DataStorageSettings data;
	private TrialSettings trial;

	private Settings()
	{
		io = new PrintOutSettings();
		computation = new ComputationSettings();
		data = new DataStorageSettings();
		trial = new TrialSettings();
	}

	public static Settings getSettings()
	{
		Settings settings = null;
		try
		{
			if (new File(DataStorageSettings.defaultSettingDirectory, DataStorageSettings.defaultSettingFileName)
			.exists())
			{
				settings = (Settings) XMLParser.getObject(
				new File(DataStorageSettings.defaultSettingDirectory, DataStorageSettings.defaultSettingFileName));
			} else
			{
				throw new IOException();
			}
		} catch (Exception badDefault)
		{
			badDefault.printStackTrace();
			settings = new Settings();
			try
			{
				FileSystemOperator.createOutputFile(
				new File(DataStorageSettings.defaultSettingDirectory, DataStorageSettings.defaultSettingFileName),
				XMLParser.serializeObject(settings));
			} catch (Exception badFile)
			{
				badFile.printStackTrace();
			}
		}
		if (settings == null)
		{
			settings = new Settings();
		}
		return settings;

	}

	public ComputationSettings computation()
	{
		return computation;
	}

	public PrintOutSettings io()
	{
		return io;
	}

	public static void setComputationSettings(Settings settings, ComputationSettings computation)
	{
		settings.computation = computation;
	}

	public DataStorageSettings getData()
	{
		return data;
	}

	public static void setDataSettings(Settings settings, DataStorageSettings data)
	{
		settings.data = data;
	}

	public TrialSettings trial()
	{
		return trial;
	}

	public static void setTrialSettings(Settings settings, TrialSettings simulation)
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
			DataStorageSettings.class),
		TRIAL(
			"Trial",
			TrialSettings.class);
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
