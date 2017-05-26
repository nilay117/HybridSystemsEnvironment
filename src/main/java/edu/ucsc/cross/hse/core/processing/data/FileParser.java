package edu.ucsc.cross.hse.core.processing.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.io.system.StringFormatter;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.component.foundation.Component;
import edu.ucsc.cross.hse.core.component.system.GlobalHybridSystem;
import edu.ucsc.cross.hse.core.object.accessors.Hierarchy;
import edu.ucsc.cross.hse.core.processing.management.Environment;
import edu.ucsc.cross.hse.core.processing.management.ProcessorAccess;

public class FileParser extends ProcessorAccess
{

	public FileParser(Environment processor)
	{
		super(processor);
	}

	public void autoStoreData(GlobalHybridSystem data)
	{
		for (Component comp : getEnvironment().getComponents(true))
		{
			comp.getConfigurer().setEnvironment(null);
			comp.getConfigurer().resetHierarchy();
		}
		if (getSettings().getData().automaticallyStoreResults)
		{
			storeEnvironmentData(data);
		}
	}

	public void storeEnvironmentData(GlobalHybridSystem data)
	{
		String directory = getSettings().getData().autoStoreDirectory + "/";
		if (getSettings().getData().environmentNameSubDirectory)
		{
			directory += data.getProperties().getName() + "/";
		}

		String fileName = data.getProperties().getDescription() + "_"
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
				fileStream.write(Zipper.compressData(out));
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
	public <T extends Component> T loadComponent(String file_directory, String file_name)
	{
		T component = (T) XMLParser.getObject(new File(file_directory, file_name));
		for (Component componen : component.getComponents(true))
		{
			Component.setInitialized(componen, null);
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

	public <T extends Component> void saveComponent(T component, String file_directory, String file_name)
	{
		prepareComponent(component);
		component.getConfigurer().saveComponentToFile(file_directory, file_name);
	}

	private <T extends Component> void prepareComponent(T component)
	{
		ArrayList<Component> allComponents = new ArrayList<Component>();
		Hierarchy.constructTree(component.getHierarchy());
		// allComponents.add(component);
		for (Component subComponent : component.getComponents(true))
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
		;
	}
}
