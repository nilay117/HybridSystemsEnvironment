package edu.ucsc.cross.hse.core.framework.data;

public interface DataType
{

	/*
	 * Determine if the element represented by the data type changes
	 * continuously
	 * 
	 * @return true if the element changes continuously, false otherwise
	 */
	public boolean changesContinuously();

	/*
	 * Determine if previous values of the data type should be stored by
	 * default. This is used when the storage settings are not defined by the
	 * user.
	 * 
	 * @return true if data type is stored by default, false otherwise
	 */
	public boolean storePreviousDataByDefault();

	/*
	 * Determine if the data type represents a state element. A state element is
	 * a data element which changes dynamically
	 * 
	 * @return true if data type represents a sate element, false otherwise
	 */
	public boolean isState();

	/*
	 * Get the title of the data type. Used for display purposes
	 * 
	 * @return title string
	 */
	public String getTitle();

	/*
	 * Constructor with initial value only
	 * 
	 * @param initial_value - initial value of the data element to be created
	 */
	public <T> Data<T> create(T initial_value);

	/*
	 * Constructor with initial value and previous data storage indicator
	 * 
	 * @param initial_value - initial value of the data element to be created
	 * 
	 * @param store_previous - flag indicating if data should be stored or not
	 */
	public <S> Data<S> create(S initial_value, boolean store_previous);

	/*
	 * Constructor with initial value and data type title.
	 * 
	 * @param initial_value - initial value of the data element to be created
	 * 
	 * @param element_title - title of data element, ie "Data Transfer Rate" or
	 * "Vertical Velocity"
	 */
	public <T> Data<T> create(T initial_value, String element_title);

	/*
	 * Constructor with initial value, data type title, and previous data
	 * storage indicator
	 * 
	 * @param initial_value - initial value of the data element to be created
	 * 
	 * @param store_previous - flag indicating if data should be stored or not
	 * 
	 * @param element_title - title of data element, ie "Data Transfer Rate" or
	 * "Vertical Velocity"
	 */
	public <S> Data<S> create(S initial_value, String element_title, boolean stored_by_default);

}
