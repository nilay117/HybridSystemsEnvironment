package edu.ucsc.cross.hybrid.env.core.test.components;

import bs.commons.unitvars.values.Velocity;
import edu.ucsc.cross.hse.core.component.constructors.DataSet;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataFactory;

public class TestState extends DataSet
{

	public Data<Double> continuous; // state that will only change continuously
	public Data<Double> discrete; // state that will only change discretely
	public Data<Velocity> hybrid; // state that will change continuously and
									// discretely

	public TestState()
	{
		super("Test State");
		continuous = DataFactory.hybridState.create(1.0, "Continuous State");
		discrete = DataFactory.discreteState.create(1.0, "Discrete State");
		hybrid = DataFactory.hybridState.create(Velocity.newKilometersPerSecondValue(2.0), "Hybrid State");
	}

	@Override
	public void initialize()
	{
		hybrid.setObject(Velocity.newKilometersPerSecondValue(Math.random() * 3 + Math.random() + 1));
		// TODO Auto-generated method stub
		discrete.setObject(20.0);
		continuous.setObject(4.0);
	}
}
