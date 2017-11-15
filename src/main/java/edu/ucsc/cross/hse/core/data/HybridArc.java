package edu.ucsc.cross.hse.core.data;

import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class HybridArc<X extends ObjectSet>
{

	private HashMap<Field, DataSeries<?>> data;

	public HashMap<Field, DataSeries<?>> getData()
	{
		return data;
	}

	private ArrayList<HybridTime> storeTimes;
	private X system;

	public HybridArc(X system, ArrayList<HybridTime> store_times)
	{
		this.system = system;
		this.storeTimes = store_times;
		data = new HashMap<Field, DataSeries<?>>();
	}

	public void addSeries(Field field, DataSeries<?> series)
	{
		data.put(field, series);
	}

	public boolean containsSeries(Field field)
	{
		return data.containsKey(field);
	}

	public static <Y extends ObjectSet> HybridArc<Y> createArc(Y system, ArrayList<HybridTime> store_times)
	{
		return new HybridArc<Y>(system, store_times);
	}
}
