package edu.ucsc.cross.hse.core.framework.data;

import java.util.ArrayList;
import java.util.Arrays;

public enum CoreDataGroup implements DataGroup
{
	ALL_DATA( // All data types defined thus far
		"All Data",
		new DataType[]
		{ CoreDataType.HYBRID_STATE, CoreDataType.DISCRETE_STATE, CoreDataType.PARAMETER, CoreDataType.PROPERTY }),

	STATE_ELEMENTS( // Only state element data types
		"All States",
		new DataType[]
		{ CoreDataType.DISCRETE_STATE, CoreDataType.HYBRID_STATE }),

	HYBRID_STATE_ELEMENTS( // Only hybrid state element
		"Hybrid States",
		new CoreDataType[]
		{ CoreDataType.HYBRID_STATE });

	public final ArrayList<DataType> subTypes;
	private final String groupTitle;

	/*
	 * Enum constructor
	 * 
	 * @param title - title of data group
	 * 
	 * @param sub_types - array of data types that are included in the group
	 * 
	 */
	private CoreDataGroup(String title, DataType[] sub_types)
	{
		groupTitle = title;
		subTypes = new ArrayList<DataType>(Arrays.asList(sub_types));
	}

	/*
	 * Get the title of the data group. Used for display purposes
	 * 
	 * @return title string
	 */
	@Override
	public String getTitle()
	{
		// TODO Auto-generated method stub
		return groupTitle;
	}

	/*
	 * Get the data types included in the group
	 * 
	 * @return list of data types included in the group
	 */
	@Override
	public ArrayList<DataType> getContents()
	{
		// TODO Auto-generated method stub
		return subTypes;
	}

	/*
	 * Determine if a data element belongs to the group
	 * 
	 * @return true if the data element is part of the group, false otherwise
	 */
	@Override
	public boolean contains(Data type)
	{
		// TODO Auto-generated method stub
		return subTypes.contains(type.getActions().getDataClass());// subTypes.contains(type.getDataClass());
	}
}
