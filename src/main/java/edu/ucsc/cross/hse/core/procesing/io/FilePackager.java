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

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingElement;

public class FilePackager extends ProcessingElement
{

	private Kryo kryo = new Kryo();

	public FilePackager(CentralProcessor processor)
	{
		super(processor);
		initialize();
	}

	private void initialize()
	{
		kryo = new Kryo();
	}

	public void storeContents(File location, Component component, FileContent... contents)
	{
		FileSystemInteractor.checkDirectory(location.getAbsolutePath(), true);
		for (FileContent content : contents)
		{
			switch (content)
			{
			case ENVIRONMENT:
				storeConfiguration(location, component.getEnvironment());
				break;
			case DATA:
				storeData(location, component);
				break;
			case SETTINGS:
				storeSettings(location, component.getEnvironment());
				break;
			case COMPONENT:
				storeComponent(location, component);
				break;
			}
		}
		try
		{
			DataCompressor.zipDirectory(location.getAbsolutePath(), location.getAbsolutePath() + ".hse");
			FileUtils.deleteDirectory(location);
		} catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void storeConfiguration(File location, Component component)
	{
		FileSystemInteractor.checkDirectory(location.getAbsolutePath(), true);
		String environment = XMLParser.serializeObject(ComponentOperator.getOperator(component).getNewInstance());
		byte[] compressed = DataCompressor.compressDataGZip(environment);
		ObjectSerializer.store(location.getAbsolutePath() + "/" + component.getActions().getAddress(), compressed);
		// FileSystemInteractor.createOutputFile(location.getAbsolutePath(),
		// component.getActions().getAddress() + ".gz",
		// compressed);
	}

	private void storeData(File location, Component component)
	{
		HashMap<String, Object> data = new HashMap<String, Object>(); // new
		ArrayList<Data> datas = new ArrayList<Data>();
		datas.addAll(component.getContents().getObjects(State.class, true));
		datas.addAll(component.getContents().getObjects(Data.class, true));
		for (Data dat : datas)
		{
			data.put(dat.getActions().getAddress(), dat.getActions().getStoredValues());
		}
		ObjectSerializer.store(data, location.getAbsolutePath());

	}

	private void storeSettings(File location, Component component)
	{
		String xmlSettings = XMLParser.serializeObject(this.getSettings());
		byte[] compressed = DataCompressor.compressDataGZip(xmlSettings);

		ObjectSerializer.store(
		location.getAbsolutePath() + "/" + ComponentOperator.generateInstanceAddress(this.getSettings()), compressed);
	}

	private void storeComponent(File location, Component component)
	{
		String xmlSettings = XMLParser.serializeObject(ComponentOperator.getOperator(component).getNewInstance());
		byte[] compressed = DataCompressor.compressDataGZip(xmlSettings);

		ObjectSerializer.store(
		location.getAbsolutePath() + "/" + component.getLabels().getFullDescription() + "_Component", compressed);
	}

	public HashMap<FileContent, Object> loadContents(File location, FileContent... contents)
	{
		ArrayList<FileContent> content = new ArrayList<FileContent>();
		content.addAll(Arrays.asList(contents));
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
					this.getConsole().print("Loading data from file : " + entry.getName());
					Object readIn = kryo.readClassAndObject(input);
					FileContent inputElement = FileContent.getFileContentType(entry.getName());
					switch (inputElement)
					{
					case DATA:
						if (content.contains(FileContent.DATA))
						{
							datas.put(entry.getName(), (HashMap<HybridTime, ?>) readIn);
						}
						break;
					case ENVIRONMENT:
						EnvironmentContent envContentz = (EnvironmentContent) XMLParser
						.getObjectFromString((String) DataDecompressor.decompressDataGZipString((byte[]) readIn));
						loadedContent.put(FileContent.ENVIRONMENT, envContentz);
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
				} catch (Exception e)
				{

				}
				entry = in.getNextEntry();

			}
			if (datas.size() > 0)
			{
				loadedContent.put(FileContent.DATA, datas);
			}
		} catch (Exception ee)
		{

		}
		return loadedContent;
	}
}
