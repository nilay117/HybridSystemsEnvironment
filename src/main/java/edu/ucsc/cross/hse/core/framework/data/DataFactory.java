package edu.ucsc.cross.hse.core.framework.data;

public class DataFactory
{

	public static final DataInstantiator state = BaseDataType.STATE;

	public static final DataInstantiator data = BaseDataType.DATA;

	protected static <S> Data<S> newData(S obj, DataTypeProperties type)
	{
		return newData(obj, type.getTitle(), "", type, type.storeDataByDefault());
	}

	protected static <S> Data<S> newData(S obj, String name, DataTypeProperties type)
	{
		return newData(obj, name, name, type, type.storeDataByDefault());
	}

	protected static <S> Data<S> newData(S obj, String name, String description, DataTypeProperties type)
	{
		return newData(obj, name, description, type, type.storeDataByDefault());
	}

	protected static <S> Data<S> newData(S obj, String name, String description, DataTypeProperties type,
	boolean save_default)
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

	protected static <S> Data<S> instantiateData(S obj, DataTypeProperties type, String name, String description,
	Boolean save_default)
	{
		Data<S> newData = new Data<S>(obj, type, name, description, save_default);// type,
																					// name,
																					// description);
		return newData;
		// TODO Auto-generated constructor stub
	}
}
