package edu.ucsc.cross.hybrid.env.core.definitions;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ucsc.cross.hybrid.env.core.classifiers.DataGroup;
import edu.ucsc.cross.hybrid.env.core.classifiers.DataType;
import edu.ucsc.cross.hybrid.env.core.constructors.Data;

public enum CoreDataGroup implements DataGroup
{
	ALL_STATES(
		"All Data",
		new DataType[]
		{ CoreDataType.HYBRID_STATE, CoreDataType.DISCRETE_STATE, CoreDataType.PARAMETER, CoreDataType.PROPERTY }),

	STATE_ELEMENTS(
		"All States",
		new DataType[]
		{ CoreDataType.DISCRETE_STATE, CoreDataType.HYBRID_STATE }),

	DYNAMIC_STATE_ELEMENTS(
		"Dynamic States",
		new CoreDataType[]
		{ CoreDataType.HYBRID_STATE });

	public final ArrayList<DataType> subTypes;
	private final String groupLabel;

	private CoreDataGroup(String label, DataType[] sub_types)
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
