package staticnet;

import edu.ucsc.cross.hse.core.model.Packet;
import edu.ucsc.cross.hse.core.model.PacketHeader;

public class BasicPacket<P> implements Packet<P>
{

	BasicPacketHeader header;
	P object;

	public BasicPacket(P obj, Object src, Object dest)
	{
		this.object = obj;
		this.header = new BasicPacketHeader(src, dest);
	}

	@Override
	public PacketHeader getHeader()
	{
		// TODO Auto-generated method stub
		return header;
	}

	@Override
	public P getPayload()
	{
		// TODO Auto-generated method stub
		return object;
	}

}
