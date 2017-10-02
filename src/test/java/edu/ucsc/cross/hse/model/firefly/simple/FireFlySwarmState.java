package edu.ucsc.cross.hse.model.firefly.simple;

import edu.ucsc.cross.hse.core.obj.structure.State;
import java.util.ArrayList;

public class FireFlySwarmState extends State
{

	public ArrayList<FireFlyState> fireFlies;

	public FireFlySwarmState(ArrayList<FireFlyState> fire_flies)
	{
		this.fireFlies = fire_flies;
	}

	public static FireFlySwarmState getRandomizedSwarm(Integer swarm_size, FireFlyProperties props)
	{
		ArrayList<FireFlyState> flies = new ArrayList<FireFlyState>();
		for (int i = 0; i < swarm_size; i++)
		{
			FireFlyState fly = new FireFlyState(Math.random() * props.maximumGlowIntensity);
			fly.setName("Fly " + i);
			flies.add(fly);
		}
		return new FireFlySwarmState(flies);
	}
}
