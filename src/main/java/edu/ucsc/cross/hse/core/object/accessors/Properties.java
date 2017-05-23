package edu.ucsc.cross.hse.core.object.accessors;

public class Properties
{

	// Classification
	public Class<?> baseComponentClass; // base component of this class
	protected Class<?> classification; // classification of the element

	// Naming
	protected String title; // general description of the element -ie "Storage
							// Device" or "Navigation Controller"
	protected String name;// specific desciption of the element -ie "WD Blue 1TB
							// Internal SSD" or "Crazyflie Nano 2.0"

	public Properties(String title, Class<?> classification)
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

	public Class<?> getClassification()
	{
		return classification;
	}

	public Class<?> getBaseComponentClass()
	{
		return baseComponentClass;
	}

}
