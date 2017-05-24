package edu.ucsc.cross.hse.core.processing.data;

import java.io.File;
import java.util.ArrayList;

import bs.commons.io.file.FileSystemOperator;
import bs.commons.io.system.StringFormatter;
import bs.commons.objects.manipulation.XMLParser;
import edu.ucsc.cross.hse.core.component.constructors.Component;
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
		FileSystemOperator.createOutputFile(new File(directory, fileName), XMLParser.serializeObject(data));
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
		component.saveComponentToFile(file_directory, file_name);
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
