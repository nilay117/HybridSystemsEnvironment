package edu.ucsc.cross.hybrid.env.core.definitions;

import edu.ucsc.cross.hybrid.env.core.classifications.DataClass;
import edu.ucsc.cross.hybrid.env.core.components.Data;
import edu.ucsc.cross.hybrid.env.core.constructors.DataFactory;

public enum CoreData implements DataClass// ComponenClassification
{
	DYNAMIC_STATE(
		"Dynamic State",
		true,
		true),
	DISCRETE_STATE(
		"Discrete State",
		true,
		true),
	PARAMETER(
		"Parameter",
		false,
		false),
	PROPERTY(
		"Property",
		false,
		false),
	DATA(
		"Data",
		false,
		false);

	public final String dataTypeName;
	private boolean storeDefault;
	public final boolean state;
	private static DataFactory dataFactory = new DataFactory();

	private CoreData(String name, boolean store_default, boolean state)
	{
		storeDefault = store_default;
		dataTypeName = name;
		this.state = state;
	}

	@Override
	public boolean isStoredByDefault()
	{
		return storeDefault;
	}

	@Override
	public boolean isState()
	{
		return state;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Class<T> baseClass()
	{
		// TODO Auto-generated method stub
		return (Class<T>) Data.class;
	}

	@Override
	public <T> Data<T> create(T initial_value)
	{
		// TODO Auto-generated method stub
		return dataFactory.newData(initial_value, this.baseLabel(), "", this, this.isStoredByDefault());
	}

	@Override
	public <T> Data<T> create(T initial_value, boolean stored_by_default)
	{
		// TODO Auto-generated method stub
		return dataFactory.newData(initial_value, this.baseLabel(), "", this, stored_by_default);
	}

	@Override
	public <T> Data<T> create(T initial_value, String label)
	{
		// TODO Auto-generated method stub
		return dataFactory.newData(initial_value, label, "", this, this.isStoredByDefault());
	}

	@Override
	public <T> Data<T> create(T initial_value, String label, boolean stored_by_default)
	{
		// TODO Auto-generated method stub
		return dataFactory.newData(initial_value, label, "", this, stored_by_default);
	}

	@Override
	public <S> Data<S> create(S initial_value, String label, String name)
	{
		// TODO Auto-generated method stub
		return dataFactory.newData(initial_value, label, name, this, this.isStoredByDefault());
	}

	@Override
	public <S> Data<S> create(S initial_value, String label, String name, boolean stored_by_default)
	{
		// TODO Auto-generated method stub
		return dataFactory.newData(initial_value, label, name, this, stored_by_default);
	}

	@Override
	public String baseLabel()
	{
		// TODO Auto-generated method stub
		return dataTypeName;
	}

	@Override
	public boolean changesContinuously()
	{
		// TODO Auto-generated method stub
		if (this.equals(CoreData.DYNAMIC_STATE))
		{
			return true;
		} else
		{
			return false;
		}
	}

}
