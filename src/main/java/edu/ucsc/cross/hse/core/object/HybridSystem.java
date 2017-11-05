package edu.ucsc.cross.hse.core.object;

import com.be3short.data.cloning.ObjectCloner;
import edu.ucsc.cross.hse.core.model.HybridDynamics;
import java.util.ArrayList;

// Hybrid system models are extensions of the
// Hybrid System class, which contains
// hidden components that allow the system
// to operate properly
public abstract class HybridSystem<X> implements HybridDynamics<X>
{

	X dynamicState;
	X state;

	public X getState()
	{
		return state;
	}

	public HybridSystem(X state)
	{
		this.state = state;
	}

	public static class EnvironmentAccessor
	{

	}

	public static class HybridSystemOperator
	{

		@SuppressWarnings("unused")
		private HybridSystem<?> sys;

		public ArrayList<HybridSystem<?>> getSubSystems()
		{
			// if (totalSystems.containsKey(sys))
			{
				// return totalSystems.get(sys);
			} // else
			{
				return new ArrayList<HybridSystem<?>>();
			}
		}

		public HybridSystemOperator(HybridSystem<?> sys)
		{
			this.sys = sys;
		}

		public static <T> boolean c(HybridSystem<T> sys)
		{
			return sys.C(sys.state);
		}

		public static <T> boolean d(HybridSystem<T> sys)
		{
			return sys.D(sys.state);
		}

		public static <T> T f(HybridSystem<T> sys)
		{
			if (HybridSystemOperator.c(sys))
			{
				sys.F(sys.state, sys.dynamicState);
			}
			return sys.dynamicState;
		}

		public static <T> T g(HybridSystem<T> sys)
		{
			if (HybridSystemOperator.d(sys))
			{
				sys.G(sys.state, sys.dynamicState);
			}
			return sys.dynamicState;
		}

		public static <T> T getDynamicState(HybridSystem<T> sys)
		{
			return sys.dynamicState;
		}

		public static HybridSystemOperator getOperator(HybridSystem<?> sys)
		{
			return new HybridSystemOperator(sys);
		}

		public static <T> void initializeDynamicState(HybridSystem<T> sys)
		{
			sys.dynamicState = ObjectCloner.deepInstanceClone(sys.getState());
		}

	}

}
