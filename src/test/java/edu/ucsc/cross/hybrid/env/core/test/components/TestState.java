package edu.ucsc.cross.hybrid.env.core.test.components;

import edu.ucsc.cross.hybrid.env.core.constructors.Data;
import edu.ucsc.cross.hybrid.env.core.constructors.DataSet;
import edu.ucsc.cross.hybrid.env.core.factories.DataFactory;

public class TestState extends DataSet
{

	public Data<Double> continuous; // state that will only change continuously
	public Data<Double> discrete; // state that will only change discretely
	public Data<Double> hybrid; // state that will change continuously and discretely

	public TestState()
	{
		super("Test State");
		continuous = DataFactory.hybridState.create(1.0, "Continuous State");
		discrete = DataFactory.discreteState.create(1.0, "Discrete State");
		hybrid = DataFactory.hybridState.create(1.0, "Hybrid State");
	}

	@Override
	public void initialize()
	{
		hybrid.set(Math.random() * 3 + Math.random() + 1);
		// TODO Auto-generated method stub

	}
}
