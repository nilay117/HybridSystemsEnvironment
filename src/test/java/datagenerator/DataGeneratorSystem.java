package datagenerator;

import edu.ucsc.cross.hse.core.object.HybridSystem;

/*
 * Hybrid system model of a data generator, consisting of a timer that increments a memory state upon each expiration.
 * This is a simple way to emulate a periodic data source such as a sensor or a data query routine.
 */
public class DataGeneratorSystem extends HybridSystem<DataGeneratorState>
{

	public DataGeneratorParameters params;

	// Constructor that loads data generator state and parameters
	public DataGeneratorSystem(DataGeneratorState state, DataGeneratorParameters parameters)
	{
		super(state); // load data generator state and parameters
		params = parameters;
	}

	// Checks if data generator state is in flow map
	public boolean C(DataGeneratorState x)
	{
		boolean waiting = x.timeToNextData > 0.0;
		return waiting;
	}

	// Computes continuous dynamics of data generator state
	public void F(DataGeneratorState x, DataGeneratorState x_dot)
	{
		x_dot.dataGenerated = 0.0;
		x_dot.timeToNextData = -1.0;
	}

	// Checks if data generator state is in jump map
	public boolean D(DataGeneratorState x)
	{
		boolean dataGenerated = x.timeToNextData <= 0.0;
		return dataGenerated;
	}

	// Computes discrete dynamics of data generator state
	public void G(DataGeneratorState x, DataGeneratorState x_plus)
	{
		x_plus.dataGenerated = x.dataGenerated + params().dataItemSize;
		x_plus.timeToNextData = params().generationInterval;
	}

	// data generator parameters
	public DataGeneratorParameters params()
	{
		return params;

	}
}
