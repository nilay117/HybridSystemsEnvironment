package edu.ucsc.cross.hse.core.procesing.io;

import java.io.File;
import java.util.HashMap;

import com.rits.cloning.Cloner;

import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.processing.data.SettingConfigurer;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

/*
 * Stores and loads data directly from/to the processor. Used to load or store
 * smaller sections of data such as a component definition.
 */
public class FileExchanger extends ProcessorAccess
{

	/*
	 * Object cloner instance
	 */
	public static Cloner cloner = new Cloner();

	/*
	 * Constructor that links the file exchanger to the processor
	 */
	public FileExchanger(CentralProcessor processor)
	{
		super(processor);
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

	/*
	 * Loads a component and all additional contents from a file
	 */
	public Component load(File file)
	{
		return load(file, FileContent.values());
	}

	/*
	 * Load all specified content from a file
	 */
	public Component load(File file, FileContent... contents)
	{
		Component component = null;
		{
			try
			{
				HashMap<FileContent, Object> content = FileProcessor.loadContents(file, contents);
				component = (Component) content.get(FileContent.COMPONENT);
				this.getSettings().setSettings((SettingConfigurer) content.get(FileContent.SETTINGS));

			} catch (Exception badFile)
			{

			}
		}
		return component;

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
