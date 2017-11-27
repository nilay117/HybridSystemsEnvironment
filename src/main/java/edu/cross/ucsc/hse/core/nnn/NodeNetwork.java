package edu.cross.ucsc.hse.core.nnn;

import edu.cross.ucsc.hse.core.comm.NodeNetworkState;
import edu.ucsc.cross.hse.core.n.NetworkRequest;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import java.util.ArrayList;
import java.util.List;
import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;

public class NodeNetwork extends HybridSystem<NodeNetworkState>
{

	ArrayList<NetworkRequest> pendingRequests;
	ArrayList<NetworkRequest> completedRequests;
	private RequestHandler requestHandler;
	NetworkStructure<HybridSystem<?>, NetworkLink<HybridSystem<?>>> structure;

	public NodeNetwork(NodeNetworkState state)
	{
		super(state);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean C(NodeNetworkState x)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void F(NodeNetworkState x, NodeNetworkState x_dot)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean D(NodeNetworkState x)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void G(NodeNetworkState x, NodeNetworkState x_plus)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void issueRequest(NetworkRequest request)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isRequestComplete(NetworkRequest request)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getRequestResponse(NetworkRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DirectedGraph<Object, NetworkLink<Object>> getStructure()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GraphPath<Object, NetworkLink<Object>>> getAllPaths(Object source, Object destination)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
