package edu.ucsc.cross.hse.core.component.data;

import edu.ucsc.cross.hse.core.component.categorization.CoreDataType;
import edu.ucsc.cross.hse.core.component.classification.DataType;

public class DataFactory
{

	public static final DataType hybridState = CoreDataType.HYBRID_STATE;

	public static final DataType discreteState = CoreDataType.DISCRETE_STATE;

	public static final DataType parameter = CoreDataType.PARAMETER;

	public static final DataType property = CoreDataType.PROPERTY;

	public static final DataType data = CoreDataType.DATA;

	public <S> Data<S> newData(S obj, DataType type)
	{
		return newData(obj, type.getTitle(), "", type, type.storePreviousDataByDefault());
	}

	public <S> Data<S> newData(S obj, String name, DataType type)
	{
		return newData(obj, name, "", type, type.storePreviousDataByDefault());
	}

	public <S> Data<S> newData(S obj, String name, String description, DataType type)
	{
		return newData(obj, name, description, type, type.storePreviousDataByDefault());
	}

	public <S> Data<S> newData(S obj, String name, String description, DataType type, boolean save_default)
	{
		Data<S> newState = null;
		if (name == null)
		{
			newState = Data.instantiateData(obj, type, type.getTitle(), "", save_default);
		} else
		{
			newState = Data.instantiateData(obj, type, name, "", save_default);
		}

		return newState;

	}
}