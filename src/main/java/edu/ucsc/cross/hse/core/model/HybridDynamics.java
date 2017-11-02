package edu.ucsc.cross.hse.core.model;

public interface HybridDynamics<X>
{

	/*
	 * The flow set is the set of states that define the continuous domain, which is where the system exhibits discrete
	 * continuous dynamics. This function returns true if the state is in the flow set, or false if not
	 */
	public boolean C(X x);

	/*
	 * The flow map is a mapping that determines the continuous dynamics of the state. This function defines how to
	 * compute the derivatives of the state elements
	 */
	public void F(X x, X x_dot);

	/*
	 * The jump set is the set of states that define the discrete domain, which is where the system exhibits discrete
	 * discrete dynamics. This function returns true if the state is in the jump set, or false if not
	 */
	public boolean D(X x);

	/*
	 * The jump map is a mapping that determines the discrete changes that occur to the state variable. This function
	 * defines how the state values change discretely
	 */
	public void G(X x, X x_plus);

}
