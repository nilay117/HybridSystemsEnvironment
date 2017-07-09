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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

import bs.commons.objects.manipulation.ObjectCloner;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentContent;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.framework.component.FullComponentOperator;
import edu.ucsc.cross.hse.core.framework.environment.HybridEnvironment;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

public class FileExchanger extends ProcessorAccess
{

	static FileProcessor packager;
	public static Cloner cloner = new Cloner();

	public FileExchanger(CentralProcessor processor)
	{
		super(processor);
		packager = new FileProcessor(processor);
	}

	/*
	 * Store all contents of the component
	 */
	public void store(File file_path, Component component)
	{
		store(file_path, component, FileContent.values());
	}

	public void store(File file_path, Component component, FileContent... contents)
	{
		packager.store(file_path, component, contents);
	}

	public Component load(File file)
	{
		return load(file, FileContent.values());
	}

	public Component load(File file, FileContent... contents)
	{
		return FileProcessor.load(this.getProcessor(), file, contents);
	}

	public String generateDirectoryName()
	{
		String directory = getSettings().getDataSettings().resultAutoStoreDirectory + "/";
		if (getSettings().getDataSettings().createResultSubDirectory)
		{
			directory += this.getComponents().getEnv().component().getLabels().getClassification() + "/";
		}
		return directory;
	}

}
