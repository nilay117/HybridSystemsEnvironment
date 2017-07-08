package edu.ucsc.cross.hse.core.procesing.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import com.be3short.data.compression.DataCompressor;
import com.be3short.data.compression.DataDecompressor;
import com.be3short.data.file.general.FileSystemInteractor;
import com.be3short.data.file.xml.XMLParser;
import com.be3short.data.serialization.ObjectSerializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.labeling.StringFormatter;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.FullComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.object.configuration.DataSettings;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingConnector;

public class FileProcessor extends ProcessingConnector
{

	private Kryo kryo = new Kryo();

	public FileProcessor(CentralProcessor processor)
	{
		super(processor);
		initialize();
	}

	private void initialize()
	{
		kryo = new Kryo();
	}

	public void store(File location, Component component, FileContent... contents)
	{
		store(location, this.getProcessor(), component, contents);
	}

	public static void store(File location, HybridEnvironment env, Component component, FileContent... contents)
	{
		FileSystemInteractor.checkDirectory(location.getAbsolutePath(), true);
		createFiles(location, env, component, contents);
		consolidateFile(location);

	}

	private static void createFiles(File location, Component component, FileContent... contents)
	{
		createFiles(location, null, component, contents);
	}

	private static void createFiles(File location, HybridEnvironment env, Component component, FileContent... contents)
	{
		for (FileContent content : contents)
		{
			try
			{
				switch (content)
				{
				case DATA:
					storeData(location, component);
					break;
				case SETTINGS:
					storeSettings(location, env.getSettings());
					break;
				case COMPONENT:
					storeComponent(location, component);
					break;
				}
			} catch (Exception fileCreationFail)
			{

			}
		}
	}

