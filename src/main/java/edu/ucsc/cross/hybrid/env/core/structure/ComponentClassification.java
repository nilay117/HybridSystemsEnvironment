package edu.ucsc.cross.hybrid.env.core.structure;

import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;

public enum ComponentClassification
{
	// Data Clasifications
	DYNAMIC_STATE(
		"Dynamic State",
		true),
	DISCRETE_STATE(
		"Discrete State",
		true),
	PARAMETER(
		"Parameter",
		false),
	PROPERTY(
		"Property",
		false),
	DATA(
		"Data",
		true),

	// Structure Classifications
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

	private ComponentClassification(String name, boolean store_default)
	{
		storeDefault = store_default;
		this.classificationName = name;
	}

	public boolean storeDefault()
	{
		return storeDefault;
	}

	public static void changeStoreDefault(ComponentClassification element, boolean store)
	{
		element.storeDefault = store;
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> Class<T> getComponentClass()
	{
		return (Class<T>) HybridSystem.class;
	}
	//
	//	@Override
	//	public ComponentId getComponentId(String title, String description)
	//	{
	//		// TODO Auto-generated method stub
	//		return ComponentId.get(this, title, description);
	//	}

}
