package edu.cross.ucsc.hse.core.comm;

import edu.ucsc.cross.hse.core.n.NetworkRequest;

public interface NetworkRequestModel
{

	public void issueRequest(NetworkRequest request);

	public boolean isRequestComplete(NetworkRequest request);

	public Object getRequestResponse(NetworkRequest request);// ,Object>
}
