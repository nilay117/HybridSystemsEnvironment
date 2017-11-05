package edu.ucsc.cross.hse.core.container;

import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EnvironmentData
{

	private ArrayList<DataSeries<?>> globalStateData;
	private HashMap<String, String> stateNames;
	private ArrayList<HybridTime> storeTimes;

	public ArrayList<DataSeries<?>> getGlobalStateData()
	{
		return globalStateData;
	}

	public ArrayList<String> getLabelOrder()
	{
		ArrayList<String> nameOrder = new ArrayList<String>();
		if (nameOrder.size() == 0)
		{
			for (DataSeries<?> dataSeries : getGlobalStateData())
			{
				nameOrder.add(getLegendLabel(dataSeries));
			}
			Collections.sort(nameOrder);
		}
		return nameOrder;
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

	public HashMap<String, String> getObjectLabels()
	{
		return stateNames;
	}

	public HashMap<String, String> getStateNames()
	{
		return stateNames;
	}

	public ArrayList<HybridTime> getStoreTimes()
	{
		return storeTimes;
	}

	public void load(EnvironmentData new_data)
	{
		storeTimes = new_data.getStoreTimes();
		globalStateData = new_data.getGlobalStateData();

	}

	private String getLegendLabel(DataSeries<?> data)
	{

		String label = data.getParentName();
		if (getObjectLabels().containsKey(data.getParentID()))
		{
			return getObjectLabels().get(data.getParentID());
		}
		String labelBase = label;
		int append = 1;
		label = labelBase + "(" + append + ")";
		while (stateNames.containsValue(label))
		{
			append++;
			label = labelBase + "(" + append + ")";
		}
		getObjectLabels().put(data.getParentID(), label);
		return label;
	}

	/*
	 * Constructor
	 */
	public EnvironmentData()
	{
		storeTimes = new ArrayList<HybridTime>();
		globalStateData = new ArrayList<DataSeries<?>>();
		stateNames = new HashMap<String, String>();
	}

	/*
	 * Constructor
	 */
	public EnvironmentData(HashMap<String, String> state_labels)
	{
		storeTimes = new ArrayList<HybridTime>();
		globalStateData = new ArrayList<DataSeries<?>>();
		stateNames = state_labels;
	}
}
