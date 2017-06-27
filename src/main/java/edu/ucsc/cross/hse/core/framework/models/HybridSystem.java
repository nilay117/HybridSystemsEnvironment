package edu.ucsc.cross.hse.core.framework.models;

/*
 * This interface declares the functions needed to define a hybrid dynamical
 * system based on the framework used by Dr. Sanfelice and other members of
 * Hybrid Systems Lab at the University of California. More details can be found
 * at https://hybrid.soe.ucsc.edu/
 */
public interface HybridSystem
{

	/*
	 * The jump set is the set of states that define the discrete domain, which
	 * is where the system exhibits discrete discrete dynamics. This function
	 * returns true if the state is in the jump set, or false if not
	 */
	public boolean jumpSet();

	/*
	 * The flow set is the set of states that define the continuous domain,
	 * which is where the system exhibits discrete continuous dynamics. This
	 * function returns true if the state is in the flow set, or false if not
	 */
	public boolean flowSet();

	/*
	 * The flow map is a mapping that determines the continuous dynamics of the
	 * state. This function defines how to compute the derivatives of the state
	 * elements
	 */
	public void flowMap();

	/*
	 * The jump map is a mapping that determines the discrete changes that occur
	 * to the state variable. This function defines how the state values change
	 * discretely
	 */
	public void jumpMap();

}
