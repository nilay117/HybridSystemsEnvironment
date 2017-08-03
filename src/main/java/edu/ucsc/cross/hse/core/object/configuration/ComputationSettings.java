package edu.ucsc.cross.hse.core.object.configuration;

/*
 * Collection of computational configurations that defines how computation
 * elements will operate
 */
public class ComputationSettings
{

	/*
	 * Ode step size if using a fixed step integrator, or minimum ode step size
	 * of a variable step integrator
	 */
	public double odeMinStep;

	/*
	 * Maximum step size for variable step integrator
	 */
	public double odeMaxStep;

	/*
	 * Absolute tolerance of the ode solver
	 */
	public double odeScalAbsoluteTolerance;

	/*
	 * Relative tolerance of the ode solver
	 */
	public double odeScalRelativeTolerance;

	/*
	 * Event handler maximum interval to check for an event
	 */
	public double ehMaxCheckInterval;

	/*
	 * Convergence threshold of an event
	 */
	public double ehConvergence;

	/*
	 * Maximum number of iterations
	 */
	public int ehMaxIterationCount;

	/*
	 * Flag indicating if jumps have priority over flows, ie if a state is in
	 * both domains simultaneously,one response has priority
	 */
	public boolean jumpPriority;

	/*
	 * Maximum recursive stack size that occurrs when a jump (unrecoverably)
	 * interrupts the ode
	 */
	public int maxRecursiveStackSize;

	/*
	 * Integrator type to be used
	 */
	public IntegratorType integrator;

	/*
	 * Amount that the event handling interval will be reduced when an event
	 * handling interval related error occurrs. Value should be between 0 and 1.
	 * I have found that reducing the event handling interval value increases
	 * the runtime significantly and would only recommend changing the default
	 * value if problems are occurring.
	 */
	public double eventHandlingIntervalReduction;

	/*
	 * Amount that the event handling convergence value will be reduced when an
	 * event handling interval related error occurrs. Value should be between 0
	 * and 1. I have found that reducing the event handling interval value
	 * increases the runtime significantly and would only recommend changing the
	 * default value if problems are occurring.
	 */
	public double eventHandlingConvergenceReduction;

	/*
	 * Multiplier to reduce the minimum step size when a step size related error
	 * occurs (for variable step integrators). Value should be between 0 and 1
	 */
	public double minStepSizeReduction;

	/*
	 * Multiplier to reduce the maximum step size (or fixed step size for fixed
	 * integrators) when a step size related error occurs. Value should be
	 * between 0 and 1
	 */
	public double stepSizeReduction;

	/*
	 * Multiplier to increase the maximum number of iterations that can be
	 * performed by the event handler when an iteration related error occurs.
	 * Value should be greater than 1;
	 */
	public Integer eventHandlingMaxIterationMultiplier;

	/*
	 * Default values constructor
	 */
	public ComputationSettings()
	{
		odeMinStep = .0005;
		odeMaxStep = .1;
		odeScalAbsoluteTolerance = 1.0e-4;
		odeScalRelativeTolerance = 1.0e-4;
		ehMaxCheckInterval = .001;
		ehConvergence = .001;
		ehMaxIterationCount = 10;
		jumpPriority = true;
		maxRecursiveStackSize = 1000;
		integrator = IntegratorType.DORMAND_PRINCE_853;
		eventHandlingIntervalReduction = 1.0;
		eventHandlingConvergenceReduction = 1.0;
		minStepSizeReduction = .25;
		stepSizeReduction = 1.0;
		eventHandlingMaxIterationMultiplier = 2;
	}

	/*
	 * Available of integrator types for use
	 */
	public static enum IntegratorType
	{
		EULER(
			"Euler"),
		DORMAND_PRINCE_853(
			"Dormand_Prince_853"),
		DORMAND_PRINCE_54(
			"Dormand_Prince_54");

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
}
