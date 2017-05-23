package edu.ucsc.cross.hybrid.env.core.properties;

import edu.ucsc.cross.hybrid.env.core.classifications.ComponentClass;

public class ComponentProperties
{

	// Classification
	public Class<?> baseComponentClass; // base component of this class
	protected ComponentClass classification; // classification of the element

	// Addressing
	protected Integer environmentAddress; // address of the environment (0 by default but may be different if running more than one environment simultaneously)
	protected Integer localAddress; // local address of the element ie with respect to the other elements of the parent
	protected Integer globalAddress; // global address of the element ie with respect to the environment
	protected Integer communicationAddress; // address assigned to the element if it is capable of communication

	// Naming
	protected String label; // general description of the element -ie "Storage Device" or "Navigation Controller"
	protected String name;// specific desciption of the element -ie "WD Blue 1TB Internal SSD" or "Crazyflie Nano 2.0"

	public ComponentProperties(String name, ComponentClass classification)
	{
		this.label = name;
		this.classification = (classification);
		this.name = name;
		this.baseComponentClass = classification.baseClass();
	}

	public String getName()
	{
		return label;
	}

	public void setName(String name)
	{
		this.label = name;
	}

	public String getDescription()
	{
		return name;
	}

	public void setDescription(String description)
	{
		this.name = description;
	}

	public Integer getLocalAddress()
	{
		return localAddress;
	}

	public Integer getGlobalAddress()
	{
		return globalAddress;
	}

	public Integer getCommunicationAddress()
	{
		return communicationAddress;
	}

	public ComponentClass getClassification()
	{
		return classification;
	}

	public Class<?> getBaseComponentClass()
	{
		return baseComponentClass;
	}

	public Integer getEnvironmentAddress()
	{
		return environmentAddress;
	}

	public static void setEnvironmentAddress(ComponentProperties element, Integer environmentAddress)
	{
		element.environmentAddress = environmentAddress;
	}

}
