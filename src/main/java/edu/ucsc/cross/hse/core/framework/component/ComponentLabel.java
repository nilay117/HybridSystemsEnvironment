package edu.ucsc.cross.hse.core.framework.component;

/*
 * This class contains information that describes the component, which is used
 * in graphical displays and for filtering.
 */
public class ComponentLabel
{

	// Naming
	protected String title; // general description of the element -ie "Storage
							// Device" or "Navigation Controller"
	protected String name;// specific desciption of the element -ie "WD Blue 1TB
							// Internal SSD" or "Crazyflie Nano 2.0"

	public ComponentLabel(String title)
	{
		this.title = title;
		this.name = title;
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

	public String getFullDescription()
	{
		String desc = name;
		if (!name.equals(title))
		{
			desc = title + " : " + desc;
		}
		return desc;
	}

	public void setDescription(String description)
	{
		this.name = description;
	}

}
