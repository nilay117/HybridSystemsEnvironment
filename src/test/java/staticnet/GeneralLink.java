package staticnet;

import edu.ucsc.cross.hse.core.model.Link;
import edu.ucsc.cross.hse.core.model.LinkProperty;
import edu.ucsc.cross.hse.core.model.Node;
import java.util.Map;

public class GeneralLink implements Link
{

	Node source;
	Node destination;

	public GeneralLink(Node source, Node destination)
	{
		this.source = source;
		this.destination = destination;
	}

	@Override
	public Node getSender()
	{
		// TODO Auto-generated method stub
		return source;
	}

	@Override
	public Node getReceiver()
	{
		// TODO Auto-generated method stub
		return destination;
	}

	@Override
	public Map<LinkProperty<?>, Object> getProperties()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
