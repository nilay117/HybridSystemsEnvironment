package edu.ucsc.cross.hybrid.env.core.definitions;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ucsc.cross.hybrid.env.core.classification.DataGroup;
import edu.ucsc.cross.hybrid.env.core.classification.DataType;
import edu.ucsc.cross.hybrid.env.core.components.Data;

public enum CoreGroup implements DataGroup
{
	ALL_STATES(
		"All Data",
		new DataType[]
		{ CoreData.DYNAMIC_STATE, CoreData.DISCRETE_STATE, CoreData.PARAMETER, CoreData.PROPERTY }),

	STATE_ELEMENTS(
		"All States",
		new DataType[]
		{ CoreData.DISCRETE_STATE, CoreData.DYNAMIC_STATE }),

	DYNAMIC_STATE_ELEMENTS(
		"Dynamic States",
		new CoreData[]
		{ CoreData.DYNAMIC_STATE });

	public final ArrayList<DataType> subTypes;
	private final String groupLabel;

	private CoreGroup(String label, DataType[] sub_types)
	{
		groupLabel = label;
		subTypes = new ArrayList<DataType>(Arrays.asList(sub_types));
	}

	@SuppressWarnings("rawtypes")
	public boolean containsObj(Data value)
	{
		return subTypes.contains(value.getProperties().getClassification());
	}

	@Override
	public String getTitle()
	{
		// TODO Auto-generated method stub
		return groupLabel;
	}

	@Override
	public ArrayList<DataType> getContents()
	{
		// TODO Auto-generated method stub
		return subTypes;
	}

	@Override
	public boolean contains(Data type)
	{
		// TODO Auto-generated method stub
		return subTypes.contains(type.getDataClass());
	}
}
