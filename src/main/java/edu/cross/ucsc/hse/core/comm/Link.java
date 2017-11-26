package edu.cross.ucsc.hse.core.comm;

public interface Link
{

	/*
	 * Gets the node that sends messages through the link
	 */
	public Node getSender();

	/*
	 * Gets the node that receives messages through the link
	 */
	public Node getReceiver();

	/*
	 * Gets the properties of the link
	 */
	public <P> P getProperty(Class<P> property_type);
}
