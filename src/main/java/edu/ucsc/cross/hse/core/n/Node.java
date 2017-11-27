package edu.ucsc.cross.hse.core.n;

import edu.ucsc.cross.hse.core.model.Packet;
import java.util.ArrayList;

public class Node
{

	private ArrayList<Packet<?>> packetSendQueue;
	private ArrayList<Packet<?>> packetsSent;
	private ArrayList<Packet<?>> packetsReceived;
}
