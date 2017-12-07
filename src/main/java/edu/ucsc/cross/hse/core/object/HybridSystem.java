package edu.ucsc.cross.hse.core.object;

import com.be3short.data.cloning.ObjectCloner;
import edu.ucsc.cross.hse.core.container.EnvironmentContents;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.model.HybridDynamics;
import java.util.ArrayList;

// Hybrid system models are extensions of the
// Hybrid System class, which contains
// hidden components that allow the system
// to operate properly
public abstract class HybridSystem<X extends ObjectSet> implements HybridDynamics<X>
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
		state.data().setHistorySaved(true);
		state.data().setSimulated(true);
	}

	public EnvironmentAccessor environment()
	{
		return new EnvironmentAccessor(this);
	}

	public static class EnvironmentAccessor

	{

		private HybridSystem<?> sys;
		private Environment env;

		public EnvironmentAccessor(HybridSystem<?> sys)
		{
			this.sys = sys;
		}

		public Object getAddressObject(Integer ind)
		{
			return EnvironmentContents.getSystem(sys, ind);
		}
	}

	public static class HybridSystemOperator
	{

		@SuppressWarnings("unused")
		private HybridSystem<?> sys;
		private Environment env;

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

		public static <T extends ObjectSet> boolean c(HybridSystem<T> sys)
		{
			return sys.C(sys.state);
		}

		public static <T extends ObjectSet> boolean d(HybridSystem<T> sys)
		{
			return sys.D(sys.state);
		}

		public static <T extends ObjectSet> T f(HybridSystem<T> sys)
		{
			if (HybridSystemOperator.c(sys))
			{
				sys.F(sys.state, sys.dynamicState);
			}
			return sys.dynamicState;
		}

		public static <T extends ObjectSet> T g(HybridSystem<T> sys)
		{
			if (HybridSystemOperator.d(sys))
			{
				sys.G(sys.state, sys.dynamicState);
			}
			return sys.dynamicState;
		}

		public static <T extends ObjectSet> T getDynamicState(HybridSystem<T> sys)
		{
			return sys.dynamicState;
		}

		public static HybridSystemOperator getOperator(HybridSystem<?> sys)
		{
			return new HybridSystemOperator(sys);
		}

		public static <T extends ObjectSet> void initializeDynamicState(HybridSystem<T> sys)
		{
			sys.dynamicState = ObjectCloner.deepInstanceClone(sys.getState());
		}

	}

}
