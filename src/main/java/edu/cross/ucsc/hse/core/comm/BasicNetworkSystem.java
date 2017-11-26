package edu.cross.ucsc.hse.core.comm;

public class BasicNetworkSystem extends NetworkSystem<BasicNode>
{

	public BasicNetworkSystem(BasicNetwork topology, BasicNode state)
	{
		super(new BasicNetwork(), state);
	}

	@Override
	public boolean C(BasicNode x)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void F(BasicNode x, BasicNode x_dot)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean D(BasicNode x)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void G(BasicNode x, BasicNode x_plus)
	{
		// TODO Auto-generated method stub

	}

}
