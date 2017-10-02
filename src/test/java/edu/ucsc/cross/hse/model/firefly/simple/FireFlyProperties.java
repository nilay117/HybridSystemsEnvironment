package edu.ucsc.cross.hse.model.firefly.simple;

public class FireFlyProperties
{

	public double maximumGlowIntensity;
	public double eConstant;

	public FireFlyProperties(double max_glow,double e)
	{
		this.maximumGlowIntensity=max_glow;
		this.eConstant=e;
	}
}
