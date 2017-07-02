package edu.ucsc.cross.hse.core.framework.data;

import java.lang.reflect.Field;
import java.util.HashMap;

import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.exceptions.UnitException;
import bs.commons.unitvars.units.NoUnit;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.environment.ContentOperator;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.object.domain.ValueDomain;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;

public class Data<T> extends Component// DynamicData<T>
{

	protected boolean save; // flag indicating if object should be stored

	private final boolean cloneToStore; // flag indicating if object needs to be
										// cloned to be stored correctly

	protected HashMap<HybridTime, T> savedHybridValues; // mapping of saved
														// values

	protected T element; // currently stored data object

	protected ValueDomain elementDomain;// domain of the data object in case it
										// can assume random values

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
	public T getValue(boolean randomize_from_domain)
	{
		try
		{
			if (randomize_from_domain)
			{
				element = (T) elementDomain.getValue();

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
	public DataWorker<T> getActions()
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
			savedHybridValues.put(ContentOperator.getOperator(getEnvironment()).getEnvironmentHybridTime().getCurrent(),
			storeValue);

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
	public Data(String name, T obj, String description, Boolean save_default)
	{
		super(name, description);
		cloneToStore = !DataOperator.isCopyRequiredOnSave(obj);
		init(obj, save_default);
	}

	public Data(String name, T obj, Boolean save_default)
	{
		super(name, "");
		cloneToStore = !DataOperator.isCopyRequiredOnSave(obj);
		init(obj, save_default);
	}

	public Data(String name, T obj)
	{
		super(name, "");
		cloneToStore = !DataOperator.isCopyRequiredOnSave(obj);
		init(obj, false);
	}

	private void init(T obj, Boolean save_default)
	{
		element = obj;

		save = save_default;

		savedHybridValues = new HashMap<HybridTime, T>();

		if (obj.getClass().equals(Double.class))
		{
			elementDomain = new ValueDomain((Double) obj);
		}
	}
}