package edu.ucsc.cross.hybrid.env.core.test.components;

import bs.commons.unitvars.values.Velocity;
import edu.ucsc.cross.hse.core.framework.component.Component;
import edu.ucsc.cross.hse.core.framework.data.Data;
import edu.ucsc.cross.hse.core.framework.data.DataFactory;
import edu.ucsc.cross.hse.core.framework.models.HybridSystem;

public class TestState extends Component
{

	public Data<Double> continuous; // state that will only change continuously
	public Data<Double> discrete; // state that will only change discretely
	public Data<Velocity> hybrid; // state that will change continuously and
									// discretely

	public TestState()
	{
		super("Test State");
		continuous = DataFactory.state.create(1.0, "Continuous State");
		discrete = DataFactory.state.create(1.0, "Discrete State");
		hybrid = DataFactory.state.create(Velocity.newKilometersPerSecondValue(2.0), "Hybrid State");
	}

	@Override
	public void initialize()
	{
		hybrid.setValue(Velocity.newKilometersPerSecondValue(Math.random() * 3 + Math.random() + 1));
		// TODO Auto-generated method stub
		discrete.setValue(20.0);
		continuous.setValue(4.0);
	}
}
