package edu.ucsc.cross.hybrid.model.vehicle.simplebouncingball;

import bs.commons.unitvars.values.Velocity;
import edu.ucsc.cross.hybrid.env.core.constructors.Data;
import edu.ucsc.cross.hybrid.env.core.constructors.DataSet;
import edu.ucsc.cross.hybrid.env.core.factories.DataFactory;

public class EnvironmentProperties extends DataSet
{

	public final Data<Velocity> gravity;// = Data.getProperty(9.81);
	public final Data<Double> restitution;

	public EnvironmentProperties()
	{
		super("Prope");
		gravity = DataFactory.hybridState.create(Velocity.newKilometersPerSecondValue(9.81), "Gravit");
		restitution = DataFactory.property.create(.99, "Restitution Coefficient", true);
		initialize();
	}

	@Override
	public void initialize()
	{
		//if (super.isInitialized() != null)
		{
			//if (!super.isInitialized())
			{

				restitution.getInitialVal().setRandomValues(0.9, 0.99);
				restitution.initializeValue();
				System.out.println("Restitution " + restitution.get());
				//gravity.getInitialVal().getRange().setValues(9.81, 9.81);
				//gravity.get().kilometersPerSecond(gravity.getInitialVal().getNumberValue());
			}
		}
		// TODO Auto-generated method stub

	}

}
