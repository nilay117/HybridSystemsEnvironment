package edu.ucsc.cross.hse.core.framework.data;

import java.util.HashMap;

import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.manipulation.ObjectCloner;
import bs.commons.unitvars.core.UnitData.Unit;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import bs.commons.unitvars.units.NoUnit;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.environment.ContentOperator;
import edu.ucsc.cross.hse.core.object.domain.HybridTime;
import edu.ucsc.cross.hse.core.object.domain.ValueDomain;
import edu.ucsc.cross.hse.core.procesing.io.FileExchanger;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;

/*
 * This class protects and manages a data object to ensure that the correct
 * value is always maintaned, and the pointer to it is always correct. It is
 * common to be accessing data from multiple different locations, thus it is
 * important for only one instance with the correct object to exist. This class
 * is also responsible for storing previous values of the object if specified by
 * the user.
 */
@SuppressWarnings(
{ "unchecked", "rawtypes" })
public class Data<T> extends Component// DynamicData<T>
{

	protected boolean save; // flag indicating if object should be stored

	private final boolean cloneToStore; // flag indicating if object needs to be
										// cloned to be stored correctly

	protected boolean flowing;

	protected DataTypeProperties dataType; // classification of the data
											// element, ie
											// Hybrid
											// State or Property

	protected Unit defaultUnit; // default unit (NoUnit class if object has
								// units)

	private T derivative; // current derivative of the data (if the data changes
							// continuously)

	protected ValueDomain<T> initialVal; // initial value of object

	protected HashMap<Double, T> savedValues; // mapping of saved values
	protected HashMap<HybridTime, T> savedHybridValues; // mapping of saved values

	protected T element; // currently stored data object

	protected ValueDomain<T> elementDomain;

	private UnitValue prejump; // pre-jump value stored immediately before jump
								// occurs

	protected T zeroDerivative;

	public T getValue()
	{

		try
		{

			if (getEnvironment().isJumpOccurring())
			{
				if (elementDomain != null)
				{
					return elementDomain.getValue();
				} else
				{
					if (defaultUnit.equals(NoUnit.NONE))
					{
						if (element.equals(prejump.get(NoUnit.NONE)))
						{
							return (T) prejump.get(NoUnit.NONE);
						} else
						{
							return element;
						}
					} else
					{
						UnitValue val = (UnitValue) element;
						if (val.get(val.getUnit()).equals(((UnitValue) element).get(val.getUnit())))
						{
							return element;
						} else
						{
							T oldValue = (T) ObjectCloner.xmlClone(element);
							UnitValue oldVal = (UnitValue) oldValue;
							oldVal.set(prejump.get(prejump.getUnit()), prejump.getUnit());
							return oldValue;
						}
					}
				}
			} else
			{
				// if (1 > 0)
				// {
				return element;
			}

		} catch (Exception e)
		{
			// System.out.println(this.getHierarchy().getParentComponent().toString());
			// e.printStackTrace();
			return element;
		}
		// } else
		// {
		// return element;
		// }
	}

