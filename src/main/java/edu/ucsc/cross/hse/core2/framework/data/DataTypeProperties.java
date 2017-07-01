package edu.ucsc.cross.hse.core2.framework.data;

public interface DataTypeProperties
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
	public boolean storeDataByDefault();

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

}
