package edu.ucsc.cross.hse.core.component.classification;

import java.util.ArrayList;

import edu.ucsc.cross.hse.core.component.data.Data;

/*
 * This interface is used to define a group of data types for organizational
 * purposes. These groups provide additional details about data elements, making
 * it easier to sort and access data elements. For example, the continuous
 * element group includes only hybrid data elements, which is used by the
 * simulation engine to only include continuous data elements in the ode vector.
 */
public interface DataGroup
{

	/*
	 * Get the title of the data group. Used for display purposes
	 * 
	 * @return title string
	 */
	public String getTitle();

	/*
	 * Get the data types included in the group
	 * 
	 * @return list of data types included in the group
	 */
	public ArrayList<DataType> getContents();

	/*
	 * Determine if a data element belongs to the group
	 * 
	 * @return true if the data element is part of the group, false otherwise
	 */
	public boolean contains(Data type);

}
