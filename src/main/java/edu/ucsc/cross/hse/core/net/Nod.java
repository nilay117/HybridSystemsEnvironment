package edu.ucsc.cross.hse.core.net;

import edu.ucsc.cross.hse.core.network.Packet;
import java.util.List;
import java.util.Map;

public interface Nod
{

	public Object getAddress();

	public List<Packet> getNewReceivedPackets();

	public List<Packet> getAllReceivedPackets();

	public Map<Object, List<Packet>> getAllPendingTransmissions();

	public List<Packet> getPendingTransmissions(Object address);

	public boolean transmitPacket(Packet packet);

}
