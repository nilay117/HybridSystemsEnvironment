package edu.ucsc.cross.hse.core.processing.computation;

import java.util.HashMap;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

import bs.commons.objects.access.FieldFinder;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.component.ComponentOperator;
import edu.ucsc.cross.hse.core.framework.data.DataOperator;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.State;
import edu.ucsc.cross.hse.core.procesing.io.SystemConsole;
import edu.ucsc.cross.hse.core.processing.execution.CentralProcessor;
import edu.ucsc.cross.hse.core.processing.execution.ProcessingElement;

/*
 * his class provides the computations and organization necessary to run a
 * simulation. It has been modified to support hybrid systems without requiring
 * pre-specified thresholds to detect discrete events. This is an improvement
 * from having to explicetly define each condition that triggers a jump.
 */
@SuppressWarnings(
{ "rawtypes", "unchecked" })
public class SimulationEngine extends ProcessingElement implements FirstOrderDifferentialEquations
{

	/*
	 * This is a mapping of all state elements used by the ode with keys that
	 * correspond to the ode state vector index. The ode state vector is made up
	 * of all data elements that can change dynamically. This structure allows
	 * for state values and be adjusted from their respective components and
	 * always be ready for use in the ode
	 */
	private HashMap<Integer, State> odeVectorMap;

	/*
	 * Constructor to link the central processor
	 * 
	 * @param processor - main environment processor
	 */
	public SimulationEngine(CentralProcessor processor)
	{
		super(processor);
		odeVectorMap = new HashMap<Integer, State>();
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

			State element = odeVectorMap.get(odeIndex);
			element.setValue(y[odeIndex]);

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
		getEnvironmentOperator().getEnvironmentHybridTime().setTime(t);
		updateValues(y);
		getConsole().printUpdates();

		zeroAllDerivatives();

		this.getComponents().performAllTasks(false);

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
			State element = odeVectorMap.get(odeIndex);
			Double derivative = element.getDerivative();
			if (derivative == null)
			{
				derivative = 0.0;
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
			State element = odeVectorMap.get(odeIndex);

			yValues[odeIndex] = element.getValue();

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
		State element = odeVectorMap.get(index);
		return element.getValue();

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

		for (State component : getEnv().getContents().getObjects(State.class, true))// .loadComponents();//.getSpecificComponent(Data.class,
			try
			{
				if (getDataOperator(component).isSimulated())// .isSimulated())
				{
					if (getDataOperator(component).isDataStored())
					{
						odeVectorMap.put(odeIndex++, component);
					}
				}

			} catch (Exception e)
			{
				e.printStackTrace();
			}

		SystemConsole.print("ODE Vector Length: " + odeIndex);

	}

	public void zeroAllDerivatives()
	{
		for (State data : odeVectorMap.values())
		{
			data.setDerivative(null);
		}
	}
}