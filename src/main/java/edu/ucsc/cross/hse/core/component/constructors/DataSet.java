package edu.ucsc.cross.hse.core.component.constructors;

import bs.commons.objects.manipulation.ObjectCloner;

public abstract class DataSet extends Component// implements
												// Initializer//DataSetProperties
{

	private boolean simulated;
	private DataSet prejump;

	public void storePrejump()
	{
		setPrejump((DataSet) ObjectCloner.xmlClone(this));
	}

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

	/*
	 * Flag indicating whether the data should be simulated or not
	 */
	public boolean isSimulated()
	{
		return simulated;
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void initializeElements()
	{
		for (Component component : this.getComponents(true))
		{
			try
			{
				// Data element = (Data) component;
				// Data.isSimulated(element, this.simulate());
			} catch (Exception nonElement)
			{
				try
				{
					DataSet elements = (DataSet) component;
					// elements.setSimulated(this.isSimulated());
					// elements.initializeElements();
				} catch (Exception nonElements)
				{

				}
			}
		}
	}

	/*
	 * Toggles whether the data contained in the data set is simulated or not
	 */
	public static void setSimulated(DataSet data_set, boolean simulate)
	{
		boolean uninitialized = true;
		if (Component.isInitialized(data_set) != null)
		{
			uninitialized = uninitialized && !Component.isInitialized(data_set);

		}
		if (uninitialized)
		{
			data_set.simulated = (simulate);
		} else
		{

		}
	}

	public DataSet getPrejump()
	{
		return prejump;
	}

	public void setPrejump(DataSet prejump)
	{
		this.prejump = prejump;
	}

}
