package edu.ucsc.cross.hse.core.framework.data;

import java.util.HashMap;

import bs.commons.objects.manipulation.ObjectCloner;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.FullComponentOperator;
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

	protected boolean save; // flag indicating if object should be stored

	private final boolean cloneToStore; // flag indicating if object needs to be
										// cloned to be stored correctly

	protected HashMap<HybridTime, T> savedHybridValues; // mapping of saved
														// values

	protected T element; // currently stored data object

	protected ValueDomain elementDomain;// domain of the data object in case it
										// can assume random values

	protected T initialValue;

	protected T prejump; // pre-jump value stored immediately before jump
	// occurs

	/*
	 * Safely access element
	 */
	public T getValue()
	{

		return element;
	}

	/*
	 * Safely access element and randomize if domain permits
	 */
	@SuppressWarnings("unchecked")
	public T getValue(boolean randomize_from_domain)
	{
		try
		{
			if (randomize_from_domain)
			{
				element = (T) elementDomain.getValue();

			} else if (component().getEnvironment().isJumpOccurring())
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

	@SuppressWarnings("unchecked")
	@Override
	public void initialize()
	{
		// setValue(0.0);
		if (savedHybridValues == null)
		{
			savedHybridValues = new HashMap<HybridTime, T>();
		}
		if (!FullComponentOperator.getOperator(this).isInitialized())
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