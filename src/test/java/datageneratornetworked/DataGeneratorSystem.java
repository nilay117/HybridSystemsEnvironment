package datageneratornetworked;

import edu.ucsc.cross.hse.core.model.Node;
import edu.ucsc.cross.hse.core.model.Packet;
import edu.ucsc.cross.hse.core.object.HybridSystem;
import staticnet.BasicPacket;
import staticnet.GeneralLink;

/*
 * Hybrid system model of a data generator, consisting of a timer that increments a memory state upon each expiration.
 * This is a simple way to emulate a periodic data source such as a sensor or a data query routine.
 */
public class DataGeneratorSystem extends HybridSystem<DataGeneratorState>
{

	public DataGeneratorParameters params;
	public Node genNet;

	// Constructor that loads data generator state and parameters
	public DataGeneratorSystem(DataGeneratorState state, DataGeneratorParameters parameters, Node gen_net)
	{
		super(state); // load data generator state and parameters
		params = parameters;
		genNet = gen_net;
	}

	// Checks if data generator state is in flow map
	public boolean C(DataGeneratorState x)
	{
		boolean waiting = x.timeToNextData > 0.0;
		return waiting;
	}

	// Computes continuous dynamics of data generator state
	public void F(DataGeneratorState x, DataGeneratorState x_dot)
	{
		x_dot.dataGenerated = 0.0;
		x_dot.timeToNextData = -1.0;
	}

	// Checks if data generator state is in jump map
	public boolean D(DataGeneratorState x)
	{
		boolean dataGenerated = x.timeToNextData <= 0.0;
		boolean packetReceived = genNet.getReceivedPackets().size() > 0;
		return dataGenerated;
	}

	// Computes discrete dynamics of data generator state
	public void G(DataGeneratorState x, DataGeneratorState x_plus)
	{
		if (x.timeToNextData <= 0.0)
		{
			try
			{
				Double newdat = x.dataGenerated + params().dataItemSize;
				genNet.sendPacket(new BasicPacket<Double>(newdat, this.genNet.getAddress(),
				this.genNet.getLinkedDestinationAddresses()
				.toArray(new GeneralLink[this.genNet.getLinkedDestinationAddresses().size()])[0].getReceiver()
				.getAddress()));// .next().getReceiver().getAddress()));

			} catch (Exception e)
			{
				e.printStackTrace();

			}
			x_plus.timeToNextData = params().generationInterval;
		}
		if (genNet.getReceivedPackets().size() > 0)
		{

			Packet<Double> p = (Packet<Double>) genNet.getReceivedPackets().get(0);
			x_plus.dataGenerated = x.dataGenerated + p.getPayload();
			System.out.println(p.getPayload());
			genNet.getReceivedPackets().remove(0);

		}
	}

	// data generator parameters
	public DataGeneratorParameters params()
	{
		return params;

	}
}
