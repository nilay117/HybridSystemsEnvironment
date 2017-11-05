package circlegenerator;

import edu.ucsc.cross.hse.core.model.Output;
import edu.ucsc.cross.hse.core.object.HybridSystem;

/*
 * Hybrid system model of a data generator, consisting of a timer that increments a memory state upon each expiration.
 * This is a simple way to emulate a periodic data source such as a sensor or a data query routine.
 */
public class CircleSystem extends HybridSystem<CircleState> implements Output<Double[]>
{

	private CircleParameters parameters;

	// Constructor that loads data generator state and parameters
	public CircleSystem(Double amplitude, Double frequency)
	{
		super(new CircleState(amplitude, 0.0, 0.0));
		this.parameters = new CircleParameters(amplitude, frequency);

	}

	@Override
	public boolean C(CircleState x)
	{

		return true;
	}

	@Override
	public boolean D(CircleState x)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void F(CircleState x, CircleState x_dot)
	{
		x_dot.translate = parameters.frequency * (2 * Math.PI);
		x_dot.xValue = x.yValue;// * (2 * Math.PI);
		x_dot.yValue = -x.xValue;// * (2 * Math.PI);

	}

	@Override
	public void G(CircleState x, CircleState x_plus)
	{
		x_plus = x;
	}

	// sample and hold parameters
	public CircleParameters params()
	{
		return parameters;
	}

	@Override
	public Double[] u()
	{
		// TODO Auto-generated method stub
		return new Double[]
		{ getState().xValue, getState().yValue };
	}

}
