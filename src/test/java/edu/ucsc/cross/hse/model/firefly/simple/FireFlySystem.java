package edu.ucsc.cross.hse.model.firefly.simple;

import edu.ucsc.cross.hse.core.exe.operator.HybridEnvironment;
import edu.ucsc.cross.hse.core.obj.structure.HybridSystem;
import org.apache.commons.math3.special.Gamma;

public class FireFlySystem extends HybridSystem<FireFlySwarmState>
{

	public FireFlyProperties params;

	public FireFlySystem(FireFlySwarmState state, FireFlyProperties params)
	{
		super(state);
		this.params = params;
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean C(FireFlySwarmState x)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void F(FireFlySwarmState x, FireFlySwarmState x_dot)
	{
		for (FireFlyState fly : x_dot.fireFlies)
		{
			fly.glowIntensity = 1.0;
		}

	}

	@Override
	public boolean D(FireFlySwarmState x)
	{
		for (FireFlyState fly : x.fireFlies)
		{
			if (fly.glowIntensity >= params.maximumGlowIntensity)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void G(FireFlySwarmState x, FireFlySwarmState x_plus)
	{
		int i = 0;
		for (FireFlyState fly : x_plus.fireFlies)
		{
			fly.glowIntensity = Gamma.regularizedGammaP(x.fireFlies.get(i++).glowIntensity, params.eConstant);
		}
	}

	public static void main(String args[])
	{
		HybridEnvironment env = new HybridEnvironment();
		FireFlyProperties p = new FireFlyProperties(1.0, .1);
		FireFlySwarmState s = FireFlySwarmState.getRandomizedSwarm(10, p);

		FireFlySystem sys = new FireFlySystem(s, p);
		// sys.getState().fireFlies.get(0).setName(s.getClass().getSimpleName());
		env.addContent(sys);
		env.start(22.0);
		env.openResultView();
	}

}
