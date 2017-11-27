package edu.cross.ucsc.hse.core.nnn;

import edu.cross.ucsc.hse.core.comm.NetworkConnection;
import edu.ucsc.cross.hse.core.model.Node;
import edu.ucsc.cross.hse.core.n.NetworkRequest;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DirectedPseudograph;

public abstract class RequestNetwork implements RequestHandler
{

	private ArrayList<NetworkRequest> pendingRequests;

	private HashMap<NetworkRequest, Object> requestResponses;

	private NetworkStructure<Node, NetworkLink<Object>> networkStructure;

	@Override
	public List<GraphPath<V source, V destination)
	{
		return null;
	}

	@Override
	public Boolean handled(NetworkRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object handleRequest(NetworkRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<NetworkRequest> getPendingNetworkRequests(edu.cross.ucsc.hse.core.nnn.V source)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<NetworkRequest> getCompletedNetworkRequests(edu.cross.ucsc.hse.core.nnn.V source)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
