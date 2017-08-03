package edu.ucsc.cross.hse.core.framework.data;

import java.util.HashMap;

import bs.commons.objects.manipulation.ObjectCloner;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentWorker;
import edu.ucsc.cross.hse.core.framework.environment.EnvironmentOperator;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.object.domain.ValueDomain;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;

/*
 * This class stores and protects any type of non state data used within the
 * environment. It stores any object in a way that protects it, and ensures it's
 * true value can always be accessed from any location. This component is needed
 * as there may be many systems accessing and modifying the same objects. For
 * example, take two systems that share some variable, if one of the systems
 * updates the value, the other system will now be pointing to the old value
 * unless code is implemented to update the value of the other system as well.
 * These extra implementations are impractical as they limit compatibility to
 * explicitly defined components, and would be very difficult to scale. Instead
 * if both systems store the value data structure, the actual object is stored
 * within the structure, thus the true value of the object is accessable by any
 * system with a pointer to the data structure.
 */
public class Data<T> extends Component
{

	/*
	 * Flag indicating if object should be stored
	 */
	protected boolean save;

	/*
	 * Flag indicating if object needs to be cloned to be stored correctly
	 */
	private final boolean cloneToStore;

	/*
	 * Mapping of stored values to their corresponding hybrid times
	 */
	protected HashMap<HybridTime, T> savedHybridValues;

	/*
	 * Current data element
	 */
	protected T element;

	/*
	 * Domain of the data object in case it can assume random values
	 */
	protected ValueDomain elementDomain;

	/*
	 * Initial value of the data element
	 */
	protected T initialValue;

	/*
	 * Pre-jump value stored immediately before jump occurs
	 */
	protected T prejump;

	/*
	 * Safely access element
	 */
	public T getValue()
	{

		return getValue(false);
	}

	/*
	 * Safely access element and randomize if domain permits
	 */
	public T getValue(boolean jump_protection)
	{
		try
		{
			if (jump_protection && component().getEnvironment().isJumpOccurring())
			{
				return prejump;
			} else
			{

				return element;
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return element;

	}

	/*
	 * Safely store element
	 */
	@SuppressWarnings("unchecked")
	public T setValue()
	{
		try
		{
			this.element = (T) elementDomain.getValue();
		} catch (Exception badDomain)
		{

		}
		return element;

	}

	/*
	 * Safely store element
	 */
	public void setValue(T element)
	{
		this.element = element;

	}

	/*
	 * Safely store elements
	 */
	@SuppressWarnings("unchecked")
	public void setValue(T min, T max)
	{
		// if (FieldFinder.containsSuper(min, Double.class))
		// {
		try
		{
			elementDomain = new ValueDomain((Double) min, (Double) max);
			setValue((T) elementDomain.getValue());
		} catch (Exception e)
		{
			setValue(min);
		}
		// }
	}

	/*
	 * Flag indicating if the element is nulls
	 */
	public boolean isElementNull()
	{
		return (getValue() == null);
	}

	/*
	 * Get a set of complementary methods that are intended to make things
	 * easier
	 */
	@Override
	public DataWorker<T> component()
	{
		return DataWorker.getConfigurer(this);
	}

	/*
	 * Store the value
	 */
	private T getStoreValue()
	{
		{
			if (cloneToStore)
			{
				return FileExchanger.cloner.deepClone(getValue());
				// return (T) ObjectCloner.xmlClone(get());
			} else
			{
				return getValue();
			}
		}

	}

	/*
	 * Store the double value
	 */
	void storeValue(Double time)
	{
		storeValue(time, false);
	}

	/*
	 * Store the double overriding the same time increment
	 */
	void storeValue(Double time, boolean override_save)
	{

		if (save || override_save)
		{
			T storeValue = getStoreValue();
			savedHybridValues
			.put(EnvironmentOperator.getOperator(component().getEnvironment()).getEnvironmentHybridTime().getCurrent(),
			storeValue);

		}
	}

	/*
	 * Store the double overriding the same time increment
	 */
	void storeValue(HybridTime time, T value)
	{

		if (save)
		{
			savedHybridValues.put(time, value);

		}
	}

	/*
	 * Instantiate the data class however fits your needs.
	 * 
	 * Mandatory: initialValue
	 * 
	 * Highly Recommended Name or Description for automatic labeling later on
	 * 
	 * Optional: Description is just extra information you want to include, and
	 * the save flag is just if you want to save your car
	 */
	@SuppressWarnings("unchecked")
	public Data(String name, T obj, String description, Boolean save_default)
	{
		super(name, description);
		cloneToStore = !DataOperator.isCopyRequiredOnSave(obj);
		init(save_default, obj);
	}

	@SuppressWarnings("unchecked")
	public Data(String name, T obj, Boolean save_default)
	{
		super(name, "");
		cloneToStore = !DataOperator.isCopyRequiredOnSave(obj);
		init(save_default, obj);
	}

	@SuppressWarnings("unchecked")
	public Data(String name, T obj)
	{
		super(name, "");
		cloneToStore = !DataOperator.isCopyRequiredOnSave(obj);
		init(false, obj);
	}

	@SuppressWarnings("unchecked")
	public Data(String name, T min, T max)
	{
		super(name, "");
		cloneToStore = !DataOperator.isCopyRequiredOnSave(min);
		init(false, min, max);
	}

	/*
	 * Store a copy of a value immediately before a jump occurs allowing
	 * pre=jump value access even if the value is changed by another component
	 */
	void storePreJumpValue()
	{
		prejump = element;
	}

	/*
	 * Initialization that defines if the data will be stored, and a range of
	 * values can be specified
	 */
	@SuppressWarnings("unchecked")
	private void init(Boolean save_default, T... obj)
	{
		element = obj[0];

		save = save_default;

		savedHybridValues = new HashMap<HybridTime, T>();

		if (obj.getClass().equals(Double.class))
		{
			if (obj.length > 1)
			{
				elementDomain = new ValueDomain((Double) obj[0], (Double) obj[1]);
			}
		}
	}

	/*
	 * Setup of initial values and components for the data element
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize()
	{
		// setValue(0.0);
		if (savedHybridValues == null)
		{
			savedHybridValues = new HashMap<HybridTime, T>();
		}
		if (!ComponentWorker.getOperator(this).isInitialized())
		{
			try
			{
				setValue((T) elementDomain.getValue(true));
			} catch (Exception e)
			{
				try
				{
					setValue(element);
				} catch (Exception ee)
				{
					setValue(null);
				}
			}
			try
			{
				initialValue = (T) ObjectCloner.xmlClone(element);
			} catch (Exception ee)
			{
				initialValue = null;
			}
			// initialValue = (T) ObjectCloner.xmlClone(element);
		}
	}
}