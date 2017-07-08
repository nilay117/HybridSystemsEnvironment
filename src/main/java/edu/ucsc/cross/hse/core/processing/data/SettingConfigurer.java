package edu.ucsc.cross.hse.core.processing.data;

import java.io.File;
import java.util.HashMap;

import com.be3short.data.file.general.FileSystemInteractor;
import com.be3short.data.file.xml.XMLParser;

import edu.ucsc.cross.hse.core.object.configuration.ComputationSettings;
import edu.ucsc.cross.hse.core.object.configuration.DataSettings;
import edu.ucsc.cross.hse.core.object.configuration.ExecutionSettings;
import edu.ucsc.cross.hse.core.object.configuration.PrintSettings;
import edu.ucsc.cross.hse.core.procesing.io.FileContent;
import edu.ucsc.cross.hse.core.procesing.io.FileProcessor;

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
		settings.put(PrintSettings.class, new PrintSettings());
		settings.put(ComputationSettings.class, new ComputationSettings());
		settings.put(DataSettings.class, new DataSettings());
		settings.put(ExecutionSettings.class, new ExecutionSettings());
	}

	public ComputationSettings getComputationSettings()
	{
		// return computation;
		return (ComputationSettings) settings.get(ComputationSettings.class);
	}

	public PrintSettings getConsolePrintSettings()
	{
		return (PrintSettings) settings.get(PrintSettings.class);
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
			loaded = FileProcessor.loadXMLSettings();
		} else
		{
			loaded = FileProcessor.loadXMLSettings(file);
		}
		for (Object set : SettingConfigurer.getSettingsMap(loaded).values())
		{
			loadSettings(set);
		}
	}

	public static SettingConfigurer getSettingsFromFile()
	{
		return FileProcessor.loadXMLSettings();
	}

	/*
	 * Save the settings to a file
	 */
	public void saveSettingsToXMLFile(File file)
	{
		FileSystemInteractor.createOutputFile(file, XMLParser.serializeObject(this));
	}
}
