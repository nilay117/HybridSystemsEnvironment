package edu.ucsc.cross.hse.core.object.configuration;

import bs.commons.objects.labeling.ChoiceName;

public class ComputationSettings
{

	public double odeMinStep; // ode step size if using a fixed step integrator,
								// or minimum ode step size of a
								// variable step integrator

	public double odeMaxStep; // maximum step size for variable step integrator

	public double odeScalAbsoluteTolerance; // absolute tolerance of the ode
											// solver

	public double odeScalRelativeTolerance; // relative tolerance of the ode
											// solver

	public double ehMaxCheckInterval; // event handler maximum interval to check
										// for an event

	public double ehConvergence; // convergence threshold of an event

	public int ehMaxIterationCount; // maximum number of iterations

	public boolean jumpPriority; // flag indicating if jumps have priority over
									// flows, ie if a state is in both domains
									// simultaneously,one response has priority

	public int maxRecursiveStackSize; // maximum recursive stack size that
										// occurrs when a jump (unrecoverably)
										// interrupts the ode

	public IntegratorType integrator; // Integrator to be used

	public double handlingThresholdReductionFactor = 1.0; // the amount that the
															// event handling
															// threshold will be
															// changed upon each
															// error occurring.
															// I have found that
															// changing this
															// number only makes
															// things worse.

	public double stepSizeReductionFactor = 1.3; // the fraction that the step
													// size will be reduced when
													// integerator errors occur
													// due to the step size
													// being too large.

	// available of integrator types for use
	public static enum IntegratorType
	{
		EULER(
			"Euler"),
		DORMAND_PRINCE_853(
			"Dormand Prince 853"),
		DORMAND_PRINCE_54(
			"Dormand Prince 54");

		@ChoiceName
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
	}

}
