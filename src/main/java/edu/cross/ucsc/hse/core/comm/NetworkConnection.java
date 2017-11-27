package edu.cross.ucsc.hse.core.comm;

import java.util.ArrayList;

public interface NetworkConnection
{

	public <P> void transmitData(P data, Object... addresses);

	public ArrayList<Packet<?>> receiveData();

}
