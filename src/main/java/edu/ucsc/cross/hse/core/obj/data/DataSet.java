package edu.ucsc.cross.hse.core.obj.data;

import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.access.ObjectManipulator;
import edu.ucsc.cross.hse.core.exe.operator.EnvironmentManager;
import edu.ucsc.cross.hse.core.obj.structure.HybridTime;
import java.util.ArrayList;
import java.util.HashMap;

@Loggable(Loggable.DEBUG)
public class DataSet
{

	private ArrayList<HybridTime> storeTimes;

	public HashMap<ObjectManipulator, DataSeries<Double>> getGlobalStateData()
	{
		return globalStateData;
	}

	public ArrayList<HybridTime> getStoreTimes()
	{
		return storeTimes;
	}

	private EnvironmentManager manager;
	private Double nextStoreTime;

	/*
	 * Constructor to link the central processor
	 * 
	 * @param processor - main environment processor
	 */
	public DataSet(EnvironmentManager manager)
	{
		this.manager = manager;
		nextStoreTime = 0.0;
		storeTimes = new ArrayList<HybridTime>();
		globalStateData = new HashMap<ObjectManipulator, DataSeries<Double>>();

	}

	private HashMap<ObjectManipulator, DataSeries<Double>> globalStateData;

	public EnvironmentManager getManager()
	{
		return manager;
	}
}
