package edu.ucsc.cross.hse.core.obj.config;

import bs.commons.objects.manipulation.XMLParser;
import com.be3short.data.file.general.FileSystemInteractor;

public class LoadConfigurationSettings
{

	public boolean loadSettingsFromFile;

	public boolean loadParametersFromFile;

	public String parameterLocation;

	public String settingsLocation;

	public LoadConfigurationSettings()
	{
		parameterLocation = defaultLocation + "/" + "execution_parameters.xml";
		settingsLocation = defaultLocation + "/" + "execution_parameters.xml";
		loadSettingsFromFile = true;
		loadParametersFromFile = true;
	}

	public static LoadConfigurationSettings get()
	{
		LoadConfigurationSettings settings = new LoadConfigurationSettings();
		try
		{
			LoadConfigurationSettings fileSettings = (LoadConfigurationSettings) XMLParser
			.getObject(loadConfigurationFile);
			if (fileSettings == null)
			{
				throw new Exception();
			} else
			{
				settings = fileSettings;
			}
		} catch (Exception noFile)
		{
			FileSystemInteractor.createOutputFile(configurationLocation, XMLParser.serializeObject(settings));
		}
		return settings;
	}

	public static final String configurationLocation = "config/environment/";

	public static final String defaultLocation = "config/environment/defaultSettings";

	public static final String loadConfigurationFile = configurationLocation + "/config_settings.xml";

}
