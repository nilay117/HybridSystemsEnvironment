package edu.ucsc.cross.hse.core.processing.computation;

import java.util.HashMap;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

import bs.commons.objects.access.FieldFinder;
import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import edu.ucsc.cross.hse.core.component.categorization.CoreDataGroup;
import edu.ucsc.cross.hse.core.component.constructors.Component;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.processing.management.Environment;
import edu.ucsc.cross.hse.core.processing.management.Processor;

@SuppressWarnings(
{ "rawtypes", "unchecked" })
public class SimulationEngine extends Processor implements FirstOrderDifferentialEquations
{

	// mapping of all state elements used by the ode to the corresponding ode
	// state vector index. The ode state vector is made up of all the elements
	// that can change continuously
	private HashMap<Integer, Data> odeVectorMap;

	public SimulationEngine(Environment processor)
	{
		super(processor);
		odeVectorMap = new HashMap<Integer, Data>();
	}

	public void initialize()
	{
		initializeIndicies();
	}

	private void initializeIndicies()
	{
		odeVectorMap.clear();
		Integer odeIndex = 0;

		for (Component component : getEnvironment().getComponents(true))// .loadComponents();//.getSpecificComponent(Data.class,
																		// null))
		{
			try
			{
				Data dat = (Data) component;
				if (dat.getProperties().getClassification().equals(Data.class))
				{
					if (Data.isSimulated(dat))
					{
						if (CoreDataGroup.DYNAMIC_STATE_ELEMENTS.contains(dat))
						{
							if (FieldFinder.containsSuper(dat.get(), UnitValue.class)
							|| FieldFinder.containsSuper(dat.get(), Number.class))
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

		// IO.debug("ODE Vector Map:\n" +
		// XMLParser.serializeObject(odeVectorMap) + "\n");
	}
	// System.exit(0);

	public void updateValues(double y[])
	{
		for (Integer odeIndex : odeVectorMap.keySet())
		{
			Data element = odeVectorMap.get(odeIndex);
			if (element.get().getClass().getSuperclass().equals(UnitValue.class))
			{
				UnitValue uv = (UnitValue) element.get();
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
				element.set(y[odeIndex]);
			}
		}
		// IO.dev(XMLParser.serializeObject(odeVectorMap, MessageCategory.DEV));
	}

	@Override
	public int getDimension()
	{
		return odeVectorMap.size();
	}

	@Override
	public void computeDerivatives(double t, double[] y, double[] yDot)
	throws MaxCountExceededException, DimensionMismatchException
	{
		updateValues(y);
		getConsole().printUpdates();

		// sim.environment().storePreJumpStates();
		getEnvironment().performTasks(false);
		updateYDotVector(yDot);
	}

	private void updateYDotVector(double y[])
	{
		for (Integer odeIndex : odeVectorMap.keySet())
		{
			Data element = odeVectorMap.get(odeIndex);
			Double derivative = 0.0;
			if (element.get().getClass().getSuperclass().equals(UnitValue.class))
			{
				try
				{
					derivative = (Double) ((UnitValue) element.getDt()).get(((UnitValue) element.get()).getUnit());
				} catch (UnitException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else
			{
				derivative = (Double) element.getDt();
			}
			y[odeIndex] = derivative;// .getDerivative();
		}
	}

	public void setODEValueVector(double[] yValues)
	{
		for (Integer odeIndex : odeVectorMap.keySet())
		{
			Data element = odeVectorMap.get(odeIndex);
			if (element.get().getClass().getSuperclass().equals(UnitValue.class))
			{
				UnitValue uv = (UnitValue) element.get();
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
				yValues[odeIndex] = (Double) element.get();
			}
			// System.out.println(XMLParser.serializeObject(odeVectorMap));
		}
	}

	private Double getODEStateElementValue(Integer index)
	{
		Data element = odeVectorMap.get(index);
		if (element.get().getClass().getSuperclass().equals(UnitValue.class))
		{
			UnitValue uv = (UnitValue) element.get();
			try
			{
				return (Double) uv.get(uv.getUnit());
			} catch (UnitException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
		{
			return (Double) element.get();
		}
		return null;

	}

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

	// public static void main(String[] args)
	// {
	// System.out.println(
	// "This is the hybrid simulation engine v1.0, please see the applications
	// repo at https://github.com/brendan1991/HybridSimEngineApplications to
	// learn how to use this tool");
	// }

}