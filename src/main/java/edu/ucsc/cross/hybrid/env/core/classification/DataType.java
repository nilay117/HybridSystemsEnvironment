package edu.ucsc.cross.hybrid.env.core.classification;

import edu.ucsc.cross.hybrid.env.core.components.Data;

public interface DataType
{

	public boolean changesContinuously();

	public boolean isStoredByDefault();

	public boolean isState();

	public String baseLabel();

	public <T> Data<T> create(T initial_value);

	public <S> Data<S> create(S initial_value, boolean stored_by_default);

	public <T> Data<T> create(T initial_value, String label);

	public <S> Data<S> create(S initial_value, String label, boolean stored_by_default);

	public <T> Data<T> create(T initial_value, String label, String name);

	public <S> Data<S> create(S initial_value, String label, String name, boolean stored_by_default);

}
