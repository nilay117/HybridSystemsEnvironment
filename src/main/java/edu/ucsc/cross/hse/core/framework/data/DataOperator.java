package edu.ucsc.cross.hse.core.framework.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bs.commons.objects.access.FieldFinder;
import edu.ucsc.cross.hse.core.framework.component.FullComponentOperator;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;

/*
 * The purpose of this class is to protect all of the methods that should not be
 * used by anyone who odesn't know how to use them. These methods are for the
 * processor to do it's job, not for students. They are accesable though, so if
 * you'd like to develop some new stuff I'd say go for it! are not intended for
 * use outside of processing, which is why they are not directly accessable from
 * the component itself. Any additional methods that should not be accessible by
 * users should be defined here. Use caution when using them as they can disrupt
 * functionality of the environment.
 */
public class DataOperator<T> extends FullComponentOperator
{

	// All data operators within the current JVM
	protected static HashMap<Data<?>, DataOperator<?>> dataOperators = new HashMap<Data<?>, DataOperator<?>>();
	Data<T> element;
	/*
	 * This list determines what elements will need to be cloned before being
	 * saved to ensure the right values are stored. For example, a list would
	 * need to be copied because a list of some sort that was saved without
	 * being copied will reflect any changes made in the future unless the list
	 * is reinitialized. A value such as a double can be saved without being
	 * copied because the stored pointer points to the correct saved value even
	 * when a new value is stored. This list below contains general classes that
	 * not need to be cloned to save
	 */
	@SuppressWarnings("rawtypes")
	static public final ArrayList<Class> changableClasses = new ArrayList<Class>(Arrays.asList(new Class[]
	{ Double.class, String.class, Integer.class, Long.class, Number.class, Boolean.class, Enum.class }));

	/*
	 * Constructor to pass the data element along to the component operator for
	 * added functionality. It also adds the data element to the global mapping
	 */
	protected DataOperator(Data<T> component)
	{
		super(component);

		element = component;

	}

	/*
	 * Access the operator using the specified data
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public static <S> DataOperator<S> getOperator(Data<S> data)
	{
		if (dataOperators.containsKey(data))
		{
			return (DataOperator<S>) dataOperators.get(data);

		} else
		{

			DataOperator config = new DataOperator(data);
			dataOperators.put(data, config);
			return config;

		}
	}

	/*
	 * Access to the store data functionality of the data element, which is
	 * blocked to the user since it is handled automatically and there are oter
	 * apis for accessing data
	 */
	public void storeValue(Double time)
	{
		element.storeValue(time, true);
	}

	/*
	 * Same as the above but with the option to overwrite the save time step
	 * increment
	 */
	public void storeValue(Double time, boolean override_save)
	{
		if (!FullComponentOperator.getOperator(component.component().getEnvironment()).outOfAllDomains())
		{
			element.storeValue(time, override_save);
		}
	}

	/*
	 * Same as the above but with the option to overwrite the save time step
	 * increment
	 */
	public void storeValue(HybridTime time, T value)
	{
		// if
		// (!FullComponentOperator.getOperator(component.component().getEnvironment()).outOfAllDomains())
		{
			element.storeValue(time, value);
		}
	}

	/*
	 * Same as the above but with the option to overwrite the save time step
	 * increment
	 */
	public void storeValue(Object value, HybridTime time)
	{
		try
		{
			storeValue(time, (T) value);
		} catch (Exception badVal)
		{

		}
	}

	/*
	 * Flag indicating is data values are being stored over time for this
	 * element
	 */
	public boolean isDataStored()
	{
		return element.save;
	}

	/*
	 * Flag indicating if the data element is a state
	 */
	public boolean isState()
	{
		return FieldFinder.containsSuper(element, State.class);
	}

	/*
	 * Flag indicating that this data should be stored before every jump, which
	 * is the default in states
	 */
	public void storePrejumpData()
	{
		// if (isDataStored())
		{
			// if (FieldFinder.containsSuper(element, State.class))
			{
				// ((State) component).storePreJumpValue();
				element.storePreJumpValue();
			}
		}
	}

	/*
	 * Flag indicating that this data should be stored before every jump, which
	 * is the default in states
	 */
	public void clearPrejumpData()
	{
		// if (isDataStored())
		{
			// if (FieldFinder.containsSuper(element, State.class))
			{
				element.prejump = null;
				// ((State) component).prejump = null;
			}
		}
	}

	public T getPreJumpValue()
	{
		T val = null;
		// if (FieldFinder.containsSuper(element, State.class))
		{
			val = element.prejump;
		}
		return val;
	}

	/*
	 * Allows externally stored data to be loaded if need be
	 */
	public void loadStoredValues(HashMap<HybridTime, T> vals)
	{
		element.savedHybridValues = vals;
	}

	/*
	 * Determine if a copy is required to save a given object
	 * 
	 * @param object - object to be evaluated
	 * 
	 * @return true if a copy needs to be made, false otherwise
	 */
	public static <T> boolean isCopyRequiredOnSave(T object)
	{

		if (object != null)
		{
			return changableClasses.contains(object);
		}
		return true;
	}

	/*
	 * Reset all data for the current data element
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void resetData()
	{
		((Data) component).component().getStoredValues().clear();
		((Data) component).setValue(((Data) component).initialValue);
		((Data) component).elementDomain = (((Data) component).elementDomain);
	}

}