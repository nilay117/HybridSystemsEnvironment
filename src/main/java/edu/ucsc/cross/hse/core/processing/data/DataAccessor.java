package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core2.framework.data.Dat;

public interface DataAccessor
{

	public HashMap<Data, ArrayList<Double[]>> getData(String title);

	public ArrayList<Data> getAllStateData();

	public ArrayList<String> getAllStateNames();

	public ArrayList<Data> getDataByTitle(String title);

	public ArrayList<State> getStatesByTitle(String title);

	public Data getDifferentDataFromSameDataSet(Data data, String title);
}
