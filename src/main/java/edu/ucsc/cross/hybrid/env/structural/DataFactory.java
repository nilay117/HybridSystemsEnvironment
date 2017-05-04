package edu.ucsc.cross.hybrid.env.structural;

import edu.ucsc.cross.hybrid.env.core.components.Data;

public abstract class DataFactory
{

	public static final DataClassification dynamicState = BaseData.DYNAMIC_STATE;

	public static final DataClassification discreteState = BaseData.DISCRETE_STATE;

	public static final DataClassification parameter = BaseData.PARAMETER;

	public static final DataClassification property = BaseData.PROPERTY;

	public static final DataClassification data = BaseData.DATA;

	protected static <S> Data<S> newData(S obj, DataClassification type)
	{
		return DataFactory.newData(obj, type.componentLabel(), "", type, type.isStoredByDefault());
	}

	protected static <S> Data<S> newData(S obj, String name, DataClassification type)
	{
		return DataFactory.newData(obj, name, "", type, type.isStoredByDefault());
	}

	protected static <S> Data<S> newData(S obj, String name, String description, DataClassification type)
	{
		return DataFactory.newData(obj, name, description, type, type.isStoredByDefault());
	}

	protected static <S> Data<S> newData(S obj, String name, String description, DataClassification type,
	boolean save_default)
	{
		Data<S> newState = null;
		if (name == null)
		{
			newState = Data.instantiateData(obj, type, type.componentLabel(), "", save_default);
		} else
		{
			newState = Data.instantiateData(obj, type, name, "", save_default);
		}

		return newState;

	}

}
