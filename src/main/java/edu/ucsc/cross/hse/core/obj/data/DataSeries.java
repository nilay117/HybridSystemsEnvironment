package edu.ucsc.cross.hse.core.obj.data;

import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.access.ObjectManipulator;
import edu.ucsc.cross.hse.core.obj.structure.HybridTime;
import java.util.ArrayList;

@Loggable(Loggable.DEBUG)
public class DataSeries<X>
{

	private ArrayList<HybridTime> storeTimes; // data store times
	private ObjectManipulator elementLocation; // access to the data element being stored

	public ObjectManipulator getElementLocation()
	{
		return elementLocation;
	}

	private ArrayList<X> storedData; // stored values

	public DataSeries(ObjectManipulator element, ArrayList<HybridTime> store_times)
	{
		elementLocation = element;
		storedData = new ArrayList<X>();
		this.storeTimes = store_times;
	}

	/*
	 * Store the current value of the data element
	 */
	public void storeData()
	{

		storedData.add((X) elementLocation.getObject());
	}

	/*
	 * Get all of the data stored
	 */
	public ArrayList<X> getAllStoredData()
	{
		return storedData;
	}
}
