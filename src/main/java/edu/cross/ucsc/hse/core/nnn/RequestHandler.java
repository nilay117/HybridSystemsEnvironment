package edu.cross.ucsc.hse.core.nnn;

import edu.ucsc.cross.hse.core.n.NetworkRequest;

public interface RequestHandler
{

	Boolean handled(NetworkRequest request);

	Object handleRequest(NetworkRequest request);
}
