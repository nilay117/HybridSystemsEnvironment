package edu.ucsc.cross.hse.core.file;

import com.be3short.io.format.FileFormat;
import com.be3short.io.general.FileSystemInteractor;
import com.be3short.obj.access.FieldFinder;
import com.be3short.obj.modification.XMLParser;
import edu.ucsc.cross.hse.core.container.EnvironmentContents;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.container.EnvironmentOutputs;
import edu.ucsc.cross.hse.core.container.EnvironmentSettings;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.io.Console;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
 * The Hybrid Systems Environment file class stores objects to be saved/retrieved from a file. Any combination of the
 * basic system contents can be stored/retrieved in this file, as well as additional user defined components. The basic
 * system contents are systems (selected systems to be stored), environment (entire environment, consists of all
 * components), settings, content (all content loaded into the environment), and execution data (data collected during
 * an environment execution). Note: All classes stored in a file must be on a projects class path in order to retrieve
 * the file successfully.
 */
@SuppressWarnings(
{ "rawtypes", "unchecked" })
public class EnvironmentFile implements FileFormat
{

	// System File Content Definitions : Used to extract system contents from a file

	// File Content Mapping : Stores all file contents
	private HashMap<Class<?>, ArrayList<Object>> fileContents;

	/*
	 * Add content to the file
	 * 
	 * @param content : content to be added
	 */
	public <T> void addContent(T... content)
	{
		for (T content_item : content)
		{

			if (content_item != null)
			{
				storeContent(content_item);
			}
		}
	}

	@Override
	public String extension()
	{
		// TODO Auto-generated method stub
		return EnvironmentFile.fileExtension;
	}

	/*
	 * Returns the first item stored in the list for the specified type. This call is used to fetch the components which
	 * only have a single instance stored, such as an environment or settings.
	 * 
	 * @param content_class - class of the content to be retrieved
	 * 
	 * @return content - first item stored in list for specified content type
	 */
	public <T> T getContent(Class<T> content_class)
	{
		T content = null;
		if (fileContents.containsKey(content_class))
		{
			if (fileContents.get(content_class).size() > 0)
			{
				content = (T) fileContents.get(content_class).get(0);
			}
		}
		return content;
	}

	/*
	 * Returns the list of stored items of the specified type. This call is used to fetch collections of components.
	 * 
	 * @param content_class - class of the list of contents to be retrieved
	 * 
	 * @return content - first item stored in list for specified content type
	 */
	public <T> ArrayList<T> getContents(Class<T> content_class)
	{
		ArrayList<T> content = null;
		if (fileContents.containsKey(content_class))
		{
			content = (ArrayList<T>) fileContents.get(content_class);
		}
		return content;
	}

	@Override
	public String label()
	{
		// TODO Auto-generated method stub
		return "HSE File";
	}

