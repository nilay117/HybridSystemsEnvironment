package edu.ucsc.cross.hse.core.processing.settings;

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

	public final String integratorOptions = IntegratorType.enumNames();
	// list of enumerator options which are printed in the output file to help
	// with seection

	public double handlingThresholdReductionFactor = 1.0;// 2.0;
	public double stepSizeReductionFactor = 1.1;// .3;

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
		odeMinStep = .005; // ode step size if using a fixed step integrator, or
							// minimum ode step size of a
		// variable step integrator

		odeMaxStep = .01; // maximum step size for variable step integrator

		odeScalAbsoluteTolerance = 1.0e-6; // absolute tolerance of the ode
											// solver

		odeScalRelativeTolerance = 1.0e-6; // relative tolerance of the ode
											// solver

		ehMaxCheckInterval = .0001; // event handler maximum interval to check
									// for an event

		ehConvergence = .0001; // convergence threshold of an event

		ehMaxIterationCount = 10; // maximum number of iterations

		jumpPriority = true;

		maxRecursiveStackSize = 1000; // maximum recursive stack size that
										// occurrs when a jump (unrecoverably)
										// interrupts the ode

		integrator = IntegratorType.DORMAND_PRINCE_853;// .EULER;//
														// .DORMAND_PRINCE_853;

	}

}
