package edu.ucsc.cross.hse.core.framework.component;

/*
 * This class contains information that describes the component, and methods to
 * display this information depending on the application. These are used for
 * graphical displays, filtering, and producing outputs.
 */
public class ComponentLabel
{

	/*
	 * General description of the element -ie "Storage Device" or
	 * "Navigation Controller"
	 */
	protected String classification;

	/*
	 * Specific desciption of the element -ie "WD Blue 1TB Internal SSD" or
	 * "Crazyflie Nano 2.0"
	 */
	protected String name;

	/*
	 * Any extra information about the component
	 */
	protected String information;

	/*
	 * Address of the component
	 */
	protected String address;

	/*
	 * Constructor that defines the title of the component
	 * 
	 * @param title - the title given to this component, preferably defined by
	 * the user, otherwise
	 */
	public ComponentLabel(String title)
	{
		this.classification = title;
		this.name = title;
		address = null;// this.toString();
	}

	/*
	 * Get the general classification of the element
	 */
	public String getClassification()
	{
		return classification;
	}

	/*
	 * Set the general classification of the element
	 */
	public void setClassification(String name)
	{
		this.classification = name;
	}

	/*
	 * Get the specific name of the element
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * Set the specific name of the element
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/*
	 * Get any additional information about the element
	 */
	protected String getInformation()
	{
		return information;
	}

	/*
	 * Set any additional information about the element
	 */
	protected void setInformation(String information)
	{
		this.information = information;
	}

	/*
	 * Get a description of the object that includes both the classification and
	 * the name.
	 * 
	 * @return classification and name combinted
	 */
	public String getFullDescription()
	{
		String desc = classification;
		if (!name.equals(classification) && name.length() > 0)
		{
			desc = desc + " : " + name;
		}
		return desc;
	}

}
