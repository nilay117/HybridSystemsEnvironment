package edu.ucsc.cross.hse.core.framework.data;

import java.util.ArrayList;
import java.util.HashMap;

import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.object.domain.ValueDomain;

/*
 * This class contains the methods available to users that perform a variety of
 * data related tasks. These methods are safe to use whenever needed as they do
 * not interfere with the processor. Be careful not to violate the constraints
 * of your system though, if a component does not store data it should not have
 * access to it.
 */
public class DataWorker<T> extends ComponentOperator
{

	/*
	 * Mapping of all data workers keeping them completely separate from the
	 * data elements themselves
	 */
	protected static HashMap<Data<?>, DataWorker<?>> dataActions = new HashMap<Data<?>, DataWorker<?>>();

	/*
	 * Mapping of all stored data times
	 */
	public HashMap<Double, ArrayList<HybridTime>> storedTimes;

	/*
	 * The data element being serviced by this worker
	 */
	public Data<T> data;

	/*
	 * Constructor that stores the data element and also passes it along to the
	 * component worker, which allows it to have that functionality as well
	 */
	public DataWorker(Data<T> data)
	{
		super(data);
		this.data = data;
	}

	/*
	 * Gets all of the stored values (if there are any) for a specific point in
	 * time. Since we are working with hybrid systems it is possible to have
	 * multiple data points at te same time instant
	 */
	public HashMap<HybridTime, T> getStoredValues()
	{
		return data.savedHybridValues;
	}

	/*
	 * Get a value (if there is one) for a given hybrid time
	 */
	public T getStoredValue(HybridTime hybrid_time)
	{
		T val = this.getStoredValues().get(hybrid_time);
		return val;
	}

	/*
	 * Specify if values should be stored for this data element
	 */
	public void setStorePreviousValues(boolean store)
	{
		data.save = store;
	}

	/*
	 * Access the value domainof the data element
	 */
	public ValueDomain getValueDomain()
	{
		return data.elementDomain;
	}

	/*
	 * Access to this worker
	 */
	@SuppressWarnings("unchecked")
	public static <S> DataWorker<S> getConfigurer(Data<S> component)
	{
		if (dataActions.containsKey(component))
		{
			return (DataWorker<S>) dataActions.get(component);

		} else
		{

			DataWorker<S> config = new DataWorker<S>(component);
			dataActions.put(component, config);
			return config;

		}
	}
}
