package edu.ucsc.cross.hse.core.procesing.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.be3short.data.compression.CompressionFormat;
import com.be3short.data.compression.DataCompressor;
import com.be3short.data.compression.DataDecompressor;
import com.be3short.data.file.general.FileSystemInteractor;
import com.rits.cloning.Cloner;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.labeling.StringFormatter;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOrganizer;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.object.configuration.DataSettings;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingElement;

public class FileExchanger extends ProcessingElement
{

	public static Cloner cloner = new Cloner();

	public FileExchanger(CentralProcessor processor)
	{
		super(processor);
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

	public void storeEnvironment()
	{
		String directory = getSettings().getDataSettings().resultAutoStoreDirectory + "/";
		if (getSettings().getDataSettings().createResultSubDirectory)
		{
			directory += this.getComponents().getEnv().getLabels().getClassification() + "/";
		}
		String fileName = this.getComponents().getEnv().getLabels().getName() + "_"
		+ StringFormatter.getCurrentDateString(System.currentTimeMillis() / 1000, "_", false) + "@"
		+ StringFormatter.getAbsoluteHHMMSS("_", false) + ".hse";
		store(directory + "/" + fileName, this.getEnv(), FileComponent.CONFIGURATION);

	}

	private void store(String file_path, Component component, FileComponent... contents)
	{
		SaveFile file = new SaveFile(null);
		for (FileComponent content : contents)
		{
			file.fileComponents.put(content, getContentString(component, content));
		}
		file.data = getDataByteMap(component);
		String output = XMLParser.serializeObject(file);
		FileSystemOperator.createOutputFile(file_path, output);
	}

	public void load(File file)
	{
		SaveFile savedFile = new SaveFile(FileSystemOperator.getFileContentsAsString(file));
		try
		{
			//			String content = DataDecompressor
			//			.decompressDataGZipString(savedFile.fileComponents.get(FileComponent.COMPONENT));
			String content = DataDecompressor
			.decompressDataGZipString(savedFile.fileComponents.get(FileComponent.CONFIGURATION));
			EnvironmentContent envContent = (EnvironmentContent) XMLParser.getObjectFromString(content);
			loadData(savedFile, envContent);
			this.processor.loadContents(envContent);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private byte[] getContentString(Component component, FileComponent component_type)
	{
		byte[] content = null;
		switch (component_type)
		{
		case COMPONENT:
			content = (DataCompressor.compressDataGZip(XMLParser.serializeObject(component)));
			break;
		case SETTINGS:
			content = (DataCompressor.compressDataGZip(XMLParser.serializeObject(this.getSettings())));
			break;
		case CONFIGURATION:
			content = (DataCompressor
			.compressDataGZip(XMLParser.serializeObject(ComponentOperator.getOperator(component).getNewInstance())));
			break;
		}
		return content;
	}

	private HashMap<String, byte[]> getDataByteMap(Component component)
	{
		HashMap<String, byte[]> dataBytes = new HashMap<String, byte[]>();
		for (Data data : component.getContents().getObjects(Data.class, true))

		{
			SystemConsole.print(data.getLabels().getFullDescription());
			dataBytes.put(data.getActions().getAddress(),
			DataCompressor.compressDataGZip(XMLParser.serializeObject(data.getActions().getStoredHybridValues())));
		}
		return dataBytes;
	}

	private void loadData(SaveFile saved_file, Component component)
	{
		HashMap<String, byte[]> dataBytes = saved_file.data;
		HashMap<String, Data> dataMap = ComponentOperator.getOperator(component).getDataLinks();
		for (String data : dataBytes.keySet())
		{
			startDataThread(dataBytes.get(data), dataMap, data);
		}
	}

	private void startDataThread(byte[] data_bytes, HashMap<String, Data> data_map, String data)
	{
		Runnable exe = loadDataThread(data_bytes, data_map, data);
		Thread thread = new Thread(exe);
		thread.start();
	}

	private Runnable loadDataThread(byte[] data_bytes, HashMap<String, Data> data_map, String data)
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{
				HashMap<HybridTime, ?> unzippedData = (HashMap<HybridTime, ?>) XMLParser
				.getObjectFromString(DataDecompressor.decompressDataGZipString(data_bytes));
				//data_map.put(data, unzippedData);
				//SystemConsole.print(unzippedData.toString());
				DataOperator.getOperator(data_map.get(data)).setStoredHybridValues(unzippedData);
			}

		};
		return task;
	}

	public static SettingConfigurer loadSettings()
	{
		return loadSettings(DataSettings.defaultSettingDirectory, DataSettings.defaultSettingFileName);
	}

	public static SettingConfigurer loadSettings(String directory, String file_name)
	{
		SettingConfigurer settings = null;
		try
		{
			if (new File(directory, file_name).exists())
			{
				settings = (SettingConfigurer) XMLParser.getObject(new File(directory, file_name));
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
				FileSystemOperator.createOutputFile(new File(directory, file_name),
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
