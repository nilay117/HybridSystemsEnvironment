package edu.ucsc.cross.hse.core2.framework.data;

/*
 * This class defines the data creation methods for all (current) possible
 * initial condition
 */
public interface DataInstantiator
{

	/*
	 * Constructor with initial value only
	 * 
	 * @param initial_value - initial value of the data element to be created
	 */
	public <T> Dat<T> create(T initial_value);

	/*
	 * Constructor with initial value and previous data storage indicator
	 * 
	 * @param initial_value - initial value of the data element to be created
	 * 
	 * @param store_previous - flag indicating if data should be stored or not
	 */
	public <S> Dat<S> create(S initial_value, boolean store_previous);

	/*
	 * Constructor with initial value and data type title.
	 * 
	 * @param initial_value - initial value of the data element to be created
	 * 
	 * @param element_title - title of data element, ie "Data Transfer Rate" or
	 * "Vertical Velocity"
	 */
	public <T> Dat<T> create(T initial_value, String element_title);

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
	public <S> Dat<S> create(S initial_value, String element_title, boolean stored_by_default);

}
