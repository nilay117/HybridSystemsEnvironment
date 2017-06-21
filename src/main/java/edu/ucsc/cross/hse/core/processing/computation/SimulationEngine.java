package edu.ucsc.cross.hse.core.processing.computation;

import java.util.HashMap;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

import bs.commons.objects.access.FieldFinder;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentAdministrator;
import edu.ucsc.cross.hse.core.framework.data.CoreDataGroup;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.processing.execution.Processor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessorAccess;

/*
 * his class provides the computations and organization necessary to run a
 * simulation. It has been modified to support hybrid systems without requiring
 * pre-specified thresholds to detect discrete events. This is an improvement
 * from having to explicetly define each condition that triggers a jump.
 */
@SuppressWarnings(
{ "rawtypes", "unchecked" })
public class SimulationEngine extends ProcessorAccess implements FirstOrderDifferentialEquations
{

	// mapping of all state elements used by the ode to the corresponding ode
	// state vector index. The ode state vector is made up of all the elements
	// that can change continuously
	private HashMap<Integer, Data> odeVectorMap;

	/*
	 * Constructor to link the central processor
	 * 
	 * @param processor - main environment processor
	 */
	public SimulationEngine(Processor processor)
	{
		super(processor);
		odeVectorMap = new HashMap<Integer, Data>();
	}

	/*
	 * Stores the values from the most current ode vector into the according
	 * variables
	 * 
	 * @param y - vector containing all values being evaluated by the ode
	 */
	public void updateValues(double y[])
	{
		for (Integer odeIndex : odeVectorMap.keySet())
		{
			Data element = odeVectorMap.get(odeIndex);
			if (element.getValue().getClass().getSuperclass().equals(UnitValue.class))
			{
				UnitValue uv = (UnitValue) element.getValue();
				try
				{
					uv.set(y[odeIndex], uv.getUnit());

				} catch (UnitException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			{
				element.setValue(y[odeIndex]);
			}
		}
		// IO.dev(XMLParser.serializeObject(odeVectorMap, MessageCategory.DEV));
	}

	/*
	 * Computes the new derivatives of each hybrid state element using the newly
	 * stored values from vector y
	 * 
	 * @param t - current time elapsed
	 * 
	 * @param y - vector containing all values being evaluated by the ode
	 * 
	 * @param yDot - vector containing all derivative values being evaluated by
	 * the ode
	 */
	@Override
	public void computeDerivatives(double t, double[] y, double[] yDot)
	throws MaxCountExceededException, DimensionMismatchException
	{
		updateValues(y);
		getConsole().printUpdates();

		// sim.environment().storePreJumpStates();
		ComponentAdministrator.getConfigurer(getEnvironment()).performTasks(false);
		updateYDotVector(yDot);
	}

	/*
	 * Updates the ode derivative vector with the current values stored in the
	 * components
	 * 
	 * @param y - vector containing all derivative values being evaluated by the
	 * ode
	 */
	private void updateYDotVector(double y[])
	{
		for (Integer odeIndex : odeVectorMap.keySet())
		{
			Data element = odeVectorMap.get(odeIndex);
			Double derivative = 0.0;
			if (element.getValue().getClass().getSuperclass().equals(UnitValue.class))
			{
				try
				{
					derivative = (Double) ((UnitValue) element.getDerivative())
					.get(((UnitValue) element.getValue()).getUnit());
				} catch (UnitException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			{
				derivative = (Double) element.getDerivative();
			}
			y[odeIndex] = derivative;// .getDerivative();
		}
	}

	/*
	 * Updates the value vector of the ode from the most current stored value.
	 * This is necessary after a jump ocurs
	 * 
	 * @param y - vector containing all values being evaluated by the ode
	 */
	public void setODEValueVector(double[] yValues)
	{
		for (Integer odeIndex : odeVectorMap.keySet())
		{
			Data element = odeVectorMap.get(odeIndex);
			if (element.getValue().getClass().getSuperclass().equals(UnitValue.class))
			{
				UnitValue uv = (UnitValue) element.getValue();
				try
				{
					yValues[odeIndex] = (Double) uv.get(uv.getUnit());
				} catch (UnitException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			{
				yValues[odeIndex] = (Double) element.getValue();
			}
			// System.out.println(XMLParser.serializeObject(odeVectorMap));
		}
	}

	/*
	 * Produces an ode vector value corresponding to a given index
	 * 
	 * @param index - array index of the value to be returned
	 * 
	 * @return double value that was stored in the ode vector
	 */
	private Double getODEStateElementValue(Integer index)
	{
		Data element = odeVectorMap.get(index);
		if (element.getValue().getClass().getSuperclass().equals(UnitValue.class))
		{
			UnitValue uv = (UnitValue) element.getValue();
			try
			{
				return (Double.class.cast(uv.get(uv.getUnit())));
			} catch (UnitException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			return (Double) element.getValue();
		}
		return null;

	}

	/*
	 * Aquires the ode value vector
	 * 
	 * @return double array with the ode values
	 */
	public double[] getODEValueVector()
	{
		double[] yValues = new double[getDimension()];
		for (Integer i = 0; i < yValues.length; i++)
		{
			double value = (double) getODEStateElementValue(i);
			yValues[i] = value;
		}
		return yValues;
	}

	/*
	 * Get the dimension of the ode vector
	 * 
	 * @return length of the ode vector
	 */
	@Override
	public int getDimension()
	{
		return odeVectorMap.size();
	}

	/*
	 * Initialization
	 */
	public void initialize()
	{
		initializeIndicies();
	}

	/*
	 * Initialization of the indicies of all data elements include in the ode
	 * vector. This is where each data elements is assigned a number used to
	 * retreive the corresponding value
	 */
	private void initializeIndicies()
	{
		odeVectorMap.clear();
		Integer odeIndex = 0;

		for (Component component : getEnvironment().getHierarchy().getComponents(true))// .loadComponents();//.getSpecificComponent(Data.class,
		// null))
		{
			try
			{
				Data dat = (Data) component;
				if (dat.getClass().equals(Data.class))
				{
					if (getDataOperator(dat).isSimulated())// .isSimulated())
					{
						if (CoreDataGroup.HYBRID_STATE_ELEMENTS.contains(dat))
						{
							if (FieldFinder.containsSuper(dat.getValue(), UnitValue.class)
							|| FieldFinder.containsSuper(dat.getValue(), Number.class))
							{
								odeVectorMap.put(odeIndex++, dat);
							}
						}
					}
				}
			} catch (Exception e)
			{
				// e.printStackTrace();
			}
		}
		System.out.println("ODE Vector Length: " + odeIndex);
	}
}