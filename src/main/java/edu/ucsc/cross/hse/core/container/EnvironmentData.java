package edu.ucsc.cross.hse.core.container;

import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.data.HybridArc;
import edu.ucsc.cross.hse.core.file.CSVFileParser;
import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EnvironmentData
{

	private HashMap<ObjectSet, HybridArc<?>> hybridArcMap;
	private ArrayList<HybridTime> hybridTimeDomain;

	public ArrayList<DataSeries<?>> getAllDataSeries()
	{
		if (!seriesListMap.containsKey(this))
		{
			populateListMap(this);
		}
		return seriesListMap.get(this);
	}

	public ArrayList<String> getAllStoredObjectSetNames()
	{
		ArrayList<String> nameOrder = new ArrayList<String>();
		{
			for (DataSeries<?> dataSeries : getAllDataSeries())
			{
				String leg = dataSeries.getParent().extension().getUniqueLabel();
				if (!nameOrder.contains(leg))
				{
					nameOrder.add(leg);
				}
			}
			Collections.sort(nameOrder);
		}
		return nameOrder;
	}

	public HybridTime getEarliestStoredTime()
	{
		HybridTime earliestTime = getLastStoredTime();
		for (HybridTime time : hybridTimeDomain)
		{
			if (time.getTime() < earliestTime.getTime())
			{
				earliestTime = time;
			}
		}
		return earliestTime;
	}

	public HashMap<ObjectSet, HybridArc<?>> getHybridArcMap()
	{
		return hybridArcMap;
	}

	public HybridTime getLastStoredTime()
	{
		HybridTime lastTime = null;
		if (hybridTimeDomain.size() > 0)
		{
			lastTime = hybridTimeDomain.get(hybridTimeDomain.size() - 1);
		}
		return lastTime;
	}

	public ArrayList<HybridTime> getStoreTimes()
	{
		return hybridTimeDomain;
	}

	public void load(EnvironmentData new_data)
	{
		// hybridTimeDomain.clear();
		hybridTimeDomain = (new_data.getStoreTimes());
		hybridArcMap = new_data.getHybridArcMap();
		// EnvironmentData.populateListMap(this);
	}

	public EnvironmentData()
	{
		hybridTimeDomain = new ArrayList<HybridTime>();
		hybridArcMap = (new HashMap<ObjectSet, HybridArc<?>>());
	}

	private static HashMap<EnvironmentData, ArrayList<DataSeries<?>>> seriesListMap = new HashMap<EnvironmentData, ArrayList<DataSeries<?>>>();

	public static void populateListMap(EnvironmentData data)
	{
		seriesListMap.put(data, new ArrayList<DataSeries<?>>());
		for (HybridArc<?> arc : data.hybridArcMap.values())
		{
			for (DataSeries<?> series : arc.getData().values())
			{
				if (!seriesListMap.get(data).contains(series))
				{
					seriesListMap.get(data).add(series);
				}
			}
		}
	}

	public void exportToCSVFile(File output)
	{
		CSVFileParser parser = new CSVFileParser(this);
		parser.createCSVOutput(output);
	}
}
