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
	Class<X> emptyClass;
	ArrayList<HybridTime> storeTimes;
	X system;

	public void exportToCSVFile(File path)
	{
		CSVFileParser csv = new CSVFileParser(this);
		csv.createCSVOutput(path);
	}

	public X getCurrentObject()
	{
		return system;
	}

	public ArrayList<HybridTime> getDataDomain()
	{
		return storeTimes;
	}

	public X getPoint(HybridTime hybrid_time)
	{
		HybridTime lookupTime = findMatchingTime(hybrid_time);
		return createInstance(lookupTime);
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

	public HybridArc(X system, Class<X> empty_class, ArrayList<HybridTime> store_times)
	{
		this.system = system;
		emptyClass = empty_class;
		storeTimes = (store_times);
		data = new HashMap<Field, DataSeries<?>>();
	}

	public static class HybridArcData<X extends ObjectSet>
	{

		HybridArc<X> arc;

		public HashMap<Field, DataSeries<?>> getData()
		{
			return arc.data;
		}

		public HybridArcData(HybridArc<X> arc)
		{
			this.arc = arc;
		}

		public void addSeries(Field field, DataSeries<?> series)
		{
			arc.data.put(field, series);
		}

		public boolean containsSeries(Field field)
		{
			return arc.data.containsKey(field);
		}

		public static <Y extends ObjectSet> HybridArcData<Y> createArc(Y system, ArrayList<HybridTime> store_times)
		{
			Class<Y> cl = (Class<Y>) system.getClass();
			return new HybridArcData<Y>(new HybridArc<Y>(system, cl, store_times));
		}

		public void setStoreTimes(ArrayList<HybridTime> storeTimes)
		{
			arc.storeTimes = storeTimes;
		}

		public HybridArc<X> getArc()
		{
			return arc;
		}

	}
}
