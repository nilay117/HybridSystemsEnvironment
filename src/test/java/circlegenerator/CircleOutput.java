package circlegenerator;

import edu.ucsc.cross.hse.core.model.Output;

public class CircleOutput implements Output<Double>
{

	private boolean x;
	private CircleState state;

	public CircleOutput(CircleState state, boolean x)
	{
		this.x = x;
		this.state = state;
	}

	@Override
	public Double u()
	{
		if (x)
		{
			return state.xValue;
		} else
		{
			return state.yValue;
		}

	}

}
