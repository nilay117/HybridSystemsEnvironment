package edu.ucsc.cross.hse.core.data;

import edu.ucsc.cross.hse.core.object.ObjectSet;
import edu.ucsc.cross.hse.core.time.HybridTime;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class HybridArcData<X extends ObjectSet> extends HybridArc<X>
{

	public HashMap<Field, DataSeries<?>> getData()
	{
		return data;
	}

	public HybridArcData(X system, Class<X> empty_class, ArrayList<HybridTime> store_times)
	{
		super(system, empty_class, store_times);
	}

	public void addSeries(Field field, DataSeries<?> series)
	{
		data.put(field, series);
	}

	public boolean containsSeries(Field field)
	{
		return data.containsKey(field);
	}

	public static <Y extends ObjectSet> HybridArcData<Y> createArc(Y system, ArrayList<HybridTime> store_times)
	{
		Class<Y> cl = (Class<Y>) system.getClass();
		return new HybridArcData<Y>(system, cl, store_times);
	}

	public void setStoreTimes(ArrayList<HybridTime> storeTimes)
	{
		this.storeTimes = storeTimes;
	}

}
