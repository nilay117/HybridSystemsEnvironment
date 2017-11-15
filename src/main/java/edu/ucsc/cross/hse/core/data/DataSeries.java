package edu.ucsc.cross.hse.core.data;

import com.be3short.obj.modification.XMLParser;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class DataSeries<X>
{

	// private Class<X> dataClass;
	// private String elementName;
	// private String parentID;
	// private String parentName;

	private ObjectSet parent;
	private Field child;
	private ArrayList<X> storedData; // stored values
	private ArrayList<HybridTime> storeTimes; // data store times

	/*
	 * Get all of the data stored
	 */
	public ArrayList<X> getAllStoredData()
	{
		return storedData;
	}

	public Class<X> getDataClass()
	{
		return (Class<X>) child.getType();
	}

	public String getElementName()
	{
		return child.getName();
	}

	public String getHeader()
	{

		return correctName(child.getName()) + "``" + correctName(XMLParser.serializeObject(parent)) + "``"
		+ correctName(XMLParser.serializeObject(child)) + "``"
		+ correctName(XMLParser.serializeObject(parent.getClass()));// +
		// "``"
		// +
		// XMLParser.serializeObject(storedData.get(0).getClass());
	}

	public String getParentID()
	{
		return parent.extension().getAddress();
	}

	public String getParentName()
	{
		return parent.extension().getUniqueLabel();
	}

	public DataSeries<?> getSeriesWithSameParent(String element_name, ArrayList<DataSeries<?>> series_list)
	{
		for (DataSeries<?> series : series_list)
		{
			if (series.getParentID().equals(parent.extension().getAddress()))
			{
				if (series.getElementName().equals(element_name))
				{
					return series;
				}

			}
		}
		return null;
	}

	public X getStoredData(HybridTime store_time)
	{
		Integer ind = storeTimes.indexOf(store_time);

		return storedData.get(ind);

	}

	public ArrayList<HybridTime> getStoreTimes()
	{
		return storeTimes;
	}

	public DataSeries(ArrayList<HybridTime> store_times, ObjectSet parent, Field element)
	{
		storedData = new ArrayList<X>();
		this.storeTimes = store_times;
		this.child = element;
		this.parent = parent;
	}

	private static String correctName(String name)
	{
		String correctedName = name.replaceAll(",", Pattern.quote("[comma]"));
		correctedName = correctedName.replaceAll("``", Pattern.quote("[semiColon]"));
		return correctedName;
	}

	private static String restoreName(String name)
	{
		String correctedName = name.replaceAll(Pattern.quote("[comma]"), ",");
		correctedName = correctedName.replaceAll(Pattern.quote("[semiColon]"), "``");
		return correctedName;
	}

	@SuppressWarnings("unchecked")
	public static <D> DataSeries<D> getDataSeries(EnvironmentData data, String entry)
	{
		String component[] = entry.split(Pattern.quote("``"));
		String elementName = component[0];
		// Class cl = (Class) XMLParser.getObjectFromString(restoreName(component[3]));
		System.out.println(entry + "\n\n" + restoreName(component[1]));
		ObjectSet parentID = (ObjectSet) (XMLParser.getObjectFromString(restoreName(component[1])));
		Field parentName = (Field) XMLParser.getObjectFromString(restoreName(component[2]));

		// Class<D> elementClass = (Class<D>) XMLParser.getObjectFromString(component[3]);
		DataSeries<D> series = new DataSeries<D>(data.getStoreTimes(), parentID, parentName);
		return series;
	}

	public static <C> DataSeries<C> getSeries(ArrayList<HybridTime> store_times, ObjectSet parent, Field element,
	Class<C> data_class)
	{
		return new DataSeries<C>(store_times, parent, element);
	}

	public ObjectSet getParent()
	{
		return parent;
	}

	public Field getChild()
	{
		return child;
	}
}
