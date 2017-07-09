package edu.ucsc.cross.hse.core.procesing.io;

import java.io.File;

import com.rits.cloning.Cloner;

import edu.ucsc.cross.hse.core.framework.component.Component;
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

	/*
	 * Store selected contents of the component based on the FileContents
	 * speficied in the input
	 */
	public void store(File file_path, Component component, FileContent... contents)
	{
		FileProcessor.store(file_path, component, contents);
	}

	public Component load(File file)
	{
		return load(file, FileContent.values());
	}

	/*
	 * Load all specified content from a file
	 */
	public Component load(File file, FileContent... contents)
	{
		return FileProcessor.load(this.getProcessor(), file, contents);
	}

	/*
	 * generate a directory name for an environment
	 */
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
