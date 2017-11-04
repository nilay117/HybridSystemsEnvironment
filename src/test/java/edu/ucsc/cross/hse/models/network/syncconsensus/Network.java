package edu.ucsc.cross.hse.models.network.syncconsensus;

import Jama.Matrix;
import com.be3short.data.cloning.ObjectCloner;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import java.util.ArrayList;

public class Network extends HybridSystem<NetworkState>
{

	NetworkParams params;

	public Network(NetworkState state, NetworkParams params)
	{
		super(state);
		this.params = params;
	}

	@Override
	public boolean C(NetworkState x)
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean D(NetworkState x)
	{
		// TODO Auto-generated method stub
		return x.eventTimer <= 0.0;
	}

	@Override
	public void F(NetworkState x, NetworkState x_dot)
	{
		x_dot.eventTimer = -1.0;
		for (int i = 0; i < x.nodes.size(); i++)
		{
			NodeState node = x.nodes.get(i);
			NodeState node_dot = x_dot.nodes.get(i);

			node_dot.stateValue = node.controllerValue;
			node_dot.controllerValue = 0.0;

		}
	}

	@Override
	public void G(NetworkState x, NetworkState x_plus)
	{
		Double nextEvent = Math.random() * (params.maximumCommunicationInterval - params.minimumCommunicationInterval)
		+ params.minimumCommunicationInterval;
		x_plus.eventTimer = nextEvent;
		double[][] stateValues = new double[getState().nodes.size()][1];
		Integer i = 0;
		for (NodeState node : x.nodes)
		{
			stateValues[i][0] = x.nodes.get(i).stateValue;
			i++;
		}
		Matrix stateVals = new Matrix(stateValues);
		Matrix lapCopy = (Matrix) ObjectCloner.xmlClone(params.laplacian);
		Matrix newControlVals = lapCopy.times(stateVals);
		i = 0;
		for (NodeState node : x_plus.nodes)
		{

			node.controllerValue = (-params.jumpGain * newControlVals.get(i++, 0));

		}
	}

	public static Network generateRandomNetwork(Integer num_nodes, Double state_range, Double controller_range)
	{
		ArrayList<NodeState> nodez = new ArrayList<NodeState>();
		for (int i = 0; i < num_nodes; i++)
		{
			NodeState node = new NodeState(((Math.random() - Math.random()) * state_range),
			((Math.random() - Math.random()) * controller_range));
			node.info().setName("N" + i);
			nodez.add(node);
		}
		NetworkParams paramz = NetworkParams.getWattsStrogatzParams(-.4, .3, 0.7, 1.5, nodez.size());
		NetworkState state = new NetworkState(nodez, .5);
		Network network = new Network(state, paramz);
		return network;

	}

	public static void main(String args[])
	{

	}
}
