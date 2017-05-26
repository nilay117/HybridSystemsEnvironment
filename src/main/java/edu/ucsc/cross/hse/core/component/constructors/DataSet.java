package edu.ucsc.cross.hse.core.component.constructors;

import edu.ucsc.cross.hse.core.component.foundation.Component;

/*
 * This DataSet constructor is for defining a collection of data elements
 * similar to a complete state. It is mainly for organization, expandibility,
 * and creating libraries. A model that has states and properties can be broken
 * up so that the properties can be defined sepeartely and swapped out to
 * describe different components with the same model. For example, a hard drive
 * would have some states like memory used, remaining data to transfer, power
 * consumed, etc, and parameters like transfer speed and maximum capacity. The
 * parameters can be filled in from a data sheet and saved for different models
 * of hard drives, and then loaded back into the set. If eventually the hard
 * drive was mounted onto a vehicle, the data sets can become part of a larger
 * data set without any changes while still being usable on their own.
 */
public abstract class DataSet extends Component// implements
												// Initializer//DataSetProperties
{

	/*
	 * Constructor that gives the set the generic name "Data Set"
	 */
	public DataSet()
	{
		super("Data Set", DataSet.class);
		simulated = true;
	}

	/*
	 * Constructor that allows the user to name the data set
	 */
	public DataSet(String title)
	{
		super(title, DataSet.class);
		simulated = true;
	}

	/*
	 * Constructor that allows the user to name the data set and define if the
	 * contained data is simulated or not
	 */
	public DataSet(String title, boolean simulated)
	{
		super(title, DataSet.class);
		this.simulated = simulated;
	}

}
