package edu.ucsc.cross.hybrid.model.vehicle.simplebouncingball;

import edu.ucsc.cross.hse.core.component.constructors.HybridSystem;

public class BouncingBallHybridSystem extends HybridSystem
{

	public final BouncingBallState state;
	public final BouncingBallDynamics dyn;

	public BouncingBallHybridSystem()
	{
		super("Simple Bounc Ball Hybrid System");
		state = new BouncingBallState();
		dyn = new BouncingBallDynamics(state);
	}

	public BouncingBallHybridSystem(BouncingBallState bb_state)
	{
		super("Simple Bn Ball Hybrid System");
		state = bb_state;
		dyn = new BouncingBallDynamics(state);
	}

	@Override
	public void initialize()
	{
		// TODO Auto-generated method stub

	}

}
