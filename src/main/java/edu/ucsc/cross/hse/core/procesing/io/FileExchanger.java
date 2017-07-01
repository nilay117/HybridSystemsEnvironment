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
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.Obj;
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
		store(directory + "/" + fileName, this.getEnv(), FileComponent.CONFIGURATION);// ,.CONFIGURATION);

	}

	private void store(String file_path, Component component, FileComponent... contents)
	{
		Kryo kryo = new Kryo();
		SaveFile file = new SaveFile(null);
		for (FileComponent content : contents)
		{
			file.fileComponents.put(content, getContentString(component, content));
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Output output;
		try
		{
			// ObjectOutputStream oos = new ObjectOutputStream(baos);
			// ObjectOutputStream oos = new ObjectOutputStream(baos);
			// output = new Output(oos);//new
			// FileOutputStream("results/file.bin"));
			// output = new Output(baos);//new
			// FileOutputStream("results/file.bin"));
			// ObjectOutputStream oos = new ObjectOutputStream(baos);
			output = new Output(new FileOutputStream(file_path));
			// System.out.println(XMLParser.serializeObject(this.getData().getAllMaps()));
			HashMap<String, HashMap<HybridTime, ?>> envContent = this.getData().getAllMaps();
			HashMap<HybridTime, String> filez = new HashMap<HybridTime, String>();
			filez.put(new HybridTime(), XMLParser.serializeObject(file));
			envContent.put(SaveFile.class.getName(), filez);
			kryo.writeClassAndObject(output, envContent);
			// String output2 = XMLParser.serializeObject(file);
			// oos.writeObject(output2);
			// file.dataz = output.toBytes();
			/// String output2 = XMLParser.serializeObject(file);
			// FileSystemOperator.createOutputFile(file_path, output2);
			// baos.close();
			output.close();
			// oos.close();

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// file.data = getDataByteMap(component);

	}

	public void load(File file)
	{
		SaveFile savedFile = null;
		Kryo kryo = new Kryo();
		try
		{
			// ..ObjectInputStream ois = new ObjectInputStream();
			// SaveFile savedFile = (SaveFile) ois.readObject();

			// String content = DataDecompressor
			// .decompressDataGZipString(savedFile.fileComponents.get(FileComponent.COMPONENT));
			Input input = new Input(new FileInputStream(file));
			// System.out.println(XMLParser.serializeObject(savedFile.dataz));

			// HashMap<String, SavedValues> envContent = (HashMap<String,
			// SavedValues>) kryo.readClassAndObject(input);//,
			// EnvironmentContent.class);
			HashMap<String, HashMap<HybridTime, ?>> envContent = (HashMap<String, HashMap<HybridTime, ?>>) kryo
			.readClassAndObject(input);// , EnvironmentContent.class);
			// System.out.println(XMLParser.serializeObject(envContent));
			HashMap<HybridTime, ?> filez = envContent.get(SaveFile.class.getName());// .keySet().toArray(new
																					// HybridTime[1])
			for (Object fileString : filez.values())
			{
				savedFile = new SaveFile((String) fileString);
			}
			String contentz = DataDecompressor
			.decompressDataGZipString(savedFile.fileComponents.get(FileComponent.CONFIGURATION));
			// kryo.register(SavedValues.class, new ExternalizableSerializer());
			// kryo.register(HybridTime.class, new ExternalizableSerializer());
			EnvironmentContent envContentz = (EnvironmentContent) XMLParser.getObjectFromString(contentz);
			input.close();
			// loadData(savedFile, envContentz);
			this.processor.loadContents(envContentz);
			// this.processor.loadContents(envContentz);
			for (Obj id : envContentz.getContents().getObjects(Obj.class, true))
			{
				// Runnable runnable = new Runnable()
				// {
				//
				// @Override
				// public void run()
				// {]
				System.out.println(id.getActions().getAddress());
				DataOperator.getOperator(id).setStoredHybridValues(envContent.get(id.getActions().getAddress()));
				// }
				// };
				// Thread thread = new Thread(runnable);
				// thread.start();
			}
			this.processor.loadContents(envContentz);
		} catch (

		Exception e)
		{
			e.printStackTrace();
		}
	}
	// public void load(File file)
	// {
	// SaveFile savedFile = new
	// SaveFile(FileSystemOperator.getFileContentsAsString(file));
	// try
	// {
	// // String content = DataDecompressor
	// //
	// .decompressDataGZipString(savedFile.fileComponents.get(FileComponent.COMPONENT));
	// String content = DataDecompressor
	// .decompressDataGZipString(savedFile.fileComponents.get(FileComponent.CONFIGURATION));
	// EnvironmentContent envContent = (EnvironmentContent)
	// XMLParser.getObjectFromString(content);
	// loadData(savedFile, envContent);
	// this.processor.loadContents(envContent);
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// }

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
		HashMap<String, Obj> dataMap = ComponentOperator.getOperator(component).getDataLinks();
		for (String data : dataBytes.keySet())
		{

			startDataThread(dataBytes.get(data), dataMap, data);
		}
	}

	private void startDataThread(byte[] data_bytes, HashMap<String, Obj> data_map, String data)
	{
		Runnable exe = loadDataThread(data_bytes, data_map, data);
		Thread thread = new Thread(exe);
		thread.start();
	}

	private Runnable loadDataThread(byte[] data_bytes, HashMap<String, Obj> data_map, String data)
	{
		Runnable task = new Runnable()
		{

			@Override
			public void run()
			{
				SystemConsole.print(data);
				HashMap<HybridTime, ?> unzippedData = (HashMap<HybridTime, ?>) XMLParser
				.getObjectFromString(DataDecompressor.decompressDataGZipString(data_bytes));
				// data_map.put(data, unzippedData);
				// SystemConsole.print(unzippedData.toString());
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
