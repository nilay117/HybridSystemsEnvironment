package edu.ucsc.cross.hybrid.env.structural;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ucsc.cross.hybrid.env.core.components.Data;

public enum BaseGroup implements ComponentGroup
{
	ALL_STATES(
		new ComponenDefinition[]
		{ BaseData.DYNAMIC_STATE, BaseData.DISCRETE_STATE, BaseData.PARAMETER, BaseData.PROPERTY }),

	STATE_ELEMENTS(
		new ComponenDefinition[]
		{ BaseData.DISCRETE_STATE, BaseData.DYNAMIC_STATE }),

	DYNAMIC_STATE_ELEMENTS(
		new BaseData[]
		{ BaseData.DYNAMIC_STATE });

	public final ArrayList<ComponenDefinition> subTypes;

	private BaseGroup(ComponenDefinition[] sub_types)
	{
		subTypes = new ArrayList<ComponenDefinition>(Arrays.asList(sub_types));
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
	public ArrayList<ComponenDefinition> getContents()
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