	private static void consolidateFile(File location)
	{
		try
		{
			DataCompressor.zipDirectory(location.getAbsolutePath(), location.getAbsolutePath() + ".hse");
			FileUtils.deleteDirectory(location);
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void storeData(File location, Component component)
	{
		HashMap<String, Object> data = new HashMap<String, Object>(); // new
		for (Data dat : component.component().getContent().getData(true))
		{
			data.put(dat.component().getAddress(), dat.component().getStoredValues());
			//ObjectSerializer.store(location.getAbsolutePath() + "/" + dat.component().getAddress(),
			//dat.component().getStoredValues());//location.getAbsolutePath());
		}
		ObjectSerializer.store(data, location.getAbsolutePath());

	}

	private static void storeSettings(File location, SettingConfigurer settings)
	{
		String xmlSettings = XMLParser.serializeObject(settings);
		byte[] compressed = DataCompressor.compressDataGZip(xmlSettings);

		ObjectSerializer.store(location.getAbsolutePath() + "/" + settings.getClass().getName(), compressed);
	}

	private static void storeComponent(File location, Component component)
	{
		String xmlSettings = XMLParser.serializeObject(FullComponentOperator.getOperator(component).getNewInstance());
		byte[] compressed = DataCompressor.compressDataGZip(xmlSettings);
		String suffix = component.component().getLabels().getFullDescription() + " Component";
		if (FieldFinder.containsSuper(component, EnvironmentContent.class))
		{
			// suffix = component.component().getLabels().getFullDescription() +
			// " " + component.component().getAddress();
		}
		System.out.println(suffix);
		ObjectSerializer.store(location.getAbsolutePath() + "/" + suffix, compressed);
	}

	public HashMap<FileContent, Object> loadContents(File location, FileContent... contents)
	{
		ArrayList<FileContent> contentz = new ArrayList<FileContent>();
		contentz.addAll(Arrays.asList(contents));
		HashMap<FileContent, Object> loadedContent = new HashMap<FileContent, Object>();
		try
		{
			ZipInputStream in = new ZipInputStream(new FileInputStream(location));
			ArrayList<File> filess = new ArrayList<File>();
			ZipEntry entry = in.getNextEntry();
			HashMap<String, HashMap<HybridTime, ?>> datas = new HashMap<String, HashMap<HybridTime, ?>>();
			Input input = new Input(in);

			while (entry != null)
			{
				try
				{
					FileContent inputElement = FileContent.getFileContentType(entry.getName());

					if (contentz.contains(inputElement))
					{
						// this.getConsole().print("Loading data from file : " +
						// entry.getName());
						Object readIn = kryo.readClassAndObject(input);
						//FileContent inputElement = FileContent.getFileContentType(entry.getName());
						this.getConsole().print("Loading data from file : " + entry.getName() + inputElement.name());
						//if (contentz.contains(inputElement))

						switch (inputElement)
						{

						case DATA:
							this.getConsole().print("Loading data file : " + entry.getName() + inputElement.name());
							datas.put(entry.getName(), (HashMap<HybridTime, ?>) readIn);
							break;
						case SETTINGS:
							loadedContent.put(FileContent.SETTINGS, ((SettingConfigurer) XMLParser
							.getObjectFromString((String) DataDecompressor.decompressDataGZipString((byte[]) readIn))));
							break;
						case COMPONENT:
							loadedContent.put(FileContent.COMPONENT, ((Component) XMLParser
							.getObjectFromString((String) DataDecompressor.decompressDataGZipString((byte[]) readIn))));
							break;

						}
					}
				} catch (Exception e)
				{

				}
				entry = in.getNextEntry();

			}
			if (datas.size() > 0)
			{
				if (contentz.contains(FileContent.DATA))
				{
					loadedContent.put(FileContent.DATA, datas);
				}
			}
		} catch (Exception ee)
		{

		}
		return loadedContent;
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
			FileSystemOperator.createOutputFile(file, XMLParser.serializeObject(settings));
		} catch (Exception badFile)
		{
			badFile.printStackTrace();
		}
	}

	public static void saveXMLSettingsWithAdditions(File file, Object... additions)
	{
		try
		{
			SettingConfigurer settings = new SettingConfigurer();
			settings.loadSettings(additions);
			FileSystemOperator.createOutputFile(file, XMLParser.serializeObject(settings));
		} catch (Exception badFile)
		{
			badFile.printStackTrace();
		}
	}

	public static Component load(HybridEnvironment environment, File file, FileContent... content)
	{

		HashMap<FileContent, Object> contents = FileExchanger.packager.loadContents(file, content);
		Component component = getComponent(contents);
		for (FileContent conten : contents.keySet())
		{
			try
			{
				switch (conten)
				{
				case DATA:
					loadAllData(contents, component);
					break;
				case SETTINGS:
					environment.getSettings().setSettings((SettingConfigurer) contents.get(FileContent.SETTINGS));
					break;
				}
			} catch (Exception noEnvironment)
			{
				noEnvironment.printStackTrace();
			}
		}
		return component;
	}

	public static SettingConfigurer loadXMLSettings()
	{
		return FileProcessor
		.loadXMLSettings(new File(DataSettings.defaultSettingDirectory + "/" + DataSettings.defaultSettingFileName));
	}

	public static String generateRandomTimeBasedAppendedFileName(String file_description)
	{
		String fileName = file_description + "_"
		+ StringFormatter.getCurrentDateString(System.currentTimeMillis() / 1000, "_", false) + "@"
		+ StringFormatter.getAbsoluteHHMMSS("_", false) + ".xml";
		return fileName;
	}

	private static void loadAllData(HashMap<FileContent, Object> contents, Component component)
	{
		if (component != null)
		{
			HashMap<String, HashMap<HybridTime, ?>> data = (HashMap<String, HashMap<HybridTime, ?>>) contents
			.get(FileContent.DATA);

			for (Data id : component.component().getContent().getData(true))
			{
				DataOperator.getOperator(id).loadStoredValues(data.get(id.component().getAddress()));
			}
		}
	}

	private static Component getComponent(HashMap<FileContent, Object> contents)
	{
		Component component = null;
		try
		{
			component = (Component) contents.get(FileContent.COMPONENT);
		} catch (Exception noEnvironment)
		{

		}
		return component;
	}

	public static <T extends Component> void saveComponent(T component, File file, FileContent... contents)
	{
		store(file, null, component, contents);

	}

	@SuppressWarnings("unchecked")
	public static <T extends Component> T load(File file, FileContent... contents)
	{
		return (T) load(null, file, contents);
	}
}
