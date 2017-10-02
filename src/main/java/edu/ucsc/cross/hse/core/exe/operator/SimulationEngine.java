package edu.ucsc.cross.hse.core.exe.operator;

import bs.commons.objects.manipulation.XMLParser;
import com.jcabi.aspects.Loggable;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

/*
 * Provides the computations and organization necessary to run a simulation. It has been modified to support hybrid
 * systems without requiring pre-specified thresholds to detect discrete events. This is an improvement from having to
 * explicetly define each condition that triggers a jump.
 */
public class SimulationEngine implements FirstOrderDifferentialEquations
{

	private EnvironmentManager content;

	public SimulationEngine(EnvironmentManager content)
	{
		this.content = content;
	}

	// /*
	// * Stores the values from the most current ode vector into the according variables
	// *
	// * @param y - vector containing all values being evaluated by the ode
	// */
	// @Loggable(Loggable.DEBUG * Log.ALL)
	// public void readStateValues(double y[])
	// {
	// for (int i = 0; i < y.length; i++)
	// {
	// content.getVector().getStateAccessVector()[i].updateObject(y[i]);
	// }
	// // IO.dev(XMLParser.serializeObject(odeVectorMap, MessageCategory.DEV));
	// }

	/*
	 * Computes the new derivatives of each hybrid state element using the newly stored values from vector y
	 * 
	 * @param t - current time elapsed
	 * 
	 * @param y - vector containing all values being evaluated by the ode
	 * 
	 * @param yDot - vector containing all derivative values being evaluated by the ode
	 */
	public void computeDerivatives(double t, double[] y, double[] yDot)
	throws MaxCountExceededException, DimensionMismatchException
	{

		content.getVector().readStateValues(y);
		content.getSystemControl().applyDynamics(false);
		content.getVector().getChangeVector(yDot, true);
		// System.out.println(XMLParser.serializeObject(y) + "\n" + XMLParser.serializeObject(yDot));
	}

	/*
	 * // * Updates the ode derivative vector with the current values stored in the components // * // * @param y -
	 * vector containing all derivative values being evaluated by the ode //
	 */
	// @Loggable(Log.ALL)
	// private void writeDerivativeValues(double y[])
	// {
	// content.getVector().getChangeVector(y, true);
	// }

	// /*
	// * Updates the value vector of the ode from the most current stored value. This is necessary after a jump ocurs
	// *
	// * @param y - vector containing all values being evaluated by the ode
	// */
	// @Loggable(Log.ALL)
	// public void writeStateValues(double[] y)
	// {
	// content.getVector().getValueVector(y, true);
	//
	// }

	/*
	 * Get the dimension of the ode vector
	 * 
	 * @return length of the ode vector
	 */
	@Override
	public int getDimension()
	{
		return content.getVector().getStateAccessVector().length;
	}
}