package edu.ucsc.cross.hse.core.data;

import edu.ucsc.cross.hse.core.object.HybridSystem;
import java.lang.reflect.Field;
import java.util.HashMap;

public class HybridArc<X>
{

	private HashMap<Field, DataSeries<?>> data;
	private HybridSystem<X> system;

}
