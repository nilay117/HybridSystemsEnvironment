package edu.ucsc.cross.hybrid.model.vehicle.simplebouncingball;

import java.util.Arrays;

import edu.ucsc.cross.hybrid.env.core.components.HybridSystem;

public class BouncingBallSystem extends HybridSystem
{

	//public HashMap<Integer, HybridSystem> hellaGenerators;

	public final BouncingBallState state;
	public final BouncingBallDynamics dyn;

	public BouncingBallSystem(BouncingBallState state, String name)
	{
		super(name);
		this.state = state;
		dyn = new BouncingBallDynamics(this.state);
	}

	public BouncingBallSystem(String name)
	{
		super(name);
		state = new BouncingBallState();
		dyn = new BouncingBallDynamics(state);
	}

	public static BouncingBallSystem getBouncingBallSystem(String... name)
	{
		if (name.length == 0)
		{
			return new BouncingBallSystem("Bouncing Ball System");
		} else
		{
			return new BouncingBallSystem(Arrays.asList(name).toString());
		}
	}

	public static BouncingBallSystem getTrackedBouncingBallSystem()
	{
		BouncingBallState state = new BouncingBallState();
		state.saveComponentToFile("./components", "bouncingBallState.xml");
		BouncingBallSystem sys = new BouncingBallSystem(state, "Bouncer");
		//TrajectoryDataSystem trajSys = new TrajectoryDataSystem("Trajectory Tracker", sys.state);
		//sys.addComponent(trajSys, 1);
		return sys;
	}

	public static BouncingBallSystem getSubBouncingBallSystem()
	{
		BouncingBallState state = new BouncingBallState();
		BouncingBallSystem sys = new BouncingBallSystem("Bouncer");
		//sys.prepSys(sys);
		//TrajectoryDataSystem trajSys = new TrajectoryDataSystem("Trajectory Tracker", sys);
		sys.addComponent(sys, 5);
		return sys;
	}

	public static HybridSystem[] getTrackedBouncingBallSystems()
	{
		BouncingBallState state = new BouncingBallState();
		BouncingBallSystem sys = new BouncingBallSystem(state, "Bouncer");
		//TrajectoryDataSystem trajSys = new TrajectoryDataSystem("Trajectory Tracker", sys.state);

		return new HybridSystem[]
		{ sys };
	}

	@Override
	public void initialize()
	{
		state.yPos.initializeValue();
		// TODO Auto-generated method stub
	}

	//	public static void main(String args[])
	//	{
	//		GeneralSimpleSetupMenu menu = new GeneralSimpleSetupMenu();
	//
	//		Environment env = new Environment(new EnvironmentElements("Bouncy Environment"));
	//		BouncingBallSystem sys = new BouncingBallSystem("Bouncer");
	//		TrajectoryDataSystem trajSys = new TrajectoryDataSystem("Trajectory Tracker", sys.state);
	//		//sys.addSubSystem(trajSys);
	//		env.addSystem(sys);
	//		//menu.reset(env);//menu.re(env);
	//	}
}
