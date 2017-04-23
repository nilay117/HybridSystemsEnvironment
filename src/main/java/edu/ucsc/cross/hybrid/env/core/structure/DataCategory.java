package edu.ucsc.cross.hybrid.env.core.structure;

import java.util.ArrayList;
import java.util.Arrays;

import edu.ucsc.cross.hybrid.env.core.components.Data;

public enum DataCategory
{
	ALL_STATES(
		new ComponentClassification[]
		{ ComponentClassification.DISCRETE_STATE, ComponentClassification.DYNAMIC_STATE, ComponentClassification.DATA, ComponentClassification.PARAMETER, ComponentClassification.PROPERTY }),

	STATE_ELEMENTS(
		new ComponentClassification[]
		{ ComponentClassification.DISCRETE_STATE, ComponentClassification.DYNAMIC_STATE }),
	DYNAMIC_STATE_ELEMENTS(
		new ComponentClassification[]
		{ ComponentClassification.DYNAMIC_STATE });

	public final ArrayList<ComponentClassification> subTypes;

	private DataCategory(ComponentClassification[] sub_types)
	{
		subTypes = new ArrayList<ComponentClassification>(Arrays.asList(sub_types));
	}

	@SuppressWarnings("rawtypes")
	public boolean containsObj(Data value)
	{
		return subTypes.contains(value.getProperties().getClassification());
	}
}