	public void setValue(T element)
	{
		this.element = element;
		// if (this.element.getClass().equals(element.getClass()))
		{
			if (!ComponentOperator.getOperator(this).isInitialized())
			{
				initialVal.setValue(element);
				try
				{
					if (FieldFinder.containsSuper(element, UnitValue.class))
					{
						initialVal.setFixedValue((Double) ((UnitValue) element).get(defaultUnit), defaultUnit);
					} else if (FieldFinder.containsSuper(element, Double.class))
					{
						initialVal.setFixedValue((Double) element);
					} else
					{
						initialVal.setValue(element);
						initialVal.setRandomValues(null, null);
					}
				} catch (UnitException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.element = element;
		}
		// if (element != null)
		// {
		// if (FieldFinder.containsSuper(get(), UnitValue.class) &&
		// FieldFinder.containsSuper(get(), UnitValue.class))
		// {
		// try
		// {
		// UnitValue currentVal = (UnitValue) get();
		// UnitValue newVal = (UnitValue) element;
		// currentVal.set(newVal.get(newVal.getUnit()), newVal.getUnit());
		// } catch (UnitException ue)
		// {
		// ue.printStackTrace();
		// }
		//
		// } else
		// {
		// try
		// {
		// if (this.element.getClass().equals(element.getClass()))
		// {
		// this.element = element;
		// }
		// } catch (Exception ue)
		// {
		// ue.printStackTrace();
		// }
		// }
		// }
	}

	public void setValue(Double min, Double max)
	{
		setValue(min, max, defaultUnit);
	}

	public void setValue(Double min, Double max, Unit unit)
	{
		try
		{
			ValueDomain<T> domain = new ValueDomain<T>(element);
			domain.setRandomValues(min, max, unit);
			T randVal = domain.getValue();
			element = randVal;

			if (!ComponentOperator.getOperator(this).isInitialized())
			{
				elementDomain = domain;
				initialVal.setRandomValues(min, max, unit);
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public T getDerivative()
	{
		if (derivative == null)
		{
			//		return zeroDerivative;//this.setDerivative(cloneZeroDerivative(element));
		}
		if (!dataType.changesContinuously())// (CoreData.DYNAMIC_STATE))
		{
			SystemConsole
			.print("attempted to get derivative of " + derivative.toString() + " when it is not a dynamic variable");
		}
		return derivative;
	}

	public void setDerivative(T derivative)
	{
		if (dataType.changesContinuously())
		{
			this.derivative = derivative;
			flowing = true;
		} else
		{
			// IO.warn("attempted to set derivative of " + get().toString() + "
			// when it is not a dynamic variable");
		}
	}

	public boolean isElementNull()
	{
		return (getValue() == null);
	}

	@Override
	public DataWorker getActions()
	{
		return DataWorker.getConfigurer(this);
	}

	// // Internal Operation Functions
	// ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••
	// The functions below are designed to be used by the processor in the
	// background, there is no need to use any of these functions for normal
	// operation. Using these is not recommended, any changes could disrupt the
	// whole flow, but I'm guessing you are curious / adventerous like me so be
	// my gues't if you'd like to work on some modifications or new features.
	// only, it is not recommended to call these elsewhere unless trying to
	// alter the standard behavior
	// ••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••••

	/*
	 * Constructor with almost all inputs possible. The important ones are obj,
	 * type, and name, which are the default/initial value, dataType, and the
	 * element name, respectively. description is optional and can be added
	 * whenever more detail is required, the save_default flag can be specified
	 * if desired, otherwise it will assume the default defined by the unit type
	 */
	protected Data(T obj, DataTypeProperties type, String name, String description, Boolean save_default)
	{
		super(name, description);
		flowing = false;
		elementDomain = null;
		element = obj;
		dataType = type;
		derivative = (T) ObjectCloner.xmlClone(obj);
		//zeroDerivative = cloneZeroDerivative(element);
		//this.element = obj;
		save = save_default;
		defaultUnit = NoUnit.NONE;
		savedValues = new HashMap<Double, T>();
		savedHybridValues = new HashMap<HybridTime, T>();
		initialVal = new ValueDomain<T>(obj);
		cloneToStore = !DataOperator.isCopyRequiredOnSave(obj); // super.id().description().information.set(description);
		try
		{
			System.out.println(obj.getClass().getSuperclass());
			if (obj.getClass().getSuperclass().equals(UnitValue.class))
			{
				UnitValue val = ((UnitValue) obj);
				defaultUnit = (Unit) ((UnitValue) obj).getUnit();
				initialVal = new ValueDomain<T>(obj);
				initialVal.setFixedValue((Double) val.get(defaultUnit), defaultUnit);

				// initialVal = new InitialValue<T>((T) ((UnitValue)
				// obj).get(((UnitValue) obj).getUnit()));
			} else

			{
				// defaultUnit = null;
				initialVal = new ValueDomain<T>(obj);
			}
		} catch (Exception badClass)
		{
			badClass.printStackTrace();
			defaultUnit = NoUnit.NONE;
			// defaultUnit = null;
			initialVal = new ValueDomain<T>(obj);
		}
		try
		{
			prejump = new UnitValue(initialVal.getValue(), defaultUnit);
		} catch (UnitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated constructor stub
	}

	protected static <S> Data instantiate(S obj, DataTypeProperties type, String name, String description,
	Boolean save_default)
	{
		Data<S> newData = new Data<S>(obj, type, name, description, save_default);
		return newData;
	}

	@Override
	public void initialize()
	{
		boolean initialize = true;
		try
		{
			initialize = initialize && !ComponentOperator.getOperator(this).isInitialized();
		} catch (Exception nullInit)
		{
			initialize = true;
		}
		if (initialize)
		{
			setValue(initialVal.getValue());
		}

	}

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
	 * Store a copy of a value immediately before a jump occurs allowing
	 * pre=jump value access even if the value is changed by another component
	 */
	void storePreJumpValue()
	{
		try
		{
			if (defaultUnit != NoUnit.NONE)
			{
				UnitValue val = (UnitValue) element;
				Unit units = val.getUnit();

				prejump.set(val.get(units), units);
			} else
			{
				prejump.set(getValue(), NoUnit.NONE);
			}
		} catch (Exception e)
		{

		}
		// preJumpValue = getStoreValue();
	}

	void storeValue(Double time)
	{
		storeValue(time, false);
	}

	void storeValue(Double time, boolean override_save)
	{
		if (save || override_save)
		{
			T storeValue = getStoreValue();
			savedValues.put(time, storeValue);
			//			savedHybridValues
			//			.put(ContentOperator.getContentAdministrator(getEnvironment()).getEnvironmentHybridTime().getCurrent(),
			//			storeValue);
		}
	}

}
