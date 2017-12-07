package edu.ucsc.cross.hse.core.container;

import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.data.HybridArc;
import edu.ucsc.cross.hse.core.data.HybridArcData;
import edu.ucsc.cross.hse.core.file.CSVFileParser;
import edu.ucsc.cross.hse.core.monitor.DataMonitor;
import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class EnvironmentData
{

	private HashMap<ObjectSet, HybridArcData<?>> hybridArcMap;
	private ArrayList<HybridTime> hybridTimeDomain;

	public <X extends ObjectSet> HybridArc<X> getSolution(X state)
	{
		if (hybridArcMap.containsKey(state))
		{
			return (HybridArc<X>) hybridArcMap.get(state);
		}
		return null;
	}

	public ArrayList<String> getAllStoredObjectSetNames()
	{
		ArrayList<String> nameOrder = new ArrayList<String>();
		{
			for (DataSeries<?> dataSeries : DataMonitor.getAllDataSeries(this))
			{
				String leg = dataSeries.getParent().data().getUniqueLabel();
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
		hybridArcMap = EnvironmentData.getHybridArcMap(this);
		// EnvironmentData.populateListMap(this);
	}

	public EnvironmentData()
	{
		hybridTimeDomain = new ArrayList<HybridTime>();
		hybridArcMap = (new HashMap<ObjectSet, HybridArcData<?>>());
	}

	public void exportToCSVFile()
	{
		CSVFileParser parser = new CSVFileParser(this);
		parser.createCSVOutput();
	}

	public void exportToCSVFile(File output)
	{
		CSVFileParser parser = new CSVFileParser(this);
		parser.createCSVOutput(output);
	}

	public static HashMap<ObjectSet, HybridArcData<?>> getHybridArcMap(EnvironmentData dat)
	{
		return dat.hybridArcMap;
	}
}
