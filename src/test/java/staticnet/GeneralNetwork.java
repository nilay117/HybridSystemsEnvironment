package staticnet;

import edu.ucsc.cross.hse.core.object.Network;

public class GeneralNetwork extends Network<GeneralNode, GeneralLink>
{

	public GeneralNetwork()
	{
		super(new GeneralNetworkFactory());
	}

}
