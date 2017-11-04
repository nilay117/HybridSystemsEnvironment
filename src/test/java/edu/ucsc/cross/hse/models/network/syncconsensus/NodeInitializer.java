package edu.ucsc.cross.hse.models.network.syncconsensus;

public interface NodeInitializer
{

	public Double getJumpGain();

	public Double getFlowGain();

	public Double getControlInitialValue();

	public Double getStateInitialValue();
}
