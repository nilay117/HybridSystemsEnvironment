package edu.ucsc.cross.hse.core.container;

import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.util.ArrayList;
import java.util.HashMap;

public class EnvironmentData
{

	private ArrayList<HybridTime> storeTimes;
	private ArrayList<DataSeries<Double>> globalStateData;
	private HashMap<String, String> stateNames;

	/*
	 * Constructor
	 */
	public EnvironmentData()
	{
		storeTimes = new ArrayList<HybridTime>();
		globalStateData = new ArrayList<DataSeries<Double>>();
		stateNames = new HashMap<String, String>();
	}

	public void load(ArrayList<HybridTime> store_times, ArrayList<DataSeries<Double>> global_state_data)
	{
		storeTimes = store_times;
		globalStateData = global_state_data;

	}

	public HybridTime getLastStoredTime()
	{
		HybridTime lastTime = null;
		if (storeTimes.size() > 0)
		{
			lastTime = storeTimes.get(storeTimes.size() - 1);
		}
		return lastTime;
	}

	public ArrayList<DataSeries<Double>> getGlobalStateData()
	{
		return globalStateData;
	}

	public ArrayList<HybridTime> getStoreTimes()
	{
		return storeTimes;
	}

	public HashMap<String, String> getStates()
	{
		return stateNames;
	}

}
