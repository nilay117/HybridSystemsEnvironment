package edu.ucsc.cross.hse.core.data;

import java.lang.reflect.Field;
import java.util.ArrayList;

import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.time.HybridTime;

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

	public Integer getParentID()
	{
		return parent.data().getAddress();
	}

	public String getParentName()
	{
		return parent.data().getUniqueLabel();
	}

	public DataSeries<?> getSeriesWithSameParent(String element_name, ArrayList<DataSeries<?>> series_list)
	{
		for (DataSeries<?> series : series_list)
		{
			if (series.getParentID().equals(parent.data().getAddress()))
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
