package edu.ucsc.cross.hse.models.network.syncconsensus;

import edu.ucsc.cross.hse.core.object.Objects;

public class NodeState extends Objects
{

	public double stateValue;
	public double controllerValue;

	public NodeState()
	{

		stateValue = 0.0;
		controllerValue = 0.0;

	}

	public NodeState(Double state_val, Double control_val)
	{
		stateValue = state_val;
		controllerValue = control_val;

	}
}
