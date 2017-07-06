package edu.ucsc.cross.hse.core.procesing.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.be3short.data.compression.CompressionFormat;
import com.be3short.data.compression.DataCompressor;
import com.be3short.data.compression.DataDecompressor;
import com.be3short.data.file.general.FileSystemInteractor;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import com.rits.cloning.Cloner;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.labeling.StringFormatter;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOrganizer;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.object.configuration.DataSettings;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.HybridEnvironment;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingElement;

public class FileExchanger extends ProcessingElement
{

	private static FilePackager packager;
	public static Cloner cloner = new Cloner();

	public FileExchanger(CentralProcessor processor)
	{
		super(processor);
		packager = new FilePackager(processor);
	}

	public void autoStoreData(EnvironmentContent data)
	{
		if (getSettings().getDataSettings().automaticallyStoreResults)
		{
			storeEnvironmentContents(data);
		}
	}

	public void storeEnvironmentContents(EnvironmentContent data)
	{
		String dir = generateDirectoryName();
		String fileName = generateRandomTimeBasedAppendedFileName(data.getActions().getAddress());
		File output = new File(dir, fileName);
		storeEnvironment(output);
	}

	public void storeEnvironment(File file)
	{
		store(file, this.getEnv(), FileContent.ENVIRONMENT, FileContent.DATA, FileContent.SETTINGS);// ,.CONFIGURATION);
	}

	public void store(File file_path, Component component, FileContent... contents)
	{
		packager.storeContents(file_path, component, contents);
	}

	public Component load(File file)
	{
		return load(file, false);
	}

	public Component load(File file, boolean reload_env)
	{
		return load(this.getProcessor(), file, reload_env);
	}

	public static Component load(HybridEnvironment environment, File file, boolean reload_env)
	{

		HashMap<FileContent, Object> contents = packager.loadContents(file, FileContent.values());
		EnvironmentContent env = null;
		try
		{

			env = environment.getContents();
		} catch (Exception noEnvironment)
		{

		}
		Component comp = null;
		try
		{

			env = (EnvironmentContent) contents.get(FileContent.ENVIRONMENT);
		} catch (Exception noEnvironment)
		{

		}
		try
		{
			comp = (Component) contents.get(FileContent.COMPONENT);
			//env.getContents().addComponent(comp);
		} catch (Exception noEnvironment)
		{

		}
		for (FileContent content : contents.keySet())
		{
			try
			{
				switch (content)
				{
				case DATA:
					loadAllData((HashMap<String, HashMap<HybridTime, ?>>) contents.get(FileContent.DATA), comp);
					break;
				case SETTINGS:
					environment.loadSettings((SettingConfigurer) contents.get(FileContent.SETTINGS));
					break;
				}
			} catch (Exception noEnvironment)
			{

			}
		}
		if (reload_env)
		{
			environment.loadContents(env);
		}
		//	System.out.println(XMLParser.serializeObject(comp));
		return comp;
	}

	private static void loadAllData(HashMap<String, HashMap<HybridTime, ?>> data, Component component)
	{
		ArrayList<Data> datas = new ArrayList<Data>();
		datas.addAll(component.getContents().getObjects(State.class, true));
		datas.addAll(component.getContents().getObjects(Data.class, true));
		System.out.println(XMLParser.serializeObject(data));
		for (Data id : component.getContents().getObjects(State.class, true))
		{
			DataOperator.getOperator(id).loadStoredValues(data.get(id.getActions().getAddress()));
		}
	}

	public static SettingConfigurer loadSettings(File file)
	{
		if (file == null)
		{
			file = new File(DataSettings.defaultSettingDirectory + "/" + DataSettings.defaultSettingFileName);
		}
		SettingConfigurer settings = null;
		try
		{
			if (file.exists())
			{
				settings = (SettingConfigurer) XMLParser.getObject(file);
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
				FileSystemOperator.createOutputFile(file, XMLParser.serializeObject(settings));
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

	@SuppressWarnings("unchecked")
	public static <T extends Component> T loadComponent(File file)
	{

		return (T) load(null, file, false);

	}

	public static <T extends Component> void saveComponent(T component, File file)
	{
		saveComponent(component, file, FileContent.values());
	}

	public static <T extends Component> void saveComponent(T component, File file, FileContent... contents)
	{
		packager.storeContents(file, component, contents);

	}

	public String generateDirectoryName()
	{
		String directory = getSettings().getDataSettings().resultAutoStoreDirectory + "/";
		if (getSettings().getDataSettings().createResultSubDirectory)
		{
			directory += this.getComponents().getEnv().getLabels().getClassification() + "/";
		}
		return directory;
	}

	public static String generateRandomTimeBasedAppendedFileName(String file_description)
	{
		String fileName = file_description + "_"
		+ StringFormatter.getCurrentDateString(System.currentTimeMillis() / 1000, "_", false) + "@"
		+ StringFormatter.getAbsoluteHHMMSS("_", false) + ".xml";
		return fileName;
	}
}
