package edu.ucsc.cross.hse.core.processing.data;

import java.io.File;
import java.util.HashMap;

import com.be3short.data.file.general.FileSystemInteractor;
import com.be3short.data.file.xml.XMLParser;

import edu.ucsc.cross.hse.core.object.configuration.ComputationSettings;
import edu.ucsc.cross.hse.core.object.configuration.DataSettings;
import edu.ucsc.cross.hse.core.object.configuration.ExecutionSettings;
import edu.ucsc.cross.hse.core.object.configuration.ConsoleSettings;

public class SettingConfigurer
{

	private HashMap<Class<?>, Object> settings;

	public SettingConfigurer()
	{// settings = new HashMap<Class<?>, Object>();
		initialize();
	}

	private void initialize()
	{
		settings = new HashMap<Class<?>, Object>();
		settings.put(ConsoleSettings.class, new ConsoleSettings());
		settings.put(ComputationSettings.class, new ComputationSettings());
		settings.put(DataSettings.class, new DataSettings());
		settings.put(ExecutionSettings.class, new ExecutionSettings());
	}

	public ComputationSettings getComputationSettings()
	{
		// return computation;
		return (ComputationSettings) settings.get(ComputationSettings.class);
	}

	public ConsoleSettings getConsolePrintSettings()
	{
		return (ConsoleSettings) settings.get(ConsoleSettings.class);
	}

	public DataSettings getDataSettings()
	{
		return (DataSettings) settings.get(DataSettings.class);
		// return data;
	}

	public ExecutionSettings getExecutionSettings()
	{
		return (ExecutionSettings) settings.get(ExecutionSettings.class);
		// return trial;
	}

	public void loadSettings(Object... setting)
	{
		for (Object settinG : setting)
		{
			settings.put(settinG.getClass(), settinG);
		}
	}

	public void setSettings(SettingConfigurer setting)
	{
		for (Object set : SettingConfigurer.getSettingsMap(setting).values())
		{
			loadSettings(set);
		}
	}

	public <T> T getSetting(Class<T> setting_class)
	{
		T returnSetting = null;
		if (settings.containsKey(setting_class))
		{
			returnSetting = setting_class.cast(settings.get(setting_class));
		}
		return returnSetting;
	}

	public static HashMap<Class<?>, Object> getSettingsMap(SettingConfigurer setting)
	{
		return setting.settings;
	}

	/*
	 * Load settings from a file
	 */
	public void loadSettingsFromXMLFile(File file)
	{

		SettingConfigurer loaded = null;
		if (file == null)
		{
			loaded = SettingConfigurer.loadDefaultSettingsFile();
		} else
		{
			loaded = SettingConfigurer.loadXMLSettings(file);
		}
		for (Object set : SettingConfigurer.getSettingsMap(loaded).values())
		{
			loadSettings(set);
		}
	}

	public static SettingConfigurer getSettingsFromFile()
	{
		return SettingConfigurer.loadDefaultSettingsFile();
	}

	/*
	 * Save the settings to a file
	 */
	public void saveSettingsToXMLFile(File file)
	{
		FileSystemInteractor.createOutputFile(file, XMLParser.serializeObject(this));
	}

	/*
	 * Loads the default settings from the default settings file
	 */
	public static SettingConfigurer loadDefaultSettingsFile()
	{
		return loadXMLSettings(
		new File(DataSettings.defaultSettingDirectory + "/" + DataSettings.defaultSettingFileName));
	}

	public static SettingConfigurer loadXMLSettings(File file)
	{
		SettingConfigurer settings = null;
		if (file.exists())
		{
			settings = (SettingConfigurer) XMLParser.getObject(file);
		} else
		{
			settings = new SettingConfigurer();
			saveXMLSettings(file, settings);
		}
		if (settings == null)
		{
			settings = new SettingConfigurer();
		}
		return settings;

	}

	public static void saveXMLSettings(File file, SettingConfigurer settings)
	{
		try
		{
			FileSystemInteractor.createOutputFile(file, XMLParser.serializeObject(settings));
		} catch (Exception badFile)
		{
			badFile.printStackTrace();
		}
	}
}
