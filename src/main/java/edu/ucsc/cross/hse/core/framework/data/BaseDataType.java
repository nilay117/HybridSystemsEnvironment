package edu.ucsc.cross.hse.core.framework.data;

public enum BaseDataType implements DataTypeProperties, DataInstantiator// ComponenClassification
{
	STATE( // State data element that changes continuously and can change
			// discretely as well
		"State",
		true, // is stored by default
		true,
		true), // is a state
	DATA(
		"Data", // Contains some type of data used within the environment
		false, // is not stored by default
		false,
		false); // is not a state

	public final String dataTypeTitle; // title
	private boolean storePreviousByDefault; // storage flag
	public final boolean representsState; // state flag
	private boolean changesContinuously;
	// element
	// factory

	/*
	 * Enum constructor
	 * 
	 * @param title - title of data type
	 * 
	 * @param store_previous - flag indicating if data should be stored or not
	 * 
	 * @param state - flag indicating if data type represents state element
	 */
	private BaseDataType(String title, boolean store_previous, boolean state, boolean continuous)
	{
		storePreviousByDefault = store_previous;
		dataTypeTitle = title;
		this.representsState = state;
		changesContinuously = continuous;
	}

	/*
	 * Determine if the element represented by the data type changes
	 * continuously
	 * 
	 * @return true if the element changes continuously, false otherwise
	 */
	@Override
	public boolean changesContinuously()
	{
		// TODO Auto-generated method stub
		return changesContinuously;
	}

	/*
	 * Determine if previous values of the data type should be stored by
	 * default. This is used when the storage settings are not defined by the
	 * user.
	 * 
	 * @return true if data type is stored by default, false otherwise
	 */
	@Override
	public boolean storeDataByDefault()
	{
		return storePreviousByDefault;
	}

	/*
	 * Determine if the data type represents a state element. A state element is
	 * a data element which changes dynamically
	 * 
	 * @return true if data type represents a sate element, false otherwise
	 */
	@Override
	public boolean isState()
	{
		return representsState;
	}

	/*
	 * Get the title of the data type. Used for display purposes
	 * 
	 * @return title string
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getTitle()
	{
		// TODO Auto-generated method stub
		return dataTypeTitle;
	}

	/*
	 * Constructor with initial value only
	 * 
	 * @param initial_value - initial value of the data element to be created
	 */
	@Override
	public <T> Data<T> create(T initial_value)
	{
		// TODO Auto-generated method stub
		return DataFactory.newData(initial_value, this.getTitle(), "", this, this.storeDataByDefault());
	}

	/*
	 * Constructor with initial value and previous data storage indicator
	 * 
	 * @param initial_value - initial value of the data element to be created
	 * 
	 * @param store_data - flag indicating if data should be stored or not
	 */
	@Override
	public <T> Data<T> create(T initial_value, boolean stored_by_default)
	{
		// TODO Auto-generated method stub
		return DataFactory.newData(initial_value, this.getTitle(), "", this, stored_by_default);
	}

	@Override
	public <T> Data<T> create(T initial_value, String label)
	{
		// TODO Auto-generated method stub
		return DataFactory.newData(initial_value, label, label, this, this.storeDataByDefault());
	}

	/*
	 * Constructor with initial value, data type title, and previous data
	 * storage indicator
	 * 
	 * @param initial_value - initial value of the data element to be created
	 * 
	 * @param store_data - flag indicating if data should be stored or not
	 * 
	 * @param element_title - title of data element, ie "Data Transfer Rate" or
	 * "Vertical Velocity"
	 */
	@Override
	public <T> Data<T> create(T initial_value, String label, boolean stored_by_default)
	{
		// TODO Auto-generated method stub
		return DataFactory.newData(initial_value, label, "", this, stored_by_default);
	}

}
