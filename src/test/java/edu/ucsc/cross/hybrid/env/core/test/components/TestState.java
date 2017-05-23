package edu.ucsc.cross.hybrid.env.core.test.components;

import bs.commons.unitvars.values.Velocity;
import edu.ucsc.cross.hybrid.env.core.data.DataFactory;
import edu.ucsc.cross.hybrid.env.core.elements.Data;
import edu.ucsc.cross.hybrid.env.core.elements.DataSet;

public class TestState extends DataSet
{

	public Data<Double> continuous; // state that will only change continuously
	public Data<Double> discrete; // state that will only change discretely
	public Data<Double> hybrid; // state that will change continuously and discretely

	public TestState()
	{
		super("Test State");
		continuous = DataFactory.dynamicState.create(1.0, "Continuous State");
		discrete = DataFactory.discreteState.create(1.0, "Discrete State");
		hybrid = DataFactory.dynamicState.create(1.0, "Hybrid State");
	}

	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub

	}
}
