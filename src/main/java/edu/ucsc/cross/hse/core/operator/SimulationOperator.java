package edu.ucsc.cross.hse.core.operator;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

/*
 * Provides the computations and organization necessary to run a simulation. It has been modified to support hybrid
 * systems without requiring pre-specified thresholds to detect discrete events. This is an improvement from having to
 * explicetly define each condition that triggers a jump.
 */
public class SimulationOperator implements FirstOrderDifferentialEquations
{

	private ExecutionOperator content;

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
		content.getDataManager().performDataActions(t, y, content.getJumpEvaluator().getCheckJumpStatus());
		content.getSystemControl().applyDynamics(false);
		content.getExecutionContent().updateChangeVector(yDot);
	}

	/*
	 * Get the dimension of the ode vector
	 * 
	 * @return length of the ode vector
	 */
	@Override
	public int getDimension()
	{
		return content.getExecutionContent().getSimulatedObjectAccessVector().length;
	}

	public SimulationOperator(ExecutionOperator content)
	{
		this.content = content;
	}
}