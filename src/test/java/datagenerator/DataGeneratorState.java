package datagenerator;

import edu.ucsc.cross.hse.core.object.Objects;

public class DataGeneratorState extends Objects
{

	public double dataGenerated; // total amount of data generated
	public double timeToNextData; // time until next data generated

	// Constructor that initializes initial state values
	public DataGeneratorState(double data_generated, double time_to_next)
	{
		dataGenerated = data_generated; // set initial data generated
		timeToNextData = time_to_next; // set initial time until next data
	}

	// Constructor that initializes state values to zero
	public DataGeneratorState()
	{
		dataGenerated = 1.0;
		timeToNextData = 0.0;
	}
}
