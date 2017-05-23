package edu.ucsc.cross.hybrid.env.core.definitions;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ucsc.cross.hybrid.env.core.classifications.GroupClass;
import edu.ucsc.cross.hybrid.env.core.elements.Data;
import edu.ucsc.cross.hybrid.env.core.classifications.ComponentClass;

public enum CoreGroup implements GroupClass
{
	ALL_STATES(
		new ComponentClass[]
		{ CoreData.DYNAMIC_STATE, CoreData.DISCRETE_STATE, CoreData.PARAMETER, CoreData.PROPERTY }),

	STATE_ELEMENTS(
		new ComponentClass[]
		{ CoreData.DISCRETE_STATE, CoreData.DYNAMIC_STATE }),

	DYNAMIC_STATE_ELEMENTS(
		new CoreData[]
		{ CoreData.DYNAMIC_STATE });

	public final ArrayList<ComponentClass> subTypes;

	private CoreGroup(ComponentClass[] sub_types)
	{
		subTypes = new ArrayList<ComponentClass>(Arrays.asList(sub_types));
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
		return null;
	}

	@Override
	public ArrayList<ComponentClass> getContents()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(Data type)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
