package staticnet;

import edu.ucsc.cross.hse.core.model.NetworkFactory;
import edu.ucsc.cross.hse.core.object.HybridSystem;

public class GeneralNetworkFactory implements NetworkFactory<GeneralNode, GeneralLink>
{

	@Override
	public GeneralLink createEdge(GeneralNode sourceVertex, GeneralNode targetVertex)
	{
		// TODO Auto-generated method stub
		return new GeneralLink(sourceVertex, targetVertex);
	}

	@Override
	public GeneralNode createNode(HybridSystem<?> system)
	{
		// TODO Auto-generated method stub
		return new GeneralNode();
	}

}
