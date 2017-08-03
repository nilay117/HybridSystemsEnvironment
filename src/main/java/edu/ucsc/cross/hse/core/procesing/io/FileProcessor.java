package edu.ucsc.cross.hse.core.procesing.io;

import java.io.File;
import java.io.FileInputStream;
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

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentWorker;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

public class FileProcessor extends ProcessorAccess
{

	public static Boolean multiThread = true;
	private static Kryo kryo = new Kryo(); // Optimized serializer

	/*
	 * Constructor that links to the processor interface
	 */
	public FileProcessor(CentralProcessor processor)
	{
		super(processor);

	}

	/*
	 * Store data from component at location. Data to store is specified by
	 * contents
	 */
	public static void store(File location, Component component, FileContent... contents)
	{
		// component.component().getSettings();
		FileSystemInteractor.checkDirectory(location.getAbsolutePath(), true);
		createFiles(location, component, contents);
		consolidateFile(location);

	}

	public static HashMap<FileContent, Object> loadContents(File location, FileContent... contents)
	{
		HashMap<FileContent, Object> readContent = null;
		Component component = null;

		readContent = readContents(location, contents);

		component = getComponent(readContent);

		SettingConfigurer settings = (SettingConfigurer) readContent.get(FileContent.SETTINGS);
		HashMap<FileContent, Object> loadedContent = new HashMap<FileContent, Object>();
		loadedContent.put(FileContent.COMPONENT, component);
		loadedContent.put(FileContent.SETTINGS, settings);
		return loadedContent;

	}

	public static Component loadComponent(File file, FileContent... content)
	{
		HashMap<FileContent, Object> contents = FileProcessor.loadContents(file, content);
		return (Component) contents.get(FileContent.COMPONENT);
	}

	private static Component getComponent(HashMap<FileContent, Object> contents)
	{
		Component component = (Component) contents.get(FileContent.COMPONENT);
		for (FileContent conten : contents.keySet())
		{
			try
			{
				switch (conten)
				{
				case DATA:
					fillComponentData(contents, component);
					break;
				case SETTINGS:
					break;
				default:
					break;
				}
			} catch (Exception noEnvironment)
			{
				noEnvironment.printStackTrace();
			}
		}
		return component;
	}

	@SuppressWarnings("unchecked")
	private static void fillComponentData(HashMap<FileContent, Object> contents, Component component)
	{
		if (component != null)
		{
			HashMap<String, HashMap<HybridTime, ?>> data = (HashMap<String, HashMap<HybridTime, ?>>) contents
			.get(FileContent.DATA);

			for (@SuppressWarnings("rawtypes")
			Data id : component.component().getContent().getData(true))
			{
				DataOperator.getOperator(id).loadStoredValues(data.get(id.component().getAddress()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private static HashMap<FileContent, Object> readContents(File location, FileContent... contents)
	{
		ArrayList<FileContent> contentz = new ArrayList<FileContent>();
		contentz.addAll(Arrays.asList(contents));
		HashMap<FileContent, Object> loadedContent = new HashMap<FileContent, Object>();
		try
		{
			ZipInputStream in = new ZipInputStream(new FileInputStream(location));
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
						// FileContent inputElement =
						// FileContent.getFileContentType(entry.getName());
						// if (contentz.contains(inputElement))

						switch (inputElement)
						{

						case DATA:
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
						default:
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

	private static void createFiles(File location, Component component, FileContent... contents)
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

					try
					{
						storeSettings(location, component.component().getSettings());
					} catch (Exception noComponentSettings)
					{
					}

					break;
				case COMPONENT:
					storeComponent(location, component);
					break;
				default:
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
		for (Data<?> dat : component.component().getContent().getData(true))
		{
			if (DataOperator.getOperator(dat).isDataStored())
			{
				data.put(dat.component().getAddress(), dat.component().getStoredValues());
			} // ObjectSerializer.store(location.getAbsolutePath() + "/" +
				// dat.component().getAddress(),
				// dat.component().getStoredValues());//location.getAbsolutePath());
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
		String xmlSettings = XMLParser.serializeObject(ComponentWorker.getOperator(component).getNewInstance());
		byte[] compressed = DataCompressor.compressDataGZip(xmlSettings);
		String suffix = component.component().getLabels().getFullDescription() + " Component";
		System.out.println(suffix);
		ObjectSerializer.store(location.getAbsolutePath() + "/" + suffix, compressed);
	}

}
