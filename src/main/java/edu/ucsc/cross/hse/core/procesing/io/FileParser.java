package edu.ucsc.cross.hse.core.procesing.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import bes.commons.data.file.general.FileSystemInteractor;
import bs.commons.io.file.FileSystemOperator;
import bs.commons.objects.labeling.StringFormatter;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentCoordinator;
import edu.ucsc.cross.hse.core.framework.component.ComponentAdministrator;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentContent;
import edu.ucsc.cross.hse.core.processing.execution.Processor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;
import edu.ucsc.cross.hse.core2.framework.component.Zipper;
import edu.ucsc.cross.hse.core2.framework.component.Zipper.CompressionFormat;

public class FileParser extends ProcessorAccess
{

	public FileParser(Processor processor)
	{
		super(processor);
	}

	public void autoStoreData(EnvironmentContent data)
	{
		for (Component comp : getEnvironment().getHierarchy().getComponents(true))
		{
			// comp.getConfigurer().setEnvironment(null);
			// comp.getConfigurer().resetHierarchy();
		}
		if (getSettings().getData().automaticallyStoreResults)
		{
			storeEnvironmentData(data);
		}
	}

	public void storeEnvironmentData(EnvironmentContent data)
	{
		String directory = getSettings().getData().autoStoreDirectory + "/";
		if (getSettings().getData().environmentNameSubDirectory)
		{
			directory += data.getDescription().getName() + "/";
		}

		String fileName = data.getDescription().getDescription() + "_"
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
				fileStream.write(Zipper.compressDataGZip(out));
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

	@SuppressWarnings("unchecked")
	public static <T extends Component> T loadComponent(String file_directory, String file_name)
	{
		return loadComponent(file_directory, file_name, false);

	}

	@SuppressWarnings("unchecked")
	public static <T extends Component> T loadComponent(String file_directory, String file_name, boolean compress)
	{
		T component = (T) XMLParser.getObject(new File(file_directory, file_name));
		for (Component componen : component.getHierarchy().getComponents(true))
		{
			ComponentAdministrator.getConfigurer(componen).setInitialized(null);
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

	public static <T extends Component> void saveComponent(T component, String file_directory, String file_name,
	CompressionFormat compression)
	{
		Object clonedComponent = ObjectCloner.xmlClone(component);
		String serializedComponent = XMLParser.serializeObject(component);
		byte[] byteOut = null;
		switch (compression)
		{
		case GZIP:
			byteOut = Zipper.compressDataGZip(serializedComponent);
			break;
		default:
			byteOut = serializedComponent.getBytes();
			break;
		}
		FileSystemInteractor.createOutputFile(file_directory, file_name, byteOut);// clonedComponent));

		// ComponentOperator.getConfigurer(component).saveComponentToFile(file_directory,
		// file_name);
	}

	private static <T extends Component> void prepareComponent(T component)
	{
		ArrayList<Component> allComponents = new ArrayList<Component>();
		ComponentCoordinator.constructTree(component.getHierarchy());
		// allComponents.add(component);
		for (Component subComponent : component.getHierarchy().getComponents(true))
		{
			if (!allComponents.contains(subComponent))
			{
				allComponents.add(subComponent);

				// if
				// (component.getProperties().getClassification().equals(ElementClassification.DATA_SET))
				// {
				// Elements elements = ((Elements) component);
				// elements.initializeElements();
				// }
			}
		}
		// System.out.println("hello");
		// super.getComponents().initializeSimulated(true, allComponents);
		// super.getComponents().initializeSimulated(false, allComponents);
		// super.getComponents().clearEmptyMaps(allComponents);
		// getElements().initializeSimulated(false);
		// initializeSimulated(true);
		// component.clearMapsIfEmpty();

	}

}
