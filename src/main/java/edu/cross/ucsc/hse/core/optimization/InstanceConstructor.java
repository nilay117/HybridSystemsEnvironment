package edu.cross.ucsc.hse.core.optimization;

public interface InstanceConstructor
{

	<X> X createNewInstance(Class<X> instance_class);
}
