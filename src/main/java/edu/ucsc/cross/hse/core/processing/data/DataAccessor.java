package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.framework.data.Data;

public interface DataAccessor
{

	public ArrayList<Data> getAllStateData();

	public ArrayList<String> getAllStateNames();

	public ArrayList<Data> getDataByTitle(String title);

	public Data getDifferentDataFromSameDataSet(Data data, String title);
}
