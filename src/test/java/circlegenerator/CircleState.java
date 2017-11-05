package circlegenerator;

import edu.ucsc.cross.hse.core.object.Objects;

public class CircleState extends Objects
{

	public Double translate;
	public Double xValue;
	public Double yValue;

	public CircleState()
	{
		translate = 0.0;
		xValue = 0.0;
		yValue = 0.0;
	}

	public CircleState(Double x, Double y, Double trans)
	{
		translate = trans;
		this.xValue = x;
		this.yValue = y;
	}

}
