package edu.ucsc.cross.hse.core.processing.data;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.State;

public interface DataAccessor
{

	/*
	 * Get all data points in an array list of double arrays where the first
	 * value is the time and the second value is the value
	 */
	public HashMap<Data<?>, ArrayList<Double[]>> getData(String title);

	/*
	 * Get a list of all state data
	 */
	public ArrayList<Data<?>> getAllStateData();

	/*
	 * Get a list of all state data names
	 */
	public ArrayList<String> getAllStateNames();

	/*
	 * Get a list of all data that matches a certain title
	 */
	public ArrayList<Data<?>> getDataByTitle(String title);

	/*
	 * Get a list of all states that matches a certain title
	 */
	public ArrayList<State> getStatesByTitle(String title);

	/*
	 * Get a different data element from the same component as the input data
	 * element
	 */
	public Data<?> getDifferentDataFromSameDataSet(Data<?> data, String title);
}
