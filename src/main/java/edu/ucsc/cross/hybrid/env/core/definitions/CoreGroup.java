package edu.ucsc.cross.hybrid.env.core.definitions;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ucsc.cross.hybrid.env.core.classifications.ComponentClass;
import edu.ucsc.cross.hybrid.env.core.classifications.GroupClass;
import edu.ucsc.cross.hybrid.env.core.components.Component;
import edu.ucsc.cross.hybrid.env.core.components.Data;

public enum CoreGroup implements GroupClass
{
	ALL_STATES(
		"All Data",
		new ComponentClass[]
		{ CoreData.DYNAMIC_STATE, CoreData.DISCRETE_STATE, CoreData.PARAMETER, CoreData.PROPERTY }),

	STATE_ELEMENTS(
		"All States",
		new ComponentClass[]
		{ CoreData.DISCRETE_STATE, CoreData.DYNAMIC_STATE }),

	DYNAMIC_STATE_ELEMENTS(
		"Dynamic States",
		new CoreData[]
		{ CoreData.DYNAMIC_STATE });

	public final ArrayList<ComponentClass> subTypes;
	private final String groupLabel;

	private CoreGroup(String label, ComponentClass[] sub_types)
	{
		groupLabel = label;
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
		return groupLabel;
	}

	@Override
	public ArrayList<ComponentClass> getContents()
	{
		// TODO Auto-generated method stub
		return subTypes;
	}

	@Override
	public boolean contains(Component type)
	{
		// TODO Auto-generated method stub
		return subTypes.contains(type.getProperties().getClassification());
	}
}
