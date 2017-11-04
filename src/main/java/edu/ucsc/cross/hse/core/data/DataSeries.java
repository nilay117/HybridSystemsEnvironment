package edu.ucsc.cross.hse.core.data;

import com.be3short.obj.modification.XMLParser;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.util.ArrayList;

public class DataSeries<X>
{

	private ArrayList<HybridTime> storeTimes; // data store times
	private ArrayList<X> storedData; // stored values
	private Class<X> dataClass;
	private String elementName;
	private String parentName;
	private String parentID;

	public DataSeries(ArrayList<HybridTime> store_times, Class<X> data_class, String element_name, String parent_name,
	String parent_id)
	{
		storedData = new ArrayList<X>();
		dataClass = data_class;
		this.storeTimes = store_times;
		this.elementName = element_name;
		this.parentName = parent_name;
		this.parentID = parent_id;
	}

	public String getParentID()
	{
		return parentID;
	}

	/*
	 * Store the current value of the data element
	 */
	public void storeData(X value)
	{

		storedData.add((X) value);
	}

	/*
	 * Store the current value of the data element
	 */
	public void storeDataGeneral(Object value)
	{

		storedData.add((X) value);
	}

	/*
	 * Get all of the data stored
	 */
	public ArrayList<X> getAllStoredData()
	{
		return storedData;
	}

	public ArrayList<HybridTime> getStoreTimes()
	{
		return storeTimes;
	}

	public X getStoredData(HybridTime store_time)
	{
		Integer ind = storeTimes.indexOf(store_time);

		return storedData.get(ind);

	}

	public String getElementName()
	{
		return elementName;
	}

	public String getParentName()
	{
		return parentName;
	}

	public DataSeries<?> getSeriesWithSameParent(String element_name, ArrayList<DataSeries<?>> series_list)
	{
		for (DataSeries<?> series : series_list)
		{
			if (series.getParentID().equals(parentID))
			{
				if (series.getElementName().equals(element_name))
				{
					return series;
				}

			}
		}
		return null;
	}

	public String getHeader()
	{

		return correctName(elementName) + ";parent=" + correctName(parentName) + ";parentID=" + correctName(parentID)
		+ ";" + XMLParser.serializeObject(storedData.get(0).getClass());
	}

	public static String correctName(String name)
	{
		String correctedName = name.replace(",", "[comma]");
		correctedName = correctedName.replace(";", "[semiColon]");
		return correctedName;
	}

	public static <D> DataSeries<D> getDataSeries(EnvironmentData data, String entry)
	{
		String component[] = entry.split(";");
		String elementName = component[0];
		String parentName = component[1];
		String parentID = component[2];
		Class<D> elementClass = (Class<D>) XMLParser.getObjectFromString(component[3]);
		DataSeries<D> series = new DataSeries<D>(data.getStoreTimes(), elementClass, elementName, parentName, parentID);
		return series;
	}

	public static <C> DataSeries<C> getSeries(ArrayList<HybridTime> store_times, Class<C> data_class,
	String element_name, String parent_name, String parent_id)
	{
		return new DataSeries<C>(store_times, data_class, element_name, parent_name, parent_id);
	}

	public Class<X> getDataClass()
	{
		return dataClass;
	}
}
