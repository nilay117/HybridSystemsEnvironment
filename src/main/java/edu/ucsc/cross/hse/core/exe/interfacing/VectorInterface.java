package edu.ucsc.cross.hse.core.exe.interfacing;

import edu.ucsc.cross.hse.core.exe.access.ObjectManipulator;
import edu.ucsc.cross.hse.core.exe.operator.EnvironmentManager;

public class VectorInterface
{

	private EnvironmentManager content;

	private ObjectManipulator[] simulatedObjectAccessVector;
	private double[] simulatedValueVector;

	public double[] getValueVector(boolean update)
	{
		return getValueVector(null, update);
	}

	public double[] getValueVector(double y[], boolean update)
	{
		if (update)
		{
			for (int i = 0; i < simulatedValueVector.length; i++)
			{
				simulatedValueVector[i] = (double) getStateAccessVector()[i].getObject();
				if (y != null)
				{

					y[i] = (double) getStateAccessVector()[i].getObject();

				}
				ObjectManipulator.validValue(getStateAccessVector()[i], simulatedValueVector[i]);
			}
		}
		return simulatedValueVector;
	}

	public double[] getChangeVector(double yDot[], boolean update)
	{
		if (update)
		{
			for (int i = 0; i < changeVector.length; i++)
			{
				changeVector[i] = (double) getStateAccessVector()[i].getChange();
				if (yDot != null)
				{
					yDot[i] = (double) getStateAccessVector()[i].getChange();
				}
			}
		}
		return changeVector;
	}

	private double[] changeVector;

	public VectorInterface(EnvironmentManager content)
	{
		this.content = content;
	}

	public void prepareVectors()
	{
		// if (simulatedObjectAccessVector == null)
		{
			fillStateAccessVector();
			initializeValueVectors();
		}
		loadValueVectors();
		// System.out.println(simulatedValueVector.length);
	}

	public void fillStateAccessVector()
	{

		simulatedObjectAccessVector = new ObjectManipulator[content.getObjControl().getNumericalStateMap().size()];
		for (Integer ind : content.getObjControl().getNumericalStateMap().keySet())
		{
			simulatedObjectAccessVector[ind] = content.getObjControl().getNumericalStateMap().get(ind);
		}

	}

	public void initializeValueVectors()
	{
		simulatedValueVector = new double[content.getObjControl().getNumericalStateMap().size()];
		changeVector = new double[content.getObjControl().getNumericalStateMap().size()];
	}

	public void loadValueVectors()
	{
		for (int i = 0; i < simulatedValueVector.length; i++)
		{
			try
			{
				changeVector[i] = 0.0;
				simulatedValueVector[i] = (double) simulatedObjectAccessVector[i].getObject();
			} catch (Exception e)
			{
				simulatedValueVector[i] = 0.0;
				e.printStackTrace();
			}
		}
	}

	public void readStateValues(double y[])
	{
		for (int i = 0; i < y.length; i++)
		{
			content.getVector().getStateAccessVector()[i].updateObject(y[i]);
		}
		// IO.dev(XMLParser.serializeObject(odeVectorMap, MessageCategory.DEV));
	}

	public ObjectManipulator[] getStateAccessVector()
	{
		return simulatedObjectAccessVector;
	}
}
