package edu.ucsc.cross.hse.core.object;

import com.be3short.obj.access.FieldFinder;
import org.jgrapht.EdgeFactory;

public class Connection implements EdgeFactory<Object, Connection>
{

	private Object origin;
	private Object destination;

	public Object getOrigin()
	{
		return origin;
	}

	public Object getDestination()
	{
		return destination;
	}

	public <S> S getOrigin(Class<S> origin_class)
	{
		try
		{
			if (FieldFinder.containsSuper(origin, origin_class))
			{
				return origin_class.cast(origin);
			}
		} catch (Exception badCast)
		{

		}
		return null;
	}

	public <D> D getDestination(Class<D> destination_class)
	{
		try
		{
			if (FieldFinder.containsSuper(destination, destination_class))
			{
				return destination_class.cast(destination);
			}
		} catch (Exception badCast)
		{

		}
		return null;
	}

	public Connection(Object sourceVertex, Object targetVertex)
	{
		origin = sourceVertex;
		destination = targetVertex;
	}

	@Override
	public Connection createEdge(Object sourceVertex, Object targetVertex)
	{
		// TODO Auto-generated method stub
		return new Connection(sourceVertex, targetVertex);
	}
}
