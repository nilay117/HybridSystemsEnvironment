package edu.ucsc.cross.hse.core.procesing.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import com.be3short.data.compression.DataCompressor;
import com.be3short.data.file.general.FileSystemInteractor;
import com.be3short.data.file.xml.XMLParser;
import com.be3short.data.serialization.ObjectSerializer;
import com.esotericsoftware.kryo.Kryo;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
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
		FileSystemInteractor.checkDirectory(location, true);
		for (FileContent content : contents)
		{
			switch (content)
			{
			case ENVIRONMENT_CONFIGURATION:
				storeConfiguration(location, content.sub_directory, component.getEnvironment());
				break;
			case ALL_DATA:
				storeData(location, content.sub_directory, component.getEnvironment());
				break;
			case SETTINGS:
				storeSettings(location, content.sub_directory, component.getEnvironment());
				break;
			}
		}
		try
		{
			DataCompressor.zipDirectory(location.getAbsolutePath(), location.getAbsolutePath() + ".hse");
			//FileUtils.deleteDirectory(location);
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

	private void storeConfiguration(File location, String sub_directory, Component component)
	{
		FileSystemInteractor.checkDirectory(location.getAbsolutePath(), true);
		String environment = XMLParser.serializeObject(ComponentOperator.getOperator(component).getNewInstance());
		byte[] compressed = DataCompressor.compressDataGZip(environment);
		ObjectSerializer.store(location.getAbsolutePath() + "/" + component.getActions().getAddress(), compressed);
		//		FileSystemInteractor.createOutputFile(location.getAbsolutePath(), component.getActions().getAddress() + ".gz",
		//		compressed);
	}

	private void storeData(File location, String sub_directory, Component component)
	{
		HashMap<String, Object> data = new HashMap<String, Object>(); // new
		for (Data dat : component.getContents().getObjects(Data.class, true))
		{
			data.put(dat.getActions().getAddress(), dat.getActions().getStoredHybridValues());
		}
		ObjectSerializer.store(data, location.getAbsolutePath());

	}

	private void storeSettings(File location, String sub_directory, Component component)
	{
		String xmlSettings = XMLParser.serializeObject(this.getSettings());
		byte[] compressed = DataCompressor.compressDataGZip(xmlSettings);

		ObjectSerializer.store(
		location.getAbsolutePath() + "/" + ComponentOperator.generateInstanceAddress(this.getSettings()), compressed);
	}
}
