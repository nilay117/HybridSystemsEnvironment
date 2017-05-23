package edu.ucsc.cross.hybrid.model.vehicle.simplebouncingball;

import bs.commons.unitvars.values.Distance;
import bs.commons.unitvars.values.Velocity;
import edu.ucsc.cross.hybrid.env.core.constructors.Data;

public interface Geolocated
{

	public Boolean nonGravityAcceleraton();

	public Data<Distance> getXPosition();

	public Data<Distance> getYPosition();

	public Data<Velocity> getXVelocity();

	public Data<Velocity> getYVelocity();
}
