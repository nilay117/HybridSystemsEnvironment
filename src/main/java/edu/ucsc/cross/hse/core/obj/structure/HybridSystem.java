package edu.ucsc.cross.hse.core.obj.structure;

import bs.commons.objects.manipulation.XMLParser;
import com.be3short.data.cloning.ObjectCloner;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.exe.interfacing.ObjectInterface;
import edu.ucsc.cross.hse.core.exe.operator.SystemOperator;
import edu.ucsc.cross.hse.core.obj.format.HybridDynamics;

// @Loggable
public abstract class HybridSystem<X> implements HybridDynamics<X>
{

	private X state;
	private X dynamicState;
	private X inputState;

	public HybridSystem(X state)
	{
		this.state = state;
		// dynamicState = (X) ObjectCloner.xmlClone(this.state);
		// ObjectManager.initializeMap(getState(), getDynamicState());
	}

	// @Loggable(Loggable.TRACE)
	public X getState()
	{
		return state;
	}

	// @Loggable(Loggable.TRACE)
	public void setState(X state)
	{
		this.state = state;
	}

	// @Loggable(Loggable.TRACE)
	public boolean D()
	{
		return D(this.state);
	}

	// @Loggable(Loggable.TRACE)
	public boolean C()
	{
		return C(this.state);
	}

	public X G()
	{
		try
		{
			if (D())
			{
				G(this.state, this.dynamicState);
			}
		} catch (Exception uninit)
		{
			System.out.println(XMLParser.serializeObject(getState()));
			System.out.println(XMLParser.serializeObject(HybridSystem.getDynamicState(this)));

			uninit.printStackTrace();
			// StateClass.initializeMap(getState(), getDynamicState());
			// return getState();
		}
		return HybridSystem.getDynamicState(this);

	}

	public X F()
	{

		if (C())
		{
			// F(getState(), getDynamicState());
			F(this.state, this.dynamicState);

		}

		return this.dynamicState;

	}

	public void initialize()
	{

	}

	public static <T> void initializeDynamicState(HybridSystem<T> sys)
	{
		sys.dynamicState = (T) ObjectCloner.xmlClone(sys.getState());
		sys.inputState = (T) ObjectCloner.xmlClone(sys.getState());
	}

	// @Loggable(Loggable.TRACE)
	public static <T> T getDynamicState(HybridSystem<T> sys)
	{
		return sys.dynamicState;
	}

	// @Loggable(Loggable.TRACE)
	public static <T> T getInputState(HybridSystem<T> sys)
	{
		return sys.inputState;
	}

	// @Loggable(Loggable.TRACE)
	public X getInput()
	{
		return inputState;
	}
}
