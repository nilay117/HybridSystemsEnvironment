package staticnet;

import edu.ucsc.cross.hse.core.model.PacketHeader;

public class BasicPacketHeader implements PacketHeader
{

	Object source;
	Object destination;

	public BasicPacketHeader(Object src, Object dest)
	{
		this.source = src;
		this.destination = dest;
	}

	@Override
	public Object getSourceAddress()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDestinationAddress()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
