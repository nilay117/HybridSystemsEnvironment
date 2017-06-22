package edu.ucsc.cross.hse.core2.framework.data;

import edu.ucsc.cross.hse.core.framework.data.BaseDataType;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataTypeProperties;

public class DataFactory
{

	public static final DataTypeProperties hybridState = BaseDataType.STATE;

	public static final DataTypeProperties discreteState = BaseDataType.DISCRETE_STATE;

	public static final DataTypeProperties parameter = BaseDataType.PARAMETER;

	public static final DataTypeProperties property = BaseDataType.PROPERTY;

	public static final DataTypeProperties data = BaseDataType.DATA;

	public <S> Data<S> newData(S obj, DataTypeProperties type)
	{
		return newData(obj, type.getTitle(), "", type, type.storePreviousDataByDefault());
	}

	public <S> Data<S> newData(S obj, String name, DataTypeProperties type)
	{
		return newData(obj, name, name, type, type.storePreviousDataByDefault());
	}

	public <S> Data<S> newData(S obj, String name, String description, DataTypeProperties type)
	{
		return newData(obj, name, description, type, type.storePreviousDataByDefault());
	}

	public <S> Data<S> newData(S obj, String name, String description, DataTypeProperties type, boolean save_default)
	{
		Data<S> newState = null;
		if (name == null)
		{
			newState = DataFactory.instantiateData(obj, type, name, type.getTitle(), save_default);
		} else
		{
			newState = DataFactory.instantiateData(obj, type, name, description, save_default);
		}

		return newState;

	}

	public static <S> Data<S> instantiateData(S obj, DataTypeProperties type, String name, String description,
	Boolean save_default)
	{
		Data<S> newData = DataInstantiator.instantiateData(obj, type, name, description, save_default);// type,
		// name,
		// description);
		return newData;
		// TODO Auto-generated constructor stub
	}

	private static class DataInstantiator extends Data
	{

		protected DataInstantiator(Object obj, DataTypeProperties type, String name, String description, Boolean save_default)
		{
			super(obj, type, name, description, save_default);
			// TODO Auto-generated constructor stub
		}

		public static <S> Data<S> instantiateData(S obj, DataTypeProperties type, String name, String description,
		Boolean save_default)
		{
			Data<S> newData = instantiate(obj, type, name, description, save_default);// type,
																						// name,
																						// description);
			return newData;
			// TODO Auto-generated constructor stub
		}
	}
}
