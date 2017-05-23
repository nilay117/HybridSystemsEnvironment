package edu.ucsc.cross.hybrid.env.core.components;

import edu.ucsc.cross.hybrid.env.core.definitions.CoreComponent;

public abstract class DataSet extends Component// implements Initializer//DataSetProperties
{

	private boolean simulated;

	/*
	 * Constructor that allows the user to name the data set
	 */
	public DataSet()
	{
		super("Data Set", CoreComponent.DATA_SET);
		simulated(true);
	}

	/*
	 * Constructor that allows the user to name the data set
	 */
	public DataSet(String title)
	{
		super(title, CoreComponent.DATA_SET);
		simulated(true);
	}

	/*
	 * Constructor that assigns generic name
	 */
	public DataSet(String title, boolean simulated)
	{
		super(title, CoreComponent.DATA_SET);
		simulated(simulated);
	}

	public boolean simulate()
	{
		return simulated;
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void initializeElements()
	{
		for (Component component : this.getAllllComponents(true))
		{
			try
			{
				//	Data element = (Data) component;
				//Data.isSimulated(element, this.simulate());
			} catch (Exception nonElement)
			{
				try
				{
					DataSet elements = (DataSet) component;
					elements.simulated(this.simulate());
					//elements.initializeElements();
				} catch (Exception nonElements)
				{

				}
			}
		}
	}

	public void simulated(boolean simulate)
	{
		this.simulated = simulate;
	}

}