	/*
	 * Stores all contents to specified file
	 * 
	 * @param output - location where file should be created
	 */
	public void writeToFile(File output)
	{
		try
		{
			String xmlOutput = XMLParser.serializeObject(this);
			File adjustedInput = output;
			if (!output.getAbsolutePath().contains(fileExtension))
			{
				adjustedInput = new File(output.getAbsolutePath() + fileExtension);
			}
			FileSystemInteractor.createOutputFile(adjustedInput.getAbsolutePath(), xmlOutput);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Stores all contents to specified file path
	 * 
	 * @param output - path of location where file should be created
	 */
	public void writeToFile(String output)
	{
		writeToFile(new File(output));
	}

	/*
	 * Initializes the content mapping with lists for all of the system objects
	 */
	private void initializeContentMap()
	{
		fileContents = new HashMap<Class<?>, ArrayList<Object>>();
		for (Class<?> componentClass : systemContents)
		{
			fileContents.put(componentClass, new ArrayList<Object>());
		}
	}

	private <T> void storeContent(T content)
	{
		Class<?> contentType = content.getClass();

		if (!fileContents.containsKey(contentType))
		{
			fileContents.put(contentType, new ArrayList<Object>());
		}
		fileContents.get(contentType).add(content);
		if (contentType.equals(Environment.class))
		{
			Environment env = (Environment) content;
			// EnvironmentData dat = new EnvironmentData();
			// if (env.getData() != null)
			{
				// add(new CSVFile(env.getManager(), true));
				if (env.getData() != null)
				{
					// dat = env.getData();
					try
					{

						// CSVFile f = new CSVFile(env.getManager(), true);
						// addContent(f);//// add(new CSVFile(env.getManager(), true));

						// env.loadData(null);
						// dat.load(env.getData().getStoreTimes(), env.getData().getGlobalStateData());
						// env.loadData(null);
						// env.getData().load(null, null);
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					// env.getData().load(null, null);
					// storeContent(env.getData());
				}
				storeContent(env.getData());
				// env.loadData(null);
				// env.loadData(null);
			}

			storeContent(env.getContents());
			storeContent(env.getSettings());
			if (env.getContents().getSystems().size() > 0)
			{
				storeContent(
				env.getContents().getSystems().toArray(new HybridSystem[env.getContents().getSystems().size()]));
			}

			// env.loadData(dat);

		} else if (FieldFinder.containsSuper(content, HybridSystem.class))
		{
			fileContents.get(HybridSystem.class).add(content);
		} else if (contentType.equals(EnvironmentData.class))
		{

		}

	}

	/*
	 * Constructor
	 */
	public EnvironmentFile()
	{
		initializeContentMap();
	}

	public static final Class<EnvironmentContents> CONTENT = EnvironmentContents.class;

	public static final Class<EnvironmentData> DATA = EnvironmentData.class;

	public static final Class<Environment> ENVIRONMENT = Environment.class;

	public static final Class<EnvironmentOutputs> OUTPUT = EnvironmentOutputs.class;

	public static final Class<EnvironmentSettings> SETTINGS = EnvironmentSettings.class;

	public static final Class<HybridSystem> SYSTEMS = HybridSystem.class;

	private static final String fileExtension = ".hse";

	// File Variables
	private static final ArrayList<Class<?>> systemContents = new ArrayList<Class<?>>(
	Arrays.asList(SYSTEMS, ENVIRONMENT, SETTINGS, CONTENT, DATA, OUTPUT));

	/*
	 * Creates an output file with the specified contents
	 * 
	 * @param output - location of output file
	 *
	 */
	public static void createFile(File output, Object... contents)
	{
		try
		{

			EnvironmentFile newOutput = new EnvironmentFile();
			newOutput.addContent(contents);
			newOutput.writeToFile(output);

		} catch (Exception badFile)
		{
			Console.error("Unable to write file: " + output, badFile);
		}
	}

	/*
	 * Loads contents from a stored HSE file
	 * 
	 * @param input - location of stored file
	 * 
	 * @return T contents from the parsed input file
	 */
	public static <T> T readContentFromFile(File input, Class<T> content_type)
	{
		EnvironmentFile inputFile = EnvironmentFile.readFromFile(input);
		T inputContent = null;
		try
		{
			if (inputFile != null)
			{
				if (content_type.equals(EnvironmentData.class) || content_type.equals(Environment.class))
				{
					EnvironmentData dat = null;
					try
					{
						dat = inputFile.getContent(EnvironmentData.class);// .extractDataFromContents();
					} catch (Exception e)
					{
						e.printStackTrace();
						dat = new EnvironmentData();
					}
					if (content_type.equals(Environment.class))
					{
						// System.out.println(XMLParser.serializeObject(dat));
						Environment env = inputFile.getContent(Environment.class);
						// if (inputFile.getContent(EnvironmentData.class) != null)
						// {
						// env.loadData(dat);// .getData().load(dat);
						//
						// }
						env.loadData(dat);
						inputContent = (T) env;

					} else
					{
						inputContent = (T) dat;
					}
				}

				inputContent = inputFile.getContent(content_type);

			}
		} catch (Exception badFile)
		{
			Console.error("Unable to fetch content " + content_type.toString() + " from file: " + input, badFile);
		}
		return inputContent;
	}

	/*
	 * Loads a stored HSE file
	 * 
	 * @param input - location of stored file
	 * 
	 * @return HSEFile parsed from the input file
	 */
	public static EnvironmentFile readFromFile(File input)
	{
		EnvironmentFile inputFile = null;
		try
		{
			if (!input.getAbsolutePath().contains(fileExtension))
			{
				Console.error("Non HSE file specified: " + input, new Exception());

			} else
			{
				EnvironmentFile parsedInput = (EnvironmentFile) XMLParser.getObject(input);
				inputFile = parsedInput;
			}
		} catch (Exception badFile)
		{
			Console.error("Unable to fetch file: " + input, badFile);
		}
		return inputFile;
	}

	public static void checkforDirs(File file)
	{
		try
		{
			// file.mkdirs();
			String directory = "";
			String dirs[] = file.getAbsolutePath().split("/");
			for (int i = 0; i < dirs.length - 1; i++)
			{
				directory += dirs[i] + "/";
			}
			File dirFile = new File(directory);
			dirFile.mkdirs();

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
