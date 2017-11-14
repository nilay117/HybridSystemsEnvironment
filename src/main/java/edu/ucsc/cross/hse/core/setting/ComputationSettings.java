package edu.ucsc.cross.hse.core.setting;

public class ComputationSettings
{

	/*
	 * Amount that the event handling convergence value will be reduced when an event handling interval related error
	 * occurrs. Value should be between 0 and 1. I have found that reducing the event handling interval value increases
	 * the runtime significantly and would only recommend changing the default value if problems are occurring.
	 */
	public Double convergenceErrorCorrectionFactor;

	/*
	 * Convergence threshold of an event
	 */
	public Double eventHandlerConvergenceThreshold;

	/*
	 * Event handler maximum interval to check for an event
	 */
	public Double eventHandlerMaximumCheckInterval;

	/*
	 * Integrator type to be used
	 */
	public IntegratorType integratorType;

	/*
	 * Amount that the event handling interval will be reduced when an event handling interval related error occurrs.
	 * Value should be between 0 and 1. I have found that reducing the event handling interval value increases the
	 * runtime significantly and would only recommend changing the default value if problems are occurring.
	 */
	public Double intervalErrorCorrectionFactor;

	/*
	 * Multiplier to increase the maximum number of iterations that can be performed by the event handler when an
	 * iteration related error occurs. Value should be greater than 1;
	 */
	public Integer iterationCountErrorCorrectionFactor;

	/*
	 * Flag indicating if jumps have priority over flows, ie if a state is in both domains simultaneously,one response
	 * has priority. True indicates jump has priority, false indicates flow has priority, null indicates neither has
	 * priority and jump or flow will be selected at random
	 */
	public Boolean jumpPriority;

	/*
	 * Maximum number of iterations for the event handler
	 */
	public int maxEventHandlerIterations;

	/*
	 * Maximum step size for variable step integrator
	 */
	public Double odeMaximumStepSize;

	/*
	 * Ode step size if using a fixed step integrator, or minimum ode step size of a variable step integrator
	 */
	public Double odeMinimumStepSize;

	/*
	 * Relative tolerance of the ode solver
	 */
	public Double odeRelativeTolerance;

	/*
	 * Absolute tolerance of the ode solver
	 */
	public Double odeSolverAbsoluteTolerance;

	/*
	 * Factor to reduce the maximum step size (or fixed step size for fixed integrators) when a step size related error
	 * occurs. Value should be between 0 and 1
	 */
	public Double stepSizeErrorMaxCorrectionFactor;

	/*
	 * Factor to reduce the minimum step size when a step size related error occurs (for variable step integrators).
	 * Value should be between 0 and 1
	 */
	public Double stepSizeErrorMinCorrectionFactor;

	/*
	 * Default values constructor
	 */
	public ComputationSettings()
	{
		jumpPriority = true;
		odeMinimumStepSize = .000005;
		odeMaximumStepSize = .01;
		odeSolverAbsoluteTolerance = 1.0e-4;
		odeRelativeTolerance = 1.0e-4;
		eventHandlerMaximumCheckInterval = .00001;
		eventHandlerConvergenceThreshold = .001;
		maxEventHandlerIterations = 10;
		integratorType = IntegratorType.DORMAND_PRINCE_853;
		intervalErrorCorrectionFactor = 1.0;
		convergenceErrorCorrectionFactor = 1.0;
		stepSizeErrorMinCorrectionFactor = 1.0;
		stepSizeErrorMaxCorrectionFactor = 1.0;
		iterationCountErrorCorrectionFactor = 2;
	}

	/*
	 * Available of integrator types for use
	 */
	public static enum IntegratorType
	{
		DORMAND_PRINCE_54(
			"Dormand_Prince_54"),
		DORMAND_PRINCE_853(
			"Dormand_Prince_853"),
		EULER(
			"Euler");

		public final String choice;

		private IntegratorType(String mode)
		{
			choice = mode;
		}

		public static String enumNames()
		{
			String availableIntegrators = "\n         Integrator Options :";// \n";
			for (IntegratorType type : IntegratorType.values())
			{
				availableIntegrators += "\n";
				availableIntegrators += ("         - " + type.choice + " = " + type.name());
			}
			return availableIntegrators;
		}
	}

	/*
	 * Maximum recursive stack size that occurrs when a jump (unrecoverably) interrupts the ode
	 */
	public static int maximumRecursiveIntegratorCalls = 200;

}
