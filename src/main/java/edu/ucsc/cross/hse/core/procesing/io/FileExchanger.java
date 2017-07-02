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
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.object.configuration.DataSettings;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingElement;

public class FileExchanger extends ProcessingElement
{

	private FilePackager packager;
	public static Cloner cloner = new Cloner();

	public FileExchanger(CentralProcessor processor)
	{
		super(processor);
		packager = new FilePackager(processor);
	}

	public void autoStoreData(EnvironmentContent data)
	{
		for (Component comp : getEnv().getContents().getComponents(true))
		{
			// comp.getConfigurer().setEnvironment(null);
			// comp.getConfigurer().resetHierarchy();
		}
		if (getSettings().getDataSettings().automaticallyStoreResults)
		{
			storeEnvironmentContents(data);
		}
	}

	public void storeEnvironmentContents(EnvironmentContent data)
	{
		String directory = getSettings().getDataSettings().resultAutoStoreDirectory + "/";
		if (getSettings().getDataSettings().createResultSubDirectory)
		{
			directory += data.getLabels().getClassification() + "/";
		}

		String fileName = data.getLabels().getName() + "_"
		+ StringFormatter.getCurrentDateString(System.currentTimeMillis() / 1000, "_", false) + "@"
		+ StringFormatter.getAbsoluteHHMMSS("_", false) + ".xml";
		String out = XMLParser.serializeObject(processor);
		FileSystemOperator.createOutputFile(new File(directory, fileName), out);
		// FileSystemOperator.createOutputFile(new File(directory, "zipped_" +
		// fileName), Zipper.compress(out).toString());
		FileOutputStream fileStream = null;
		try
		{

			try
			{
				fileStream = new FileOutputStream(new File(directory, "zipped.xml.gz"));
				fileStream.write(DataCompressor.compressDataGZip(out));
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally
		{
			try
			{
				fileStream.close();
			} catch (Exception e)
			{
				e.printStackTrace();
				/* We should probably delete the file now? */ }
		}
	}

	public void storeEnvironment(File file)
	{
		String directory = getSettings().getDataSettings().resultAutoStoreDirectory + "/";
		if (getSettings().getDataSettings().createResultSubDirectory)
		{
			directory += this.getComponents().getEnv().getLabels().getClassification() + "/";
		}
		String fileName = this.getComponents().getEnv().getLabels().getName() + "_"
		+ StringFormatter.getCurrentDateString(System.currentTimeMillis() / 1000, "_", false) + "@"
		+ StringFormatter.getAbsoluteHHMMSS("_", false) + ".hse";
		store(file, this.getEnv(), FileContent.ENVIRONMENT_CONFIGURATION, FileContent.ALL_DATA, FileContent.SETTINGS);// ,.CONFIGURATION);

	}

	private void store(File file_path, Component component, FileContent... contents)
	{
		packager.storeContents(file_path, component, contents);

	}

	public void load(File file)
	{

		HashMap<FileContent, Object> contents = packager.loadContents(file, FileContent.values());
		EnvironmentContent env = (EnvironmentContent) contents.get(FileContent.ENVIRONMENT_CONFIGURATION);
		for (FileContent content : contents.keySet())
		{
			switch (content)
			{
			case ALL_DATA:
				loadAllData((HashMap<String, HashMap<HybridTime, ?>>) contents.get(FileContent.ALL_DATA), env);
				break;
			case SETTINGS:
				this.setSettings((SettingConfigurer) contents.get(FileContent.SETTINGS));
				break;

			}
		}
		this.processor.loadContents(env);
	}

	private void loadAllData(HashMap<String, HashMap<HybridTime, ?>> data, EnvironmentContent env)
	{
		for (Data id : env.getContents().getObjects(Data.class, true))
		{
			DataOperator.getOperator(id).setStoredHybridValues(data.get(id.getActions().getAddress()));
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
	public static <T extends Component> T loadComponent(String file_directory, String file_name)
	{
		return loadComponent(file_directory, file_name, false);

	}

	@SuppressWarnings("unchecked")
	public static <T extends Component> T loadComponent(String file_directory, String file_name, boolean compress)
	{
		T component = (T) XMLParser.getObject(new File(file_directory, file_name));
		for (Component componen : component.getContents().getComponents(true))
		{
			ComponentOperator.getOperator(componen).setInitialized(null);
			// try
			// {
			// Data<T> data = (Data) componen;
			// data.initializeValue();
			// } catch (Exception e)
			// {
			//
			// }
		}
		return component;
	}

	public static <T extends Component> void saveComponent(T component, String file_directory, String file_name)
	{
		saveComponent(component, file_directory, file_name, CompressionFormat.NONE);
	}

	public static <T extends Component> void saveComponent(T component, String file_directory, String file_name,
	CompressionFormat compression)
	{
		String adjustedFileName = file_name;
		Object clonedComponent = ObjectCloner.xmlClone(component);
		String serializedComponent = XMLParser.serializeObject(component);
		byte[] byteOut = null;
		switch (compression)
		{
		case GZIP:
			byteOut = DataCompressor.compressDataGZip(serializedComponent);
			adjustedFileName += ".gz";
			break;
		default:
			byteOut = serializedComponent.getBytes();
			break;
		}
		FileSystemInteractor.createOutputFile(file_directory, adjustedFileName, byteOut);// clonedComponent));

		// ComponentOperator.getConfigurer(component).saveComponentToFile(file_directory,
		// file_name);
	}

	public static String generateRandomTimeBasedAppendedFileName(String file_description)
	{
		String fileName = file_description + "_"
		+ StringFormatter.getCurrentDateString(System.currentTimeMillis() / 1000, "_", false) + "@"
		+ StringFormatter.getAbsoluteHHMMSS("_", false) + ".xml";
		return fileName;
	}
}
