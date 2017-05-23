package edu.ucsc.cross.hybrid.model.vehicle.simplebouncingball;

import bs.commons.unitvars.values.Distance;
import bs.commons.unitvars.values.Velocity;
import edu.ucsc.cross.hse.core.component.constructors.DataSet;
import edu.ucsc.cross.hse.core.component.data.Data;
import edu.ucsc.cross.hse.core.component.data.DataFactory;

public class BouncingBallState extends DataSet implements Geolocated
{

	public Data<Velocity> xVel;
	public Data<Velocity> yVel;
	public Data<Distance> xPos;
	public Data<Distance> yPos;
	public EnvironmentProperties envProps;

	public BouncingBallState()
	{
		super("Bouncing Ball State");
		envProps = new EnvironmentProperties();
		xVel = DataFactory.hybridState.create(Velocity.newMetersPerSecondValue(), "X Velocity");
		yVel = DataFactory.hybridState.create(Velocity.newMetersPerSecondValue(), "Y Velocity");
		xPos = DataFactory.hybridState.create(Distance.newMetersValue(), "X Position");
		yPos = DataFactory.hybridState.create(Distance.newMetersValue(9.0), "Y Position");

		initialize();
		//yPos = Data.getDynamicStateData(Distance.newMeterValue(9.0), "Y Position");
	}

	@Override
	public void initialize()
	{
		//envProps.initialize();
		//		yVel.getInitialVal().getRange().setValues(-2.0, 2.0);
		//		xVel.getInitialVal().getRange().setValues(0.5, 2.0);
		//		yPos.getInitialVal().getRange().setValues(4.0, 15.0);
		//		yPos.initializeValue();
		//		yVel.initializeValue();
		//		xVel.initializeValue();
		yPos.getDt().meters(yVel.get().metersPerSecond());
		yVel.getDt().metersPerSecond(-9.81);
		//yPos.getDt().meters(yVel.get().metersPerSeconds());
	}

	@Override
	public Boolean nonGravityAcceleraton()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Data<Distance> getXPosition()
	{
		// TODO Auto-generated method stub
		return xPos;
	}

	@Override
	public Data<Distance> getYPosition()
	{
		// TODO Auto-generated method stub
		return yPos;
	}

	@Override
	public Data<Velocity> getXVelocity()
	{
		// TODO Auto-generated method stub
		return xVel;
	}

	@Override
	public Data<Velocity> getYVelocity()
	{
		// TODO Auto-generated method stub
		return yVel;
	}

}