package edu.ucsc.cross.hse.core.data;

import edu.ucsc.cross.hse.core.time.HybridTime;
import java.util.ArrayList;

public class DataSeries<X>
{

	private ArrayList<HybridTime> storeTimes; // data store times
	private ArrayList<X> storedData; // stored values
	private String elementName;
	private String parentName;
	private String parentID;

	public DataSeries(ArrayList<HybridTime> store_times, String element_name, String parent_name, String parent_id)
	{
		storedData = new ArrayList<X>();
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

	public <T> DataSeries<T> getSeriesWithSameParent(String element_name, ArrayList<DataSeries<T>> series_list)
	{
		for (DataSeries<T> series : series_list)
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
}
