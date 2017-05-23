package edu.ucsc.cross.hybrid.env.core.accessability;

public class Identifier
{

	// Classification
	public Class<?> baseComponentClass; // base component of this class
	protected Class<?> classification; // classification of the element

	// Addressing
	protected Integer environmentAddress; // address of the environment (0 by default but may be different if running more than one environment simultaneously)
	protected Integer localAddress; // local address of the element ie with respect to the other elements of the parent
	protected Integer globalAddress; // global address of the element ie with respect to the environment
	protected Integer communicationAddress; // address assigned to the element if it is capable of communication

	// Naming
	protected String title; // general description of the element -ie "Storage Device" or "Navigation Controller"
	protected String name;// specific desciption of the element -ie "WD Blue 1TB Internal SSD" or "Crazyflie Nano 2.0"

	public Identifier(String title, Class<?> classification)
	{
		this.title = title;
		this.classification = (classification);
		this.name = title;
		this.baseComponentClass = classification;
	}

	public String getName()
	{
		return title;
	}

	public void setName(String name)
	{
		this.title = name;
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

	public Class<?> getClassification()
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

	public static void setEnvironmentAddress(Identifier element, Integer environmentAddress)
	{
		element.environmentAddress = environmentAddress;
	}

}
