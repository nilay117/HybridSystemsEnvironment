package edu.ucsc.cross.hse.core.data;

import edu.ucsc.cross.hse.core.file.CSVFileParser;
import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class HybridArc<X extends ObjectSet>
{

	HashMap<Field, DataSeries<?>> data;
	ArrayList<Double> times;
	ArrayList<Integer> jumps;
	ArrayList<HybridTime> storeTimes;
	Class<X> emptyClass;
	X system;

	public HybridArc(X system, Class<X> empty_class, ArrayList<HybridTime> store_times)
	{
		this.system = system;
		emptyClass = empty_class;
		storeTimes = (store_times);
		data = new HashMap<Field, DataSeries<?>>();
	}

	public HashMap<HybridTime, X> getTrajectory()
	{
		HashMap<HybridTime, X> solution = new HashMap<HybridTime, X>();

		for (HybridTime ht : storeTimes)
		{
			X newInstance = createInstance(ht);
			if (newInstance != null)
			{
				solution.put(ht, newInstance);
			}
		}

		return solution;
	}

	public X getPoint(HybridTime hybrid_time)
	{
		HybridTime lookupTime = findMatchingTime(hybrid_time);
		return createInstance(lookupTime);
	}

	private HybridTime findMatchingTime(HybridTime hybrid_time)
	{
		if (storeTimes.contains(hybrid_time))
		{
			return hybrid_time;
		} else
		{
			for (HybridTime time : storeTimes)
			{
				if (time.getJumps().equals(hybrid_time.getJumps()) && time.getTime() == hybrid_time.getTime())
				{
					return time;
				}
			}
		}
		return null;
	}

	private X createInstance(HybridTime hybrid_time)
	{
		X newInstance = null;
		try
		{
			newInstance = emptyClass.newInstance();

			for (Field field : data.keySet())
			{
				try
				{
					field.set(newInstance, data.get(field).getStoredData(hybrid_time));
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} catch (Exception ee)
		{
			// TODO Auto-generated catch block
			ee.printStackTrace();
		}
		return newInstance;
	}

	public X getCurrent()
	{
		return system;
	}

	public void exportCSV(File path)
	{
		CSVFileParser csv = new CSVFileParser(this);
		csv.createCSVOutput(path);
	}
}
