package edu.ucsc.cross.hse.core.framework.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.HashMap;

import edu.ucsc.cross.hse.core.object.domain.HybridTime;

public class SavedValues<T> implements Serializable// Externalizable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5749793001174157212L;
	public HashMap<HybridTime, T> savedHybridValues; // mapping of saved values

	public SavedValues()
	{
		savedHybridValues = new HashMap<HybridTime, T>();
	}

	public SavedValues(HashMap<HybridTime, T> vals)
	{
		savedHybridValues = vals;
	}

	//	@Override
	//	public void writeExternal(ObjectOutput out) throws IOException
	//	{
	//		for (HybridTime valTime : savedHybridValues.keySet())
	//		{
	//			out.writeObject(valTime);
	//			out.writeObject(savedHybridValues.get(valTime));
	//		}
	//
	//	}
	//
	//	@Override
	//	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	//	{
	//		Object readIn = in.readObject();
	//		while (readIn != null)
	//		{
	//			T readInVal = (T) in.readObject();
	//			savedHybridValues.put((HybridTime) readIn, readInVal);
	//			readIn = in.readObject();
	//		}
	//
	//	}
}
