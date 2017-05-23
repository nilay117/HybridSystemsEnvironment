package edu.ucsc.cross.hybrid.env.core.data;

import edu.ucsc.cross.hybrid.env.core.classifications.DataClass;
import edu.ucsc.cross.hybrid.env.core.definitions.CoreData;
import edu.ucsc.cross.hybrid.env.core.elements.Data;

public class DataFactory
{

	public static final DataClass dynamicState = CoreData.DYNAMIC_STATE;

	public static final DataClass discreteState = CoreData.DISCRETE_STATE;

	public static final DataClass parameter = CoreData.PARAMETER;

	public static final DataClass property = CoreData.PROPERTY;

	public static final DataClass data = CoreData.DATA;

	public <S> Data<S> newData(S obj, DataClass type)
	{
		return newData(obj, type.componentLabel(), "", type, type.isStoredByDefault());
	}

	public <S> Data<S> newData(S obj, String name, DataClass type)
	{
		return newData(obj, name, "", type, type.isStoredByDefault());
	}

	public <S> Data<S> newData(S obj, String name, String description, DataClass type)
	{
		return newData(obj, name, description, type, type.isStoredByDefault());
	}

	public <S> Data<S> newData(S obj, String name, String description, DataClass type, boolean save_default)
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
