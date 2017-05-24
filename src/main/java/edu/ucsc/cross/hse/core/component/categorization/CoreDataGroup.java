package edu.ucsc.cross.hse.core.component.categorization;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ucsc.cross.hse.core.component.classification.DataGroup;
import edu.ucsc.cross.hse.core.component.classification.DataType;
import edu.ucsc.cross.hse.core.component.data.Data;

public enum CoreDataGroup implements DataGroup
{
	ALL_DATA(
		"All Data",
		new DataType[]
		{ CoreDataType.HYBRID_STATE, CoreDataType.DISCRETE_STATE, CoreDataType.PARAMETER, CoreDataType.PROPERTY }),

	STATE_ELEMENTS(
		"All States",
		new DataType[]
		{ CoreDataType.DISCRETE_STATE, CoreDataType.HYBRID_STATE }),

	HYBRID_STATE_ELEMENTS(
		"Hybrid States",
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
