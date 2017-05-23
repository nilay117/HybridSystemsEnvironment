package edu.ucsc.cross.hybrid.env.core.constructors;

import edu.ucsc.cross.hybrid.env.core.classification.DataType;
import edu.ucsc.cross.hybrid.env.core.components.Data;
import edu.ucsc.cross.hybrid.env.core.definitions.CoreData;

public class DataFactory
{

	public static final DataType dynamicState = CoreData.DYNAMIC_STATE;

	public static final DataType discreteState = CoreData.DISCRETE_STATE;

	public static final DataType parameter = CoreData.PARAMETER;

	public static final DataType property = CoreData.PROPERTY;

	public static final DataType data = CoreData.DATA;

	public <S> Data<S> newData(S obj, DataType type)
	{
		return newData(obj, type.baseLabel(), "", type, type.isStoredByDefault());
	}

	public <S> Data<S> newData(S obj, String name, DataType type)
	{
		return newData(obj, name, "", type, type.isStoredByDefault());
	}

	public <S> Data<S> newData(S obj, String name, String description, DataType type)
	{
		return newData(obj, name, description, type, type.isStoredByDefault());
	}

	public <S> Data<S> newData(S obj, String name, String description, DataType type, boolean save_default)
	{
		Data<S> newState = null;
		if (name == null)
		{
			newState = Data.instantiateData(obj, type, type.baseLabel(), "", save_default);
		} else
		{
			newState = Data.instantiateData(obj, type, name, "", save_default);
		}

		return newState;

	}
}
