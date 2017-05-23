package edu.ucsc.cross.hse.core.object.settings;

import bs.commons.objects.labeling.ChoiceName;

public class ComputationSettings
{
	//////Simulation ODE Solver Settings

	public double odeMinStep; // ode step size if using a fixed step integrator, or minimum ode step size of a
	// variable step integrator

	public double odeMaxStep; // maximum step size for variable step integrator

	public double odeScalAbsoluteTolerance; // absolute tolerance of the ode solver

	public double odeScalRelativeTolerance; // relative tolerance of the ode solver

	public double ehMaxCheckInterval; // event handler maximum interval to check for an event

	public double ehConvergence; // convergence threshold of an event

	public int ehMaxIterationCount; // maximum number of iterations

	public boolean jumpPriority;

	public int maxRecursiveStackSize; // maximum recursive stack size that occurrs when  a jump (unrecoverably) interrupts the ode

	public IntegratorType integrator;

	public final String integratorOptions = IntegratorType.enumNames();

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

	public ComputationSettings()
	{
		odeMinStep = .01; // ode step size if using a fixed step integrator, or minimum ode step size of a
		// variable step integrator

		odeMaxStep = .1; // maximum step size for variable step integrator

		odeScalAbsoluteTolerance = 1.0e-6; // absolute tolerance of the ode solver

		odeScalRelativeTolerance = 1.0e-6; // relative tolerance of the ode solver

		ehMaxCheckInterval = .001; // event handler maximum interval to check for an event

		ehConvergence = .001; // convergence threshold of an event

		ehMaxIterationCount = 10; // maximum number of iterations

		jumpPriority = true;

		maxRecursiveStackSize = 1000; // maximum recursive stack size that occurrs when  a jump (unrecoverably) interrupts the ode

		integrator = IntegratorType.DORMAND_PRINCE_853;

	}

}
