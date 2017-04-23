package edu.ucsc.cross.hybrid.env.core.processor;

import java.util.HashMap;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

import bs.commons.unitvars.core.UnitValue;
import bs.commons.unitvars.exceptions.UnitException;
import edu.ucsc.cross.hybrid.env.core.components.Data;
import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;
import edu.ucsc.cross.hybrid.env.core.structure.Component;
import edu.ucsc.cross.hybrid.env.core.structure.ComponentClassification;

@SuppressWarnings(
{ "rawtypes", "unchecked" })
public class SimulationEngine extends Processor implements FirstOrderDifferentialEquations
{

	// mapping of all state elements used by the ode to the corresponding ode
	// state vector index. The ode state vector is made up of all the elements
	// that can change continuously

	private HashMap<Integer, Data> odeVectorMap;

	SimulationEngine(Environment processor)
	{
		super(processor);
		odeVectorMap = new HashMap<Integer, Data>();
	}

	void initialize()
	{
		this.initializeIndicies();
	}

	private void initializeIndicies()
	{
		odeVectorMap.clear();
		Integer odeIndex = 0;
		for (HybridSystem componen : getEnvironment().getAllSystems())
		{
			for (Component component : componen.getComponents(ComponentClassification.DYNAMIC_STATE, true))//.loadComponents();//.getSpecificComponent(Data.class, null))
			{
				Data dat = (Data) component;
				if (Data.isSimulated(dat))
				{
					odeVectorMap.put(odeIndex++, dat);
				}
			}
		}
		//IO.debug("ODE Vector Map:\n" + XMLParser.serializeObject(odeVectorMap) + "\n");
	}

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
		//IO.dev(XMLParser.serializeObject(odeVectorMap, MessageCategory.DEV));
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
		//	sim.environment().storePreJumpStates();
		getComponents().performTasks(false);
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
			y[odeIndex] = derivative;//.getDerivative();
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
			//System.out.println(XMLParser.serializeObject(odeVectorMap));
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

	//	public static void main(String[] args)
	//	{
	//		System.out.println(
	//		"This is the hybrid simulation engine v1.0, please see the applications repo at https://github.com/brendan1991/HybridSimEngineApplications to learn how to use this tool");
	//	}

}