package edu.cross.ucsc.hse.core.nnn;

import java.util.ArrayList;

public class NetworkLink<N>
{

	public NetworkNode<N> origin;
	public NetworkNode<N> destination;
	private NetworkTopology<N> network;
	private ArrayList<LinkParameter> parameters;

	public NetworkLink(NetworkTopology<N> network, NetworkNode<N> org, NetworkNode<N> dest)
	{
		this.origin = org;
		this.destination = dest;
	}

	public void addLinkParameter(LinkParameter parameter)
	{
		if (!parameters.contains(parameter))
		{
			parameters.add(parameter);
		}
	}

	public void removeLinkParameter(LinkParameter parameter)
	{
		if (parameters.contains(parameter))
		{
			parameters.remove(parameter);
		}
	}
}
