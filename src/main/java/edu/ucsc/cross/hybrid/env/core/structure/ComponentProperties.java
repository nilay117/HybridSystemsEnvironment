package edu.ucsc.cross.hybrid.env.core.structure;

public class ComponentProperties
{

	protected ComponentClassification classification; // classification of the element
	protected Integer environmentAddress; // address of the environment (0 by default but may be different if running more than one environment simultaneously)
	protected Integer localAddress; // local address of the element ie with respect to the other elements of the parent
	protected Integer globalAddress; // global address of the element ie with respect to the environment
	protected Integer communicationAddress; // address assigned to the element if it is capable of communication
	protected String name; // general description of the element -ie "Storage Device" or "Navigation Controller"
	protected String description;// specific desciption of the element -ie "WD Blue 1TB Internal SSD" or "Crazyflie Nano 2.0"
	protected boolean save; // flag to store element data

	public ComponentProperties(String name, ComponentClassification classification)
	{
		this.name = name;
		this.classification = (classification);
		this.description = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public boolean save()
	{
		return save;
	}

	public void save(boolean save)
	{
		this.save = save;
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

	public ComponentClassification getClassification()
	{
		return classification;
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
