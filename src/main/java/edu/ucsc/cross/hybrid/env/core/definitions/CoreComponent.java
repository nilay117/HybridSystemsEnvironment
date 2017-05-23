package edu.ucsc.cross.hybrid.env.core.definitions;

import edu.ucsc.cross.hybrid.env.core.classifications.ComponentClass;
import edu.ucsc.cross.hybrid.env.core.components.Data;

public enum CoreComponent implements ComponentClass
{
	DATA_ITEM(
		"Single Data Item",
		false),
	DATA_SET(
		"Data Set",
		false),
	BEHAVIOR(
		"Dynamical Model",
		false),
	HYBRID_SYSTEM(
		"Hybrid System",
		false),
	COMPONENT(
		"Component",
		false),
	ENVIRONMENT(
		"Environment",
		false),
	PROCESSOR(
		"Processor",
		false);

	public final String classificationName;
	private boolean storeDefault;

	private CoreComponent(String name, boolean store_default)
	{
		storeDefault = store_default;
		this.classificationName = name;
	}

	public boolean storeDefault()
	{
		return storeDefault;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Class<T> baseClass()
	{
		// TODO Auto-generated method stub
		return (Class<T>) Data.class;
	}

	@Override
	public String baseLabel()
	{
		// TODO Auto-generated method stub
		return classificationName;
	}

	//	public static void changeStoreDefault(ComponentClassification element, boolean store)
	//	{
	//		element.storeDefault = store;
	//	}
	//
	//	@SuppressWarnings("unchecked")
	//	public <T extends Component> Class<T> getComponentClass()
	//	{
	//		return (Class<T>) HybridSystem.class;
	//	}
}
