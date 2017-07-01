package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.Obj;

public interface DataAccessor
{

	public ArrayList<Obj> getAllStateData();

	public ArrayList<String> getAllStateNames();

	public ArrayList<Obj> getDataByTitle(String title);

	public Obj getDifferentDataFromSameDataSet(Obj data, String title);
}
